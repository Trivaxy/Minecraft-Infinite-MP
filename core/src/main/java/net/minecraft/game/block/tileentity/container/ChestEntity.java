package net.minecraft.game.block.tileentity.container;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class ChestEntity extends ContainerEntity implements Inventory {
    public ChestEntity chestLeft;
    public ChestEntity chestFront;
    public ChestEntity chestBack;
    public ChestEntity chestRight;
    public boolean adjacentCheck = false;
    public float lidAngle;
    public float prevLidAngle;
    public int players;

    public ChestEntity() {
        this.inventory = new ItemStack[36];
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public double getMaxDistance() {
        return 65536.0;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.chest");
    }

    @Override
    public void reset() {
        super.reset();
        this.adjacentCheck = false;
    }

    public void checkForChests() {
        if (!this.adjacentCheck) {
            this.adjacentCheck = true;
            this.chestLeft = null;
            this.chestFront = null;
            this.chestBack = null;
            this.chestRight = null;
            if (this.world.getBlockId(this.x - 1, this.y, this.z) == BlockList.chest.id) {
                this.chestBack = (ChestEntity)this.world.getTileEntity(this.x - 1, this.y, this.z);
            }

            if (this.world.getBlockId(this.x + 1, this.y, this.z) == BlockList.chest.id) {
                this.chestFront = (ChestEntity)this.world.getTileEntity(this.x + 1, this.y, this.z);
            }

            if (this.world.getBlockId(this.x, this.y, this.z - 1) == BlockList.chest.id) {
                this.chestLeft = (ChestEntity)this.world.getTileEntity(this.x, this.y, this.z - 1);
            }

            if (this.world.getBlockId(this.x, this.y, this.z + 1) == BlockList.chest.id) {
                this.chestRight = (ChestEntity)this.world.getTileEntity(this.x, this.y, this.z + 1);
            }

            if (this.chestLeft != null) {
                this.chestLeft.reset();
            }

            if (this.chestRight != null) {
                this.chestRight.reset();
            }

            if (this.chestFront != null) {
                this.chestFront.reset();
            }

            if (this.chestBack != null) {
                this.chestBack.reset();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForChests();
        this.prevLidAngle = this.lidAngle;
        if (this.players > 0 && this.lidAngle == 0.0F && this.chestLeft == null && this.chestBack == null) {
            double angleX = this.x + 0.5;
            double angleZ = this.z + 0.5;
            if (this.chestRight != null) {
                angleZ += 0.5;
            }

            if (this.chestFront != null) {
                angleX += 0.5;
            }

            this.world.playBlockSound(angleX, this.y + 0.5, angleZ, "random.chest_open", 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.players == 0 && this.lidAngle > 0.0F || this.players > 0 && this.lidAngle < 1.0F) {
            float angle = this.lidAngle;
            if (this.players > 0) {
                this.lidAngle = 1.0F;
            } else {
                this.lidAngle = 0.0F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float maxAngle = 0.5F;
            if (this.lidAngle < maxAngle && angle >= maxAngle && this.chestLeft == null && this.chestBack == null) {
                double angleX = this.x + 0.5;
                double angleZ = this.z + 0.5;
                if (this.chestRight != null) {
                    angleZ += 0.5;
                }

                if (this.chestFront != null) {
                    angleX += 0.5;
                }

                this.world.playBlockSound(angleX, this.y + 0.5, angleZ, "random.chest_close", 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public void open() {
        this.players++;
    }

    @Override
    public void close() {
        this.players--;
    }

    @Override
    public void invalidate() {
        this.reset();
        this.checkForChests();
        super.invalidate();
    }
}
