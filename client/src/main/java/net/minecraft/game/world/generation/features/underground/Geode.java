package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.ocean.Crag;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.generation.features.Feature;

public class Geode extends Feature {
    private int metadata = 0;

    public Geode(int meta) {
        this.metadata = meta;
    }

    public boolean withinSphere(int x, int y, int z, int radius) {
        return x * x + y * y + z * z <= radius * radius;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (world.getBlock(x, y, z) != null
            && y >= 8
            && y <= world.getSeaLevel() - 16
            && !(world.getBiomeAt(x, z) instanceof Ocean)
            && !(world.getBiomeAt(x, z) instanceof Crag)) {
            if (y <= world.getBiomeAt(x, z).yOffset) {
                this.drawSphereAbsolute(world, x, y, z, 8, (short)BlockList.stone.id, 2);
            } else {
                this.drawSphereAbsolute(world, x, y, z, 8, (short)BlockList.stone.id, 0);
            }

            this.drawSphereAbsolute(world, x, y, z, 7, (short)BlockList.stone.id, 1);
            this.drawSphereAbsolute(world, x, y, z, 6, (short)BlockList.clusterBlock.id, this.metadata);
            this.drawSphereRandom(world, random, x, y, z, 5, (short)BlockList.crystalBlock.id, this.metadata, 0);
            this.drawSphereAbsolute(world, x, y, z, 4, 0, 0);
            return true;
        } else {
            return false;
        }
    }

    public boolean drawSphereAbsolute(World world, int x0, int y0, int z0, int radius, int blockID, int meta) {
        short b = (short)blockID;
        byte m = (byte)meta;

        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        world.setBlockAndMetadata(x0 + x, y0 + y, z0 + z, b, m);
                        world.setBlockAndMetadata(x0 - x, y0 + y, z0 + z, b, m);
                        world.setBlockAndMetadata(x0 + x, y0 + y, z0 - z, b, m);
                        world.setBlockAndMetadata(x0 - x, y0 + y, z0 - z, b, m);
                        world.setBlockAndMetadata(x0 + x, y0 - y, z0 + z, b, m);
                        world.setBlockAndMetadata(x0 - x, y0 - y, z0 + z, b, m);
                        world.setBlockAndMetadata(x0 + x, y0 - y, z0 - z, b, m);
                        world.setBlockAndMetadata(x0 - x, y0 - y, z0 - z, b, m);
                    }
                }
            }
        }

        return true;
    }

    public boolean drawSphereRandom(World world, Random rnd, int x0, int y0, int z0, int radius, int blockID, int meta, int altId) {
        short b = (short)blockID;
        byte m = (byte)meta;

        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        world.setBlockAndMetadata(x0 + x, y0 + y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 - x, y0 + y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 + x, y0 + y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 - x, y0 + y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 + x, y0 - y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 - x, y0 - y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 + x, y0 - y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        world.setBlockAndMetadata(x0 - x, y0 - y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                    }
                }
            }
        }

        return true;
    }
}
