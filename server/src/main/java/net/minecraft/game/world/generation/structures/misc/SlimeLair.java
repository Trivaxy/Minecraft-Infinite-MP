package net.minecraft.game.world.generation.structures.misc;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.slime.Slime;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.warm.WarmBiome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.util.math.BlockArrayUtils;

public class SlimeLair extends Structure {
    public static final int NUMSPHERES = 40;
    private SlimeLair.SlimeBossLairSphere[] spheres = new SlimeLair.SlimeBossLairSphere[40];
    public Random randSpheres;
    public Random randGeneral;
    public int chunkXb1;
    public int chunkXb2;
    public int chunkZb1;
    public int chunkZb2;
    private boolean spawnedKingSlime;

    public SlimeLair(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        long seed = this.world.getSeed() + originChunkX * 25117 + originChunkZ * 151121;
        this.randSpheres = new Random(seed);
        this.randGeneral = new Random(seed);
        this.spawnedKingSlime = false;
    }

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int i = 0; i < 40; i++) {
            int cX = (int)(this.randSpheres.nextGaussian() * 40.0);
            if (this.randSpheres.nextBoolean()) {
                cX = -cX;
            }

            cX += this.centerX;
            int cZ = (int)(this.randSpheres.nextGaussian() * 40.0);
            if (this.randSpheres.nextBoolean()) {
                cZ = -cZ;
            }

            cZ += this.centerZ;
            int cY = this.randSpheres.nextInt(4) + (this.world.getSeaLevel() - 32);
            int dX = Math.abs(cX - this.centerX);
            int dZ = Math.abs(cZ - this.centerZ);
            int d = Math.max(dX, dZ);
            int radius = 20 - (d >> 1);
            this.spheres[i] = new SlimeLair.SlimeBossLairSphere(cX, cY, cZ, radius);
        }
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof WarmBiome;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.chunkXb1 = chunkX << 4;
        this.chunkXb2 = this.chunkXb1 + 15;
        this.chunkZb1 = chunkZ << 4;
        this.chunkZb2 = this.chunkZb1 + 15;
        BlockArrayUtils blockArrayUtils = new BlockArrayUtils(blockArray, metadata, chunkX, chunkZ);

        for (int i = 0; i < 40; i++) {
            this.carveSphere(this.world, this.spheres[i].x, this.spheres[i].y, this.spheres[i].z, this.spheres[i].r, 0, blockArray, blockArrayUtils);
        }
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        if (chunkX == this.originChunkX && chunkZ == this.originChunkZ && !this.spawnedKingSlime) {
            this.spawnedKingSlime = true;
            Slime slimeKing = new Slime(this.world, 16);
            slimeKing.setPosAndRot(chunkX << 4, this.world.getSeaLevel() - 32, chunkZ << 4, 0.0F, 0.0F);
            this.world.spawnEntity(slimeKing);
        }

        if (chunkX <= this.originChunkX + 1 && chunkZ <= this.originChunkZ + 1 && chunkX >= this.originChunkX - 1 && chunkZ >= this.originChunkZ - 1) {
            int x0 = chunkX << 4;
            int z0 = chunkZ << 4;

            for (int i = 0; i < 8; i++) {
                int x = x0 + rand.nextInt(8) + 4;
                int z = z0 + rand.nextInt(8) + 4;
                int y = rand.nextInt(world.getSeaLevel() / 4) + 64;
                OreLump.mud.place(world, rand, x, y, z);
                x = x0 + rand.nextInt(8) + 4;
                z = z0 + rand.nextInt(8) + 4;
                y = rand.nextInt(world.getSeaLevel() / 4) + 64;
                OreLump.moss.place(world, rand, x, y, z);
                x = x0 + rand.nextInt(16) + 8;
                z = z0 + rand.nextInt(16) + 8;
                y = rand.nextInt(world.getSeaLevel() / 4) + 64;
                FlowerPatch.glowshrooms.place(world, rand, x, y, z);
                x = x0 + rand.nextInt(16) + 8;
                z = z0 + rand.nextInt(16) + 8;
                y = rand.nextInt(world.getSeaLevel() / 4) + 64;
                FlowerPatch.glowLychen.place(world, rand, x, y, z);
            }

            for (int i = 0; i < 100; i++) {
                int x = x0 + rand.nextInt(16) + 8;
                int z = z0 + rand.nextInt(16) + 8;
                int y = rand.nextInt(48) + world.getBiomeAt(x, z).yOffset;
                new BigMushroom(3).place(world, rand, x, y, z);
            }

            for (int i = 0; i < 16; i++) {
                int x = x0 + rand.nextInt(16);
                int z = z0 + rand.nextInt(16);
                int y = rand.nextInt(world.getSeaLevel() / 4) + 64;
                if (world.isSolid(x, y - 1, z) && world.getBlockId(x, y, z) == 0 && y > 74) {
                    world.setBlock(x, y, z, BlockList.mobSpawner.id);
                    if (world.getTileEntity(x, y, z) != null) {
                        ((SpawnerEntity)world.getTileEntity(x, y, z)).setMob("Slime");
                    }
                }
            }
        }
    }

    public boolean carveSphere(World world, int x0, int y0, int z0, int radius, int id, short[] data, BlockArrayUtils blockArrayUtils) {
        int testX = x0;
        int testZ = z0;
        if (x0 <= this.chunkXb1) {
            testX = this.chunkXb1;
        } else if (x0 >= this.chunkXb2) {
            testX = this.chunkXb2;
        }

        if (z0 <= this.chunkZb1) {
            testZ = this.chunkZb1;
        } else if (z0 >= this.chunkZb2) {
            testZ = this.chunkZb2;
        }

        int dx = x0 - testX;
        int dz = z0 - testZ;
        int distanceSquared = dx * dx + dz * dz;
        if (distanceSquared > radius * radius) {
            return false;
        }

        blockArrayUtils.drawSphereAbsolute(x0, y0, z0, radius, id, 0);
        return true;
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public int getSpawnChance() {
        return 10;
    }

    @Override
    public int minimumSeparation() {
        return 16;
    }

    public class SlimeBossLairSphere {
        public final int x;
        public final int y;
        public final int z;
        public final int r;

        public SlimeBossLairSphere(int x, int y, int z, int r) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }
    }
}
