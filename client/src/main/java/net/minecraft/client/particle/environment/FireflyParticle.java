package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class FireflyParticle extends Particle {
    private int courseChangeCooldown = 0;
    private double waypointX;
    private double waypointY;
    private double waypointZ;

    public FireflyParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.textureIndex = 34;
        this.setSize(0.02F, 0.02F);
        this.scale = this.scale * (this.rand.nextFloat() * 0.6F + 0.2F);
        this.motionX *= 0.02F;
        this.motionY *= 0.02F;
        this.motionZ *= 0.02F;
        this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        this.glows = true;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
        double newX = this.waypointX - this.posX;
        double newY = this.waypointY - this.posY;
        double newZ = this.waypointZ - this.posZ;
        this.yaw = (float)(Math.atan2(newZ, newX) * 180.0 / (float) Math.PI) - 90.0F;
        double dist = MathHelper.sqrt_double(newX * newX + newY * newY + newZ * newZ);
        if (dist < 1.0 || dist > 20.0) {
            this.waypointX = this.posX + (this.rand.nextFloat() * 2.0 - 1.0) * 2.0;
            this.waypointY = this.posY + (this.rand.nextFloat() * 2.0 - 1.0) * 2.0;
            this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0 - 1.0) * 2.0;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = this.courseChangeCooldown + this.rand.nextInt(5) + 2;
            if (this.isCourseTraversable(dist)) {
                this.motionX += newX / dist * 0.01;
                this.motionY += newY / dist * 0.01;
                this.motionZ += newZ / dist * 0.01;
            } else {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.maxAge-- <= 0) {
            this.setDead();
        }
    }

    private boolean isCourseTraversable(double dist) {
        double targetX = (this.waypointX - this.posX) / dist;
        double targetY = (this.waypointY - this.posY) / dist;
        double targetZ = (this.waypointZ - this.posZ) / dist;
        BoundingBox hitbox = this.boundingBox.copy();
        if (dist > 256.0) {
            dist = 256.0;
        }

        for (int i = 1; i < dist; i++) {
            hitbox.offset(targetX, targetY, targetZ);
            if (this.world.getBoundingBoxes(this, hitbox).size() > 0) {
                return false;
            }
        }

        return true;
    }
}
