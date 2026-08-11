package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Glowstone extends Feature {
    public static final Glowstone INSTANCE = new Glowstone();

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        if (world.isAir(x, y, z) && world.isSolid(x, y + 1, z)) {
            world.setBlockWithNotify(x, y, z, BlockList.glowStone.id);

            for (int attempts = 0; attempts < 1500; attempts++) {
                int xOff = x + rand.nextInt(8) - rand.nextInt(8);
                int yOff = y - rand.nextInt(12);
                int zOff = z + rand.nextInt(8) - rand.nextInt(8);
                if (world.getBlockId(xOff, yOff, zOff) == 0) {
                    int chance = 0;

                    for (int iteration = 0; iteration < 6; iteration++) {
                        int id = 0;
                        if (iteration == 0) {
                            id = world.getBlockId(xOff - 1, yOff, zOff);
                        }

                        if (iteration == 1) {
                            id = world.getBlockId(xOff + 1, yOff, zOff);
                        }

                        if (iteration == 2) {
                            id = world.getBlockId(xOff, yOff - 1, zOff);
                        }

                        if (iteration == 3) {
                            id = world.getBlockId(xOff, yOff + 1, zOff);
                        }

                        if (iteration == 4) {
                            id = world.getBlockId(xOff, yOff, zOff - 1);
                        }

                        if (iteration == 5) {
                            id = world.getBlockId(xOff, yOff, zOff + 1);
                        }

                        if (id == BlockList.glowStone.id) {
                            chance++;
                        }
                    }

                    if (chance == 1) {
                        world.setBlockWithNotify(xOff, yOff, zOff, BlockList.glowStone.id);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
