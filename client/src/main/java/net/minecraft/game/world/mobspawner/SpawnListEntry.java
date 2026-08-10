package net.minecraft.game.world.mobspawner;

import net.minecraft.game.entity.mob.Mob;

public class SpawnListEntry {
    public Class<? extends Mob> entityClass;
    public int spawnRate;

    public SpawnListEntry(Class<? extends Mob> class1, int rate) {
        this.entityClass = class1;
        this.spawnRate = rate;
    }
}
