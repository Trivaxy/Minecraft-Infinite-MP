package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Pillar extends Feature {
    public static Pillar INSTANCE;
    private int id;

    public Pillar(int id) {
        this.id = id;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (world.isAir(x, y, z) && world.getBlockId(x, y - 1, z) == this.id) {
            int height = random.nextInt(32) + 6;
            int width = random.nextInt(4) + 1;

            for (int x0 = x - width; x0 <= x + width; x0++) {
                for (int z0 = z - width; z0 <= z + width; z0++) {
                    int radX = x0 - x;
                    int radZ = z0 - z;
                    if (radX * radX + radZ * radZ <= width * width + 1 && world.getBlockId(x0, y - 1, z0) != this.id) {
                        return false;
                    }
                }
            }

            for (int y0 = y; y0 < y + height && y0 < 128; y0++) {
                for (int var14 = x - width; var14 <= x + width; var14++) {
                    for (int z0 = z - width; z0 <= z + width; z0++) {
                        int radX = var14 - x;
                        int radZ = z0 - z;
                        if (radZ * radZ + radX * radX <= width * width + 1) {
                            world.setBlockAndMetadataWithNotify(var14, y0, z0, BlockList.stoneBrick.id, 4);
                        }
                    }
                }
            }

            VoidCrystal theCrystal = new VoidCrystal(world);
            theCrystal.setPosAndRot(x + 0.5F, y + height, z + 0.5F, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(theCrystal);
            world.setBlockWithNotify(x, y + height, z, BlockList.bedrock.id);
            return true;
        } else {
            return false;
        }
    }

    static {
        INSTANCE = new Pillar(BlockList.voidRock.id);
    }
}
