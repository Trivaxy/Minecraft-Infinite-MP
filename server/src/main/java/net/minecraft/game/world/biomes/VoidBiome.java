package net.minecraft.game.world.biomes;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.monster.Stalker;
import net.minecraft.game.world.World;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class VoidBiome extends Biome {
    protected VoidBiome(int biomeID) {
        super(biomeID);
        this.monsters.clear();
        this.animals.clear();
        this.seaCreatures.clear();
        this.monsters.add(new SpawnListEntry(Stalker.class, 8));
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
    }

    @Override
    public void caveBuilder(World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.TOWNAURA, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
