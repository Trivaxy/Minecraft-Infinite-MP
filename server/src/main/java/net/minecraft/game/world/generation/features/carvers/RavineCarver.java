package net.minecraft.game.world.generation.features.carvers;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.ocean.Crag;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.game.world.generation.features.LargeFeature;
import net.minecraft.util.math.MathHelper;

public class RavineCarver extends LargeFeature {
    private final float[] maxSize = new float[1024];

    protected void generateRavine(
        long randomSeed,
        int originalX,
        int originalZ,
        short[] chunkData,
        byte[] metadata,
        double posX,
        double posY,
        double posZ,
        float dirX,
        float dirY,
        float dirZ,
        int left,
        int right,
        double width
    ) {
        Random rand = new Random(randomSeed);
        double chunkX = originalX << 4 | 8;
        double chunkZ = originalZ << 4 | 8;
        float recurisveX = 0.0F;
        float recursiveZ = 0.0F;
        if (right <= 0) {
            int rng = (this.range << 4) - 16;
            right = rng - rand.nextInt(rng >>> 2);
        }

        boolean turn = false;
        if (left == -1) {
            left = right >>> 1;
            turn = true;
        }

        float offset = 1.0F;
        int height = 0;

        for (int index = 0; index < 256; index++) {
            if (index == 0 || rand.nextInt(3) == 0) {
                offset = 1.0F + rand.nextFloat() * rand.nextFloat() * 1.0F;
            }

            this.maxSize[index] = offset * offset;
        }

        while (height < this.world.getWorldHeight()) {
            height++;
        }

        for (; left < right; left++) {
            double w = 1.5 + MathHelper.sin(left * (float) Math.PI / right) * dirX * 1.0F;
            double d = w * width;
            w *= rand.nextFloat() * 0.25 + 0.75;
            d *= rand.nextFloat() * 0.25 + 0.75;
            float cos = MathHelper.cos(dirZ);
            float sin = MathHelper.sin(dirZ);
            posX += MathHelper.cos(dirY) * cos;
            posY += sin;
            posZ += MathHelper.sin(dirY) * cos;
            dirZ *= 0.7F;
            dirZ += recursiveZ * 0.05F;
            dirY += recurisveX * 0.05F;
            recursiveZ *= 0.8F;
            recurisveX *= 0.5F;
            recursiveZ += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
            recurisveX += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4.0F;
            if (turn || rand.nextInt(4) != 0) {
                double distX = posX - chunkX;
                double distZ = posZ - chunkZ;
                double diff = right - left;
                double range = dirX + 2.0F + 16.0F;
                if (distX * distX + distZ * distZ - diff * diff > range * range) {
                    return;
                }

                if (posX >= chunkX - 16.0 - w * 2.0 && posZ >= chunkZ - 16.0 - w * 2.0 && posX <= chunkX + 16.0 + w * 2.0 && posZ <= chunkZ + 16.0 + w * 2.0) {
                    int startX = MathHelper.floor_double(posX - w) - (originalX << 4) - 1;
                    int endX = MathHelper.floor_double(posX + w) - (originalX << 4) + 1;
                    int startY = MathHelper.floor_double(posY - d) - 1;
                    int endY = MathHelper.floor_double(posY + d) + 1;
                    int startZ = MathHelper.floor_double(posZ - w) - (originalZ << 4) - 1;
                    int endZ = MathHelper.floor_double(posZ + w) - (originalZ << 4) + 1;
                    if (startX < 0) {
                        startX = 0;
                    }

                    if (endX > 16) {
                        endX = 16;
                    }

                    if (startY < 1) {
                        startY = 1;
                    }

                    if (endY > this.world.getWorldHeight() - 8) {
                        endY = this.world.getWorldHeight() - 8;
                    }

                    if (startZ < 0) {
                        startZ = 0;
                    }

                    if (endZ > 16) {
                        endZ = 16;
                    }

                    boolean suddenStop = false;
                    boolean fillWithWater = false;

                    for (int x = startX; !suddenStop && x < endX; x++) {
                        for (int z = startZ; !suddenStop && z < endZ; z++) {
                            for (int y = endY + 1; !suddenStop && y >= startY - 1; y--) {
                                int id = y << 8 | z << 4 | x;
                                if (y >= 0 && y < this.world.getWorldHeight()) {
                                    if (chunkData[id] == BlockList.waterMoving.id
                                        || chunkData[id] == BlockList.waterStill.id
                                        || chunkData[z] == BlockList.lavaStill.id
                                        || chunkData[id] == BlockList.lavaMoving.id) {
                                        if (!(this.world.getBiomeAt(x, z) instanceof Ocean)
                                            && (!(this.world.getBiomeAt(x, z) instanceof Crag) || y >= this.world.getSeaLevel())) {
                                            suddenStop = true;
                                        } else {
                                            fillWithWater = true;
                                        }
                                    }

                                    if (this.world.currDim instanceof HellDimension
                                        && (chunkData[id] == BlockList.lavaMoving.id || chunkData[id] == BlockList.lavaStill.id)) {
                                        suddenStop = true;
                                    }

                                    if (y != startY - 1 && x != startX && x != endX - 1 && z != startZ && z != endZ - 1) {
                                        y = startY;
                                    }
                                }
                            }
                        }
                    }

                    if (!suddenStop) {
                        for (int x = startX; x < endX; x++) {
                            double finalX = ((originalX << 4 | x) + 0.5 - posX) / w;

                            for (int z = startZ; z < endZ; z++) {
                                double finalZ = ((originalZ << 4 | z) + 0.5 - posZ) / w;
                                int id = endY << 8 | z << 4 | x;
                                boolean end = false;
                                if (finalX * finalX + finalZ * finalZ < 1.0) {
                                    for (int hght = endY - 1; hght >= startY; hght--) {
                                        double finalY = (hght + 0.5 - posY) / d;
                                        if ((finalX * finalX + finalZ * finalZ) * this.maxSize[hght] + finalY * finalY / 6.0 < 1.0) {
                                            short carveThruBlock = chunkData[id];
                                            Biome biome = this.world.getBiomeAt(x | originalX << 4, z | originalZ << 4);
                                            if (carveThruBlock == biome.topBlock) {
                                                end = true;
                                            }

                                            int yMax = 10;
                                            if (fillWithWater) {
                                                yMax = this.world.getSeaLevel() / 2 + this.world.getSeaLevel() / 8;
                                            }

                                            if (carveThruBlock == BlockList.stone.id
                                                || carveThruBlock == biome.fillerBlock
                                                || carveThruBlock == biome.topBlock
                                                || carveThruBlock == BlockList.sandStone.id
                                                || carveThruBlock == BlockList.snowBlock.id
                                                || carveThruBlock == BlockList.moss.id
                                                || carveThruBlock == BlockList.packedIce.id
                                                || carveThruBlock == BlockList.clusterBlock.id
                                                || carveThruBlock == BlockList.quickSand.id
                                                || carveThruBlock == BlockList.powderSnow.id
                                                || carveThruBlock == BlockList.dirt.id
                                                || carveThruBlock == BlockList.netherrack.id
                                                || carveThruBlock == BlockList.slowSand.id
                                                || carveThruBlock == BlockList.sand.id
                                                || carveThruBlock == BlockList.redSand.id
                                                || carveThruBlock == BlockList.silverSand.id
                                                || carveThruBlock == BlockList.snowLayer.id
                                                || carveThruBlock == BlockList.voidRock.id
                                                || carveThruBlock == BlockList.voidGrass.id
                                                || carveThruBlock == BlockList.snowBlock.id) {
                                                if (hght < yMax && !(this.world.currDim instanceof VoidDimension)) {
                                                    if (fillWithWater) {
                                                        chunkData[id] = (short)BlockList.magma.id;
                                                    } else {
                                                        chunkData[id] = (short)BlockList.lavaStill.id;
                                                    }

                                                    metadata[id] = 0;
                                                } else {
                                                    if (fillWithWater) {
                                                        chunkData[id] = (short)BlockList.waterStill.id;
                                                    } else {
                                                        chunkData[id] = 0;
                                                    }

                                                    metadata[id] = 0;
                                                    if (end && chunkData[id - 256] == BlockList.dirt.id) {
                                                        chunkData[id - 256] = biome.topBlock;
                                                        metadata[id - 256] = biome.topMeta;
                                                    }
                                                }
                                            }
                                        }

                                        id -= 256;
                                    }
                                }
                            }
                        }

                        if (turn) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originalX, int originalZ, short[] chunkData, byte[] metadata) {
        if (rand.nextInt(50) == 0) {
            double x = chunkX << 4 | rand.nextInt(16);
            double y = rand.nextInt(rand.nextInt(128) + 8) + 20;
            double z = chunkZ << 4 | rand.nextInt(16);
            float offY = rand.nextFloat() * (float) Math.PI * 2.0F;
            float offZ = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float offX = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
            this.generateRavine(rand.nextLong(), originalX, originalZ, chunkData, metadata, x, y, z, offX, offY, offZ, 0, 0, 3.0);
        }
    }
}
