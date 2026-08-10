package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Shrine extends Feature {
    public static final Shrine INSTANCE = new Shrine();
    public static final int[][][] SCHEMATIC = new int[][][]{
        {
                {16457, 16697, 16697, 16697, 16697, 16687},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 16697, 16697, 16697, 16697, 16687}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 139556, 0, 0, 0, 0},
                {16457, 131364, 0, 0, 0, 0},
                {16457, 131364, 0, 0, 0, 0},
                {16457, 131364, 0, 0, 0, 0},
                {16457, 131364, 0, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 139556, 0, 0, 0, 0},
                {16457, 16457, 16687, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 16687, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 139556, 0, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 0, 0, 0, 0},
                {16457, 16457, 0, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 139556, 0, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 0, 0, 0, 0},
                {16457, 16457, 0, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 139556, 0, 0, 0, 0},
                {16457, 16457, 16687, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 281, 0, 0, 0},
                {16457, 16457, 16687, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 135460, 0, 0, 0, 0},
                {16457, 135460, 0, 0, 0, 0},
                {16457, 135460, 0, 0, 0, 0},
                {16457, 135460, 0, 0, 0, 0},
                {16457, 135460, 0, 0, 0, 0},
                {16457, 143652, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0}
        },
        {
                {16457, 16697, 16697, 16697, 16697, 16687},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 0, 0, 0, 0, 0},
                {16457, 16697, 16697, 16697, 16697, 16687}
        }
    };

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int sizeX = SCHEMATIC.length;
        int sizeZ = SCHEMATIC[0].length;
        int sizeY = SCHEMATIC[0][0].length;
        int offsetX = sizeX / 2;
        int offsetZ = sizeZ / 2;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                for (int dy = 0; dy < sizeY; dy++) {
                    int blockAndMeta = SCHEMATIC[dx][dz][dy];
                    if (blockAndMeta != -1) {
                        int blockId = blockAndMeta & 4095;
                        int metadata = blockAndMeta >> 12 & 0xFF;
                        int targetX = x + dx - offsetX;
                        int targetZ = z + dz - offsetZ;
                        world.setBlockAndMetadata(targetX, y + dy, targetZ, blockId, metadata);

                        for (int i = y; i > 0; i--) {
                            if (world.getBlock(targetX, i, targetZ) == null) {
                                world.setBlockAndMetadata(targetX, i, targetZ, BlockList.stoneBrick.id, 4);
                            }
                        }
                    }
                }
            }
        }

        Seraphim theFinalBossOfTheGameOrSomeShit = new Seraphim(world);
        theFinalBossOfTheGameOrSomeShit.setPosAndRot(x + 0.5F, y + 5, z + 0.5F, 0.0F, 0.0F);
        world.spawnEntity(theFinalBossOfTheGameOrSomeShit);
        return true;
    }
}
