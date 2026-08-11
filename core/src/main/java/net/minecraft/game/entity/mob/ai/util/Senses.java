package net.minecraft.game.entity.mob.ai.util;

import java.util.ArrayList;
import net.minecraft.Profiler;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;

public class Senses {
    private Mob mob;
    private ArrayList<Entity> cachePositive = new ArrayList<>();
    private ArrayList<Entity> cacheNegative = new ArrayList<>();

    public Senses(Mob mob) {
        this.mob = mob;
    }

    public void clearSensingCache() {
        this.cachePositive.clear();
        this.cacheNegative.clear();
    }

    public boolean canSee(Entity target) {
        if (this.cachePositive.contains(target)) {
            return true;
        }

        if (this.cacheNegative.contains(target)) {
            return false;
        }

        Profiler.startSection("canSee");
        boolean visible = this.mob.isVisible(target);
        Profiler.endSection();
        if (visible) {
            this.cachePositive.add(target);
        } else {
            this.cacheNegative.add(target);
        }

        return visible;
    }

    protected static float updateRotation(float yaw, float ogYaw, float max) {
        float newYaw = ogYaw - yaw;

        while (newYaw < -180.0F) {
            newYaw += 360.0F;
        }

        while (newYaw >= 180.0F) {
            newYaw -= 360.0F;
        }

        if (newYaw > max) {
            newYaw = max;
        }

        if (newYaw < -max) {
            newYaw = -max;
        }

        return yaw + newYaw;
    }
}
