package net.minecraft.game.world.mobspawner;

import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.mob.animal.aquatic.AquaticMob;
import net.minecraft.game.entity.mob.monster.Monster;

public enum SpawnCategory {
    monster(Monster.class, 70, MaterialList.air, false),
    creature(Animal.class, 15, MaterialList.air, true),
    aquatic(AquaticMob.class, 5, MaterialList.water, true);

    private final Class<? extends Entity> creatureClass;
    private final int maxSpawns;
    private final Material spawnMaterial;
    private final boolean animal;

    SpawnCategory(Class<? extends Entity> creature, int max, Material material, boolean animal) {
        this.creatureClass = creature;
        this.maxSpawns = max;
        this.spawnMaterial = material;
        this.animal = animal;
    }

    public Class<? extends Entity> getCreatureClass() {
        return this.creatureClass;
    }

    public int getMaxSpawns() {
        return this.maxSpawns;
    }

    public Material getSpawnMaterial() {
        return this.spawnMaterial;
    }

    public boolean animal() {
        return this.animal;
    }
}
