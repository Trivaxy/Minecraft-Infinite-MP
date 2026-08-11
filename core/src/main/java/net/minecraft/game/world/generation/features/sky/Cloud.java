package net.minecraft.game.world.generation.features.sky;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Cloud extends Feature {
    public static final Cloud SMALL_CLOUD = new Cloud(BlockList.cloud.id, 0);
    private int blockID;
    private int meta;

    public Cloud(int id, int meta) {
        this.blockID = id;
        this.meta = meta;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        x -= 8;
        z -= 8;
        y -= 4;
        BitSet set = new BitSet(2048);
        int size = random.nextInt(4) + 4;

        for (int i = 0; i < size; i++) {
            double off1 = random.nextDouble() * 6.0 + 3.0;
            double off2 = random.nextDouble() * 4.0 + 2.0;
            double off3 = random.nextDouble() * 6.0 + 3.0;
            double off4 = random.nextDouble() * (16.0 - off1 - 2.0) + 1.0 + off1 / 2.0;
            double off5 = random.nextDouble() * (8.0 - off2 - 4.0) + 2.0 + off2 / 2.0;
            double off6 = random.nextDouble() * (16.0 - off3 - 2.0) + 1.0 + off3 / 2.0;

            for (int chunkX = 1; chunkX < 15; chunkX++) {
                for (int chunkZ = 1; chunkZ < 15; chunkZ++) {
                    for (int chunkY = 1; chunkY < 7; chunkY++) {
                        double distX = (chunkX - off4) / (off1 / 2.0);
                        double distY = (chunkY - off5) / (off2 / 2.0);
                        double distZ = (chunkZ - off6) / (off3 / 2.0);
                        double dist = distX * distX + distY * distY + distZ * distZ;
                        if (dist < 1.0) {
                            set.set(chunkX << 7 | chunkZ << 3 | chunkY);
                        }
                    }
                }
            }
        }

        for (int chunkX = 1; chunkX < 16; chunkX++) {
            for (int chunkZ = 1; chunkZ < 16; chunkZ++) {
                for (int chunkY = 1; chunkY < 8; chunkY++) {
                    boolean pass = !set.get(chunkX << 7 | chunkZ << 3 | chunkY)
                        && (
                            chunkX < 15 && set.get(chunkX + 1 << 7 | chunkZ << 3 | chunkY)
                                || chunkX > 0 && set.get(chunkX - 1 << 7 | chunkZ << 3 | chunkY)
                                || chunkZ < 15 && set.get(chunkX << 7 | chunkZ + 1 << 3 | chunkY)
                                || chunkZ > 0 && set.get(chunkX << 7 | chunkZ - 1 << 3 | chunkY)
                                || chunkY < 7 && set.get(chunkX << 7 | chunkZ << 3 | chunkY + 1)
                                || chunkY > 0 && set.get(chunkX << 7 | chunkZ << 3 | chunkY - 1)
                        );
                    if (pass && chunkY >= 8 && chunkY < world.getWorldHeight() - 8 && world.getBlock(x + chunkX, y + chunkY, z + chunkZ) != null) {
                        return false;
                    }
                }
            }
        }

        for (int chunkX = 0; chunkX < 16; chunkX++) {
            for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
                for (int chunkY = 0; chunkY < 8; chunkY++) {
                    if (set.get(chunkX << 7 | chunkZ << 3 | chunkY) && world.getBlock(x + chunkX, y + chunkY, z + chunkZ) == null) {
                        world.setBlockAndMetadataWithNotify(x + chunkX, y + chunkY, z + chunkZ, this.blockID, this.meta);
                    }
                }
            }
        }

        return true;
    }
}
