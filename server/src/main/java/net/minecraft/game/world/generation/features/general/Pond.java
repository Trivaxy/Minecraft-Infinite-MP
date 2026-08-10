package net.minecraft.game.world.generation.features.general;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.dimension.Overworld;
import net.minecraft.game.world.generation.features.Feature;

public class Pond extends Feature {
    public static final Pond WATER = new Pond(BlockList.waterStill.id);
    public static final Pond LAVA = new Pond(BlockList.lavaStill.id);
    private int blockId;

    public Pond(int blockId) {
        this.blockId = blockId;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        Biome biome = world.getBiomeAt(x, z);
        x -= 8;
        z -= 8;

        while (y > 0 && world.isAir(x, y, z)) {
            y--;
        }

        y -= 4;
        BitSet set = new BitSet(2048);
        int height = random.nextInt(4) + 4;

        for (int they = 0; they < height; they++) {
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
                        double distTotal = distX * distX + distY * distY + distZ * distZ;
                        if (distTotal < 1.0) {
                            set.set(chunkX << 7 | chunkZ << 3 | chunkY);
                        }
                    }
                }
            }
        }

        for (int var36 = 1; var36 < 16; var36++) {
            for (int pondWidth = 1; pondWidth < 16; pondWidth++) {
                for (int pondHeight = 1; pondHeight < 8; pondHeight++) {
                    boolean pass = !set.get(var36 << 7 | pondWidth << 3 | pondHeight)
                        && (
                            var36 < 15 && set.get(var36 + 1 << 7 | pondWidth << 3 | pondHeight)
                                || var36 > 0 && set.get(var36 - 1 << 7 | pondWidth << 3 | pondHeight)
                                || pondWidth < 15 && set.get(var36 << 7 | pondWidth + 1 << 3 | pondHeight)
                                || pondWidth > 0 && set.get(var36 << 7 | pondWidth - 1 << 3 | pondHeight)
                                || pondHeight < 7 && set.get(var36 << 7 | pondWidth << 3 | pondHeight + 1)
                                || pondHeight > 0 && set.get(var36 << 7 | pondWidth << 3 | pondHeight - 1)
                        );
                    if (pass) {
                        Material material12 = world.getBlockMaterial(x + var36, y + pondHeight, z + pondWidth);
                        if (pondHeight >= 4 && material12.getIsLiquid()) {
                            return false;
                        }

                        if (pondHeight < 4
                            && (!material12.isSolid() || material12 == MaterialList.ice)
                            && world.getBlockId(x + var36, y + pondHeight, z + pondWidth) != this.blockId) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int var37 = 0; var37 < 16; var37++) {
            for (int pondWidth = 0; pondWidth < 16; pondWidth++) {
                for (int pondHeight = 0; pondHeight < 8; pondHeight++) {
                    if (set.get(var37 << 7 | pondWidth << 3 | pondHeight)) {
                        world.setBlockWithNotify(x + var37, y + pondHeight, z + pondWidth, pondHeight >= 4 ? 0 : this.blockId);
                    }
                }
            }
        }

        if (this.blockId > 0 && BlockList.blocks[this.blockId].material == MaterialList.lava) {
            for (int var38 = 0; var38 < 16; var38++) {
                for (int pondWidth = 0; pondWidth < 16; pondWidth++) {
                    for (int pondHeight = 0; pondHeight < 8; pondHeight++) {
                        boolean pass = !set.get(var38 << 7 | pondWidth << 3 | pondHeight)
                            && (
                                var38 < 15 && set.get(var38 + 1 << 7 | pondWidth << 3 | pondHeight)
                                    || var38 > 0 && set.get(var38 - 1 << 7 | pondWidth << 3 | pondHeight)
                                    || pondWidth < 15 && set.get(var38 << 7 | pondWidth + 1 << 3 | pondHeight)
                                    || pondWidth > 0 && set.get(var38 << 7 | pondWidth - 1 << 3 | pondHeight)
                                    || pondHeight < 7 && set.get(var38 << 7 | pondWidth << 3 | pondHeight + 1)
                                    || pondHeight > 0 && set.get(var38 << 7 | pondWidth << 3 | pondHeight - 1)
                            );
                        if (pass && (pondHeight < 4 || random.nextInt(2) != 0) && world.getBlockMaterial(x + var38, y + pondHeight, z + pondWidth).isSolid()) {
                            world.setBlockWithNotify(
                                x + var38, y + pondHeight, z + pondWidth, world.currDim instanceof HellDimension ? BlockList.magma.id : BlockList.stone.id
                            );
                            if (y <= biome.yOffset && world.currDim instanceof Overworld) {
                                world.setBlockMetadataWithNotify(x + var38, y + pondHeight, z + pondWidth, 2);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
