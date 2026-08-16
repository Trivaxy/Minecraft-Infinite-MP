package net.minecraft.server.player;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.server.world.WorldServer;

public class ItemInWorldManager {
    private WorldServer thisWorld;
    public Player thisPlayer;
    private int newRemovalTime;
    private int activeX;
    private int activeY;
    private int activeZ;
    private int removalTime;
    private boolean isRemoving;
    private int blockPosX;
    private int blockPosY;
    private int blockPosZ;
    private int prevRemovalTime;

    public ItemInWorldManager(WorldServer worldServer1) {
        this.thisWorld = worldServer1;
    }

    public void beginBlockRemoval() {
        this.removalTime++;
        if (this.isRemoving) {
            int i1 = this.removalTime - this.prevRemovalTime;
            int i2 = this.thisWorld.getBlockId(this.blockPosX, this.blockPosY, this.blockPosZ);
            if (i2 != 0) {
                Block block3 = BlockList.blocks[i2];
                float f4 = block3.breakStrength(this.thisPlayer, this.thisWorld.getBlockMetadata(this.blockPosX, this.blockPosY, this.blockPosZ)) * (i1 + 1);
                if (f4 >= 1.0F) {
                    this.isRemoving = false;
                    this.hitBlock(this.blockPosX, this.blockPosY, this.blockPosZ);
                }
            } else {
                this.isRemoving = false;
            }
        }
    }

    public void useBlock(int i1, int i2, int i3, int i4) {
        this.thisWorld.extinguishFire(null, i1, i2, i3, i4);
        this.newRemovalTime = this.removalTime;
        int i5 = this.thisWorld.getBlockId(i1, i2, i3);
        if (i5 > 0) {
            BlockList.blocks[i5].blockPunched(this.thisWorld, i1, i2, i3, this.thisPlayer);
        }

        if (i5 > 0 && BlockList.blocks[i5].breakStrength(this.thisPlayer, this.thisWorld.getBlockMetadata(i1, i2, i3)) >= 1.0F) {
            this.hitBlock(i1, i2, i3);
        } else {
            this.activeX = i1;
            this.activeY = i2;
            this.activeZ = i3;
        }
    }

    public void onBlockRemoving(int i1, int i2, int i3) {
        if (i1 == this.activeX && i2 == this.activeY && i3 == this.activeZ) {
            int i4 = this.removalTime - this.newRemovalTime;
            int i5 = this.thisWorld.getBlockId(i1, i2, i3);
            if (i5 != 0) {
                Block block6 = BlockList.blocks[i5];
                float f7 = block6.breakStrength(this.thisPlayer, this.thisWorld.getBlockMetadata(i1, i2, i3)) * (i4 + 1);
                if (f7 >= 0.7F) {
                    this.hitBlock(i1, i2, i3);
                } else if (!this.isRemoving) {
                    this.isRemoving = true;
                    this.blockPosX = i1;
                    this.blockPosY = i2;
                    this.blockPosZ = i3;
                    this.prevRemovalTime = this.newRemovalTime;
                }
            }
        }
    }

    public boolean removeBlock(int i1, int i2, int i3) {
        Block block4 = BlockList.blocks[this.thisWorld.getBlockId(i1, i2, i3)];
        int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
        boolean z6 = this.thisWorld.setBlockWithNotify(i1, i2, i3, 0);
        if (block4 != null && z6) {
            block4.onBroken(this.thisWorld, i1, i2, i3, i5);
        }

        return z6;
    }

    public boolean hitBlock(int i1, int i2, int i3) {
        int i4 = this.thisWorld.getBlockId(i1, i2, i3);
        int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
        this.thisWorld.playEffect(this.thisPlayer, 2001, i1, i2, i3, i5 << 12 | i4);
        ItemStack itemStack7 = this.thisPlayer.getCurrentEquippedItem();
        if (this.thisPlayer.getUsing()) {
            itemStack7 = this.thisPlayer.inventory.getOffHandItem();
        }

        boolean z8 = this.thisPlayer.canHarvestBlock(BlockList.blocks[i4]);
        if (itemStack7 != null) {
            if (BlockList.blocks[i4].hardness > 0.0F) {
                itemStack7.onDestroyBlock(i4, i1, i2, i3, this.thisPlayer);
            }

            if (itemStack7.stackSize == 0) {
                if (this.thisPlayer.getUsing()) {
                    this.thisPlayer.destroyOffhandItem();
                } else {
                    this.thisPlayer.destroyCurrentEquippedItem();
                }
            }
        }

        boolean z6 = this.removeBlock(i1, i2, i3);
        if (z6) {
            ((EntityPlayerMP)this.thisPlayer).onBlockMined(i4);
        }

        if (z6 && z8) {
            BlockList.blocks[i4].dropItem(this.thisWorld, this.thisPlayer, i1, i2, i3, i5);
            ((EntityPlayerMP)this.thisPlayer).playerNetServerHandler.sendPacket(new UpdateBlockPacket(i1, i2, i3, this.thisWorld));
        }

        return z6;
    }

    public boolean destroyCurrentlyHeldItem(Player entityPlayer1, World world2, ItemStack itemStack3) {
        int i4 = itemStack3.stackSize;
        ItemStack itemStack5 = itemStack3.useItemOnBlock(world2, entityPlayer1);
        if (itemStack5 != itemStack3 || itemStack5 != null && itemStack5.stackSize != i4) {
            if (!entityPlayer1.inventory.canUseItem()) {
                entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentSlot] = itemStack5;
                if (itemStack5.stackSize <= 0) {
                    entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentSlot] = null;
                }
            } else {
                entityPlayer1.inventory.offhandSlot[0] = itemStack5;
                if (itemStack5.stackSize <= 0) {
                    entityPlayer1.inventory.offhandSlot[0] = null;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean activeBlockOrUseItem(Player entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7) {
        int i8 = world2.getBlockId(i4, i5, i6);
        return i8 > 0 && BlockList.blocks[i8].blockActivated(world2, i4, i5, i6, entityPlayer1)
            ? true
            : (itemStack3 == null ? false : itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7));
    }
}
