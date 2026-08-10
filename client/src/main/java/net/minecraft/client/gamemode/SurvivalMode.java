package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class SurvivalMode extends GameMode {
    protected float curBlockDamage = 0.0F;
    protected float prevBlockDamage = 0.0F;
    protected float blockSoundCounter = 0.0F;
    protected int blockHitDelay;

    public SurvivalMode(Minecraft minecraft) {
        super(minecraft);
        if (minecraft.player != null) {
            minecraft.player.flying = false;
        }
    }

    @Override
    public void swapItem(int id, int meta) {
        this.mc.player.inventory.swapFromInventory(id, meta);
    }

    @Override
    public boolean removeBlock(int x, int y, int z, int side) {
        int id = this.mc.world.getBlockId(x, y, z);
        int meta = this.mc.world.getBlockMetadata(x, y, z);
        boolean canHarvest = this.mc.player.canHarvestBlock(BlockList.blocks[id]);
        ItemStack heldItem = this.mc.player.getCurrentEquippedItem();
        if (this.mc.player.getUsing()) {
            heldItem = this.mc.player.inventory.getOffHandItem();
        }

        if (heldItem != null) {
            if (BlockList.blocks[id].hardness > 0.0F) {
                heldItem.onDestroyBlock(side, x, y, z, this.mc.player);
            }

            if (heldItem.stackSize == 0) {
                if (this.mc.player.getUsing()) {
                    this.mc.player.destroyOffhandItem();
                } else {
                    this.mc.player.destroyCurrentEquippedItem();
                }
            }

            if (heldItem.getItem() == ItemList.gauntletUriel) {
                canHarvest = false;
            }
        }

        if (canHarvest) {
            BlockList.blocks[id].dropItem(this.mc.world, this.mc.player, x, y, z, meta);
        }

        return super.removeBlock(x, y, z, side);
    }

    @Override
    public void clickBlock(int x, int y, int z, int side) {
        this.mc.world.extinguishFire(this.mc.player, x, y, z, side);
        int i5 = this.mc.world.getBlockId(x, y, z);
        if (i5 > 0 && this.curBlockDamage == 0.0F) {
            BlockList.blocks[i5].blockPunched(this.mc.world, x, y, z, this.mc.player);
        }

        if (i5 > 0 && BlockList.blocks[i5].breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z)) >= 1.0F) {
            this.removeBlock(x, y, z, side);
        }
    }

    @Override
    public void resetBreakTime() {
        this.curBlockDamage = 0.0F;
        this.blockHitDelay = 0;
    }

    @Override
    public void updateBreakTime(int x, int y, int z, int side) {
        int id = this.mc.world.getBlockId(x, y, z);
        int meta = this.mc.world.getBlockMetadata(x, y, z);
        if (this.blockHitDelay > 0) {
            this.blockHitDelay--;
        } else if (x == this.hitX && y == this.hitY && z == this.hitZ) {
            if (id == 0 || this.mc.player.isBlocking()) {
                return;
            }

            Block block = BlockList.blocks[id];
            this.curBlockDamage = this.curBlockDamage + block.breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z));
            if (this.blockSoundCounter % 4.0F == 0.0F && block != null) {
                StepSound snd = block.getStepSound(meta);
                this.mc.sndManager.playBlockSound(snd.getSound(), x + 0.5F, y + 0.5F, z + 0.5F, (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch() * 0.5F);
            }

            if (OptionValues.particleCount < 2) {
                this.mc.particleRenderer.spawnBlockHitParticles(x, y, z, Minecraft.getMinecraft().selectedObject.sideHit);
            }

            this.blockSoundCounter++;
            if (this.curBlockDamage >= 1.0F) {
                this.removeBlock(x, y, z, side);
                this.curBlockDamage = 0.0F;
                this.prevBlockDamage = 0.0F;
                this.blockSoundCounter = 0.0F;
                this.blockHitDelay = 5;
            }
        } else {
            this.curBlockDamage = 0.0F;
            this.prevBlockDamage = 0.0F;
            this.blockSoundCounter = 0.0F;
            this.hitX = x;
            this.hitY = y;
            this.hitZ = z;
        }
    }

    @Override
    public void setBlockBreakTime(float time) {
        if (this.curBlockDamage <= 0.0F) {
            this.mc.gameRenderer.blocks.blockBreakTime = 0.0F;
        } else {
            float offset = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * time;
            this.mc.gameRenderer.blocks.blockBreakTime = offset;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.prevBlockDamage = this.curBlockDamage;
    }
}
