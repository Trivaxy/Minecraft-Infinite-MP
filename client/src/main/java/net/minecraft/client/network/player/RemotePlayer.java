package net.minecraft.client.network.player;

import net.minecraft.client.player.SkinDownloader;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class RemotePlayer extends Player {
    private int posIncrements;
    private double newPosX;
    private double newPosY;
    private double newPosZ;
    private double newRotYaw;
    private double newRotPitch;
    private boolean itemInUse = false;

    public RemotePlayer(World world, String name) {
        super(world);
        this.username = name;
        this.yOffset = 0.0F;
        this.stepHeight = 0.0F;
        this.noClip = true;
        if (name != null && name.length() > 0) {
            this.updateSkin();
        }
    }

    @Override
    public boolean inRange(double distance) {
        double renderDist = this.boundingBox.getLength();
        renderDist *= 640.0;
        return distance < renderDist * renderDist;
    }

    @Override
    protected void resetHeight() {
        this.yOffset = 0.0F;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        return true;
    }

    @Override
    public void teleportInServer(double x, double y, double z, float yaw, float pitch, int increments) {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotYaw = yaw;
        this.newRotPitch = pitch;
        this.posIncrements = increments;
    }

    @Override
    public void onTick() {
        super.onTick();
        this.prevLimbYaw = this.limbYaw;
        double x = this.posX - this.prevX;
        double z = this.posZ - this.prevZ;
        float dist = MathHelper.sqrt_double(x * x + z * z) * 4.0F;
        if (dist > 1.0F) {
            dist = 1.0F;
        }

        this.limbYaw = this.limbYaw + (dist - this.limbYaw) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbYaw;
        this.onGround = this.world.getBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, -0.1, 0.0)).size() > 0;
        this.updateFootstep(x, z);
        if (!this.itemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentSlot] != null) {
            ItemStack stack = this.inventory.mainInventory[this.inventory.currentSlot];
            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentSlot], ItemList.items[stack.itemID].getMaxUseDuration(stack));
            this.itemInUse = true;
        } else if (this.itemInUse && !this.isEating()) {
            this.clearItemInUse();
            this.itemInUse = false;
        }
    }

    @Override
    public void mobTick() {
        if (this.posIncrements > 0) {
            double x = this.posX + (this.newPosX - this.posX) / this.posIncrements;
            double y = this.posY + (this.newPosY - this.posY) / this.posIncrements;
            double z = this.posZ + (this.newPosZ - this.posZ) / this.posIncrements;
            double rot = this.newRotYaw - this.headFacing;

            while (rot < -180.0) {
                rot += 360.0;
            }

            while (rot >= 180.0) {
                rot -= 360.0;
            }

            this.headFacing = (float)(this.headFacing + rot / this.posIncrements);
            this.pitch = (float)(this.pitch + (this.newRotPitch - this.pitch) / this.posIncrements);
            this.posIncrements--;
            this.setPosition(x, y, z);
            this.setRotation(this.headFacing, this.pitch);
        }

        this.prevCamYaw = this.camYaw;
        float camYaw = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float camPitch = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
        if (camYaw > 0.1F) {
            camYaw = 0.1F;
        }

        if (!this.onGround || this.health <= 0) {
            camYaw = 0.0F;
        }

        if (this.onGround || this.health <= 0) {
            camPitch = 0.0F;
        }

        this.camYaw = this.camYaw + (camYaw - this.camYaw) * 0.4F;
        this.camPitch = this.camPitch + (camPitch - this.camPitch) * 0.8F;
    }

    @Override
    public void equipItem(int slot, int id, int meta) {
        ItemStack stack = null;
        if (id >= 0) {
            stack = new ItemStack(id, 1, meta);
        }

        if (slot == 0) {
            this.inventory.mainInventory[this.inventory.currentSlot] = stack;
        } else if (slot == 5) {
            this.inventory.offhandSlot[0] = stack;
        } else {
            this.inventory.armorSlots[slot - 1] = stack;
        }
    }

    @Override
    public void setCrawling(boolean state) {
        if (state) {
            this.setSize(0.6F, 0.9F);
        } else {
            this.setSize(0.6F, 1.8F);
        }
    }

    @Override
    public void mountEntity(Entity entity) {
        super.mountEntity(entity);
        if (entity != null) {
            this.yOffset = 1.62F;
        } else {
            this.yOffset = 0.0F;
        }
    }

    @Override
    public void updateSkin() {
        if (this.skinUrl == null && !this.foundSkin) {
            new SkinDownloader(this).start();
        }

        if (this.isSlim) {
            this.texture = "/mob/player/alex.png";
        }
    }
}
