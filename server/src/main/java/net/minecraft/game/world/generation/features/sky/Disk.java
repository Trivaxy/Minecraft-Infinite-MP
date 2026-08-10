package net.minecraft.game.world.generation.features.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Disk extends Feature {
    public static final Disk INSTANCE;
    public static final Disk PLATFORM;
    private int id;

    public Disk(int id) {
        this.id = id;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int size = random.nextInt(6) + 2;

        for (int x0 = x - size; x0 <= x + size; x0++) {
            for (int z0 = z - size; z0 <= z + size; z0++) {
                int xDiff = x0 - x;
                int zDiff = z0 - z;
                int radiusSq = size * size;
                if (xDiff * xDiff + zDiff * zDiff + random.nextInt(4) <= radiusSq) {
                    world.setBlock(x0, y, z0, this.id);
                }
            }
        }

        return true;
    }

    static {
        INSTANCE = new Disk(BlockList.quickSoil.id);
        PLATFORM = new Disk(BlockList.obsidian.id);
    }
}
