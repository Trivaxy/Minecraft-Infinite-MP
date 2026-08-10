package net.minecraft.util.random;

import java.util.Random;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.math.MathHelper;

public class RandomPositionGenerator {
    private static Vector randomPosition = Vector.createVector(0.0, 0.0, 0.0);

    public static Vector findRandomTarget(Mob mob, int x, int z) {
        return findRandomTargetBlock(mob, x, z, (Vector)null);
    }

    public static Vector findRandomTarget(Mob mob, int x, int z, Vector vec) {
        randomPosition.xCoord = vec.xCoord - mob.posX;
        randomPosition.yCoord = vec.yCoord - mob.posY;
        randomPosition.zCoord = vec.zCoord - mob.posZ;
        return findRandomTargetBlock(mob, x, z, randomPosition);
    }

    public static Vector findRandomTargetAwayFrom(Mob mob, int x, int z, Vector vec) {
        randomPosition.xCoord = mob.posX - vec.xCoord;
        randomPosition.yCoord = mob.posY - vec.yCoord;
        randomPosition.zCoord = mob.posZ - vec.zCoord;
        return findRandomTargetBlock(mob, x, z, randomPosition);
    }

    private static Vector findRandomTargetBlock(Mob mob, int x, int z, Vector vec) {
        Random random = mob.rand;
        boolean success = false;
        int posX = 0;
        int posY = 0;
        int posZ = 0;
        float max = -99999.0F;
        boolean tooFar;
        if (mob.hasTarget()) {
            double dist = mob.getHomePosition()
                    .getDistanceSq(MathHelper.floor_double(mob.posX), MathHelper.floor_double(mob.posY), MathHelper.floor_double(mob.posZ))
                + 4.0;
            tooFar = dist < mob.getMaxTrackDist() + x;
        } else {
            tooFar = false;
        }

        for (int i = 0; i < 10; i++) {
            int offX = random.nextInt(2 * x) - x;
            int offZ = random.nextInt(2 * z) - z;
            int whar = random.nextInt(2 * x) - x;
            if (vec == null || offX * vec.xCoord + whar * vec.zCoord >= 0.0) {
                offX += MathHelper.floor_double(mob.posX);
                offZ += MathHelper.floor_double(mob.posY);
                whar += MathHelper.floor_double(mob.posZ);
                if (!tooFar || mob.withinTrackingDist(offX, offZ, whar)) {
                    float wght = mob.getPathWeight(offX, offZ, whar);
                    if (wght > max) {
                        max = wght;
                        posX = offX;
                        posY = offZ;
                        posZ = whar;
                        success = true;
                    }
                }
            }
        }

        return success ? Vector.createVector(posX, posY, posZ) : null;
    }
}
