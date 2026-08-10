package net.minecraft.game.entity.mob.ai.util;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.util.math.MathHelper;

public class FlightControl extends MoveControl {
    private boolean collision = true;
    private int courseChange;

    public FlightControl(Mob mob) {
        super(mob);
    }

    public FlightControl(Mob mob, boolean col) {
        super(mob);
        this.collision = col;
    }

    @Override
    public void tick() {
        if (this.isMoving) {
            double distX = this.posX - this.entity.posX;
            double distY = this.posY - this.entity.posY;
            double distZ = this.posZ - this.entity.posZ;
            double dist = distX * distX + distY * distY + distZ * distZ;
            if (this.courseChange-- <= 0) {
                this.courseChange = this.courseChange + this.entity.rand.nextInt(5) + 2;
                dist = MathHelper.sqrt_double(dist);
                if (this.isTraversible(this.posX, this.posY, this.posZ, dist) && this.waterCheck()) {
                    this.entity.motionX = this.entity.motionX + distX / dist * this.speed;
                    this.entity.motionY = this.entity.motionY + distY / dist * this.speed;
                    this.entity.motionZ = this.entity.motionZ + distZ / dist * this.speed;
                } else {
                    this.isMoving = false;
                }
            }
        }
    }

    private boolean waterCheck() {
        return this.entity.canBreatheUnderwater() ? this.entity.isInWater() : true;
    }

    private boolean isTraversible(double x, double y, double z, double dist) {
        double distX = (x - this.entity.posX) / dist;
        double distY = (y - this.entity.posY) / dist;
        double distZ = (z - this.entity.posZ) / dist;
        BoundingBox boundingBox = this.entity.boundingBox.copy();

        for (int i = 1; i < dist; i++) {
            boundingBox = boundingBox.offset(distX, distY, distZ);
            if (!this.entity.world.getBoundingBoxes(this.entity, boundingBox).isEmpty() && this.collision) {
                return false;
            }
        }

        return true;
    }
}
