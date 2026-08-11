package net.minecraft.game.entity.mob.ai.passive;

import java.util.Random;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class FleeSun extends Action {
    private Mob host;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private float friction;
    private World theWorld;

    public FleeSun(Mob creature, float f) {
        this.host = creature;
        this.friction = f;
        this.theWorld = creature.world;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        if (!this.theWorld.isRaining()
            && this.theWorld.isDaytime()
            && this.host.attackTarget == null
            && this.theWorld.isBlockExposed(MathHelper.floor_double(this.host.posX), (int)this.host.boundingBox.minY, MathHelper.floor_double(this.host.posZ))) {
            Vector safeLocation = this.findSafeLocation();
            if (safeLocation == null) {
                return false;
            }

            this.shelterX = safeLocation.xCoord;
            this.shelterY = safeLocation.yCoord;
            this.shelterZ = safeLocation.zCoord;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.host.navigator.noPath();
    }

    @Override
    public void start() {
        this.host.navigator.moveToLocation(this.shelterX, this.shelterY, this.shelterZ, this.friction);
    }

    private Vector findSafeLocation() {
        Random randomizer = this.host.rand;

        for (int range = 0; range < 10; range++) {
            int x = MathHelper.floor_double(this.host.posX + randomizer.nextInt(20) - 10.0);
            int y = MathHelper.floor_double(this.host.boundingBox.minY + randomizer.nextInt(6) - 3.0);
            int z = MathHelper.floor_double(this.host.posZ + randomizer.nextInt(20) - 10.0);
            if (!this.theWorld.isBlockExposed(x, y, z) && this.host.getPathWeight(x, y, z) < 0.0F) {
                return Vector.createVector(x, y, z);
            }
        }

        return null;
    }
}
