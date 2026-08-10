package net.minecraft.client.network.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.SurvivalMode;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.UsePacket;

public class MultiplayerMode extends SurvivalMode {
    private ClientHandler networkHandler;
    private boolean isHittingBlock = false;
    private int currentPlayerItem = 0;

    public MultiplayerMode(Minecraft minecraft, ClientHandler networkHandler) {
        super(minecraft);
        this.networkHandler = networkHandler;
    }

    @Override
    public boolean removeBlock(int x, int y, int z, int side) {
        int id = this.mc.world.getBlockId(x, y, z);
        ItemStack heldItem = this.mc.player.getCurrentEquippedItem();
        if (this.mc.player.getUsing()) {
            heldItem = this.mc.player.inventory.getOffHandItem();
        }

        if (heldItem != null) {
            if (BlockList.blocks[id].hardness > 0.0F) {
                heldItem.onDestroyBlock(id, x, y, z, this.mc.player);
            }

            if (heldItem.stackSize == 0) {
                if (this.mc.player.getUsing()) {
                    this.mc.player.destroyOffhandItem();
                } else {
                    this.mc.player.destroyCurrentEquippedItem();
                }
            }
        }

        return super.removeBlock(x, y, z, side);
    }

    @Override
    public void clickBlock(int x, int y, int z, int side) {
        if (!this.isHittingBlock || x != this.hitX || y != this.hitY || z != this.hitZ) {
            this.networkHandler.sendPacket(new DigPacket(0, x, y, z, side));
            int id = this.mc.world.getBlockId(x, y, z);
            if (id > 0 && this.curBlockDamage == 0.0F) {
                BlockList.blocks[id].blockPunched(this.mc.world, x, y, z, this.mc.player);
            }

            if (id > 0 && BlockList.blocks[id].breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z)) >= 1.0F) {
                this.removeBlock(x, y, z, side);
            } else {
                this.isHittingBlock = true;
                this.hitX = x;
                this.hitY = y;
                this.hitZ = z;
                this.curBlockDamage = 0.0F;
                this.prevBlockDamage = 0.0F;
                this.blockSoundCounter = 0.0F;
            }
        }
    }

    @Override
    public void resetBreakTime() {
        this.curBlockDamage = 0.0F;
        this.isHittingBlock = false;
    }

    @Override
    public void updateBreakTime(int x, int y, int z, int side) {
        if (this.isHittingBlock || this.mc.player.getUsing()) {
            this.syncCurrentPlayItem();
            if (this.blockHitDelay > 0) {
                this.blockHitDelay--;
            } else if (x == this.hitX && y == this.hitY && z == this.hitZ) {
                int id = this.mc.world.getBlockId(x, y, z);
                int meta = this.mc.world.getBlockMetadata(x, y, z);
                if (id == 0) {
                    this.isHittingBlock = false;
                    return;
                }

                Block block6 = BlockList.blocks[id];
                this.curBlockDamage = this.curBlockDamage + block6.breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z));
                if (this.blockSoundCounter % 4.0F == 0.0F && block6 != null) {
                    StepSound snd = block6.getStepSound(meta);
                    this.mc.sndManager.playBlockSound(snd.getSound(), x + 0.5F, y + 0.5F, z + 0.5F, (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch() * 0.5F);
                }

                this.blockSoundCounter++;
                if (this.curBlockDamage >= 1.0F) {
                    this.isHittingBlock = false;
                    int type = 2;
                    if (this.mc.player.getUsing()) {
                        type = 6;
                    }

                    this.networkHandler.sendPacket(new DigPacket(type, x, y, z, side));
                    this.removeBlock(x, y, z, side);
                    this.curBlockDamage = 0.0F;
                    this.prevBlockDamage = 0.0F;
                    this.blockSoundCounter = 0.0F;
                    this.blockHitDelay = 5;
                }
            } else {
                this.clickBlock(x, y, z, side);
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.syncCurrentPlayItem();
    }

    private void syncCurrentPlayItem() {
        if (this.mc.player.inventory.currentSlot != this.currentPlayerItem) {
            this.currentPlayerItem = this.mc.player.inventory.currentSlot;
            this.networkHandler.sendPacket(new ItemSwitchPacket(this.currentPlayerItem));
        }
    }

    @Override
    public void swapHands(ItemStack currentItem, ItemStack offHandItem) {
        super.swapHands(currentItem, offHandItem);
        this.networkHandler.sendPacket(new ItemSwitchPacket(44));
    }

    @Override
    public boolean useItemOnBlock(Player player, World world, ItemStack item, int x, int y, int z, int side) {
        this.syncCurrentPlayItem();
        this.networkHandler.sendPacket(new PlacePacket(x, y, z, side, player.inventory.getCurrentItem()));
        return super.useItemOnBlock(player, world, item, x, y, z, side);
    }

    @Override
    public boolean useItem(Player player, World world, ItemStack item) {
        this.syncCurrentPlayItem();
        this.networkHandler.sendPacket(new PlacePacket(-1, -1, -1, 255, player.inventory.getCurrentItem()));
        return super.useItem(player, world, item);
    }

    @Override
    public Player createPlayer(World world) {
        return new NetworkPlayer(this.mc, world, this.mc.session, this.networkHandler);
    }

    @Override
    public void attackEntity(Player player, Entity entity) {
        this.syncCurrentPlayItem();
        this.networkHandler.sendPacket(new UsePacket(player.entityId, entity.entityId, 1, this.mc.player.getUsing()));
        player.attackEntityWithItem(entity);
    }

    @Override
    public void interactWithEntity(Player player, Entity entity) {
        this.syncCurrentPlayItem();
        this.networkHandler.sendPacket(new UsePacket(player.entityId, entity.entityId, 0, this.mc.player.getUsing()));
        player.useItemOnEntity(entity);
    }

    @Override
    public ItemStack windowClick(int windowId, int slot, int click, boolean shift, Player player) {
        short transId = player.openContainer.updateTransaction(player.inventory);
        ItemStack stack = super.windowClick(windowId, slot, click, shift, player);
        this.networkHandler.sendPacket(new ClickPacket(windowId, slot, click, shift, stack, transId));
        return stack;
    }

    @Override
    public void closeScreen(int id, Player player) {
        this.networkHandler.sendPacket(new ContainerClosePacket(player.openContainer.windowId));
        super.closeScreen(id, player);
    }

    @Override
    public void stopUsingItem(Player player) {
        this.syncCurrentPlayItem();
        if (this.networkHandler != null) {
            this.networkHandler.sendPacket(new DigPacket(5, 0, 0, 0, 255));
        }

        super.stopUsingItem(player);
    }
}
