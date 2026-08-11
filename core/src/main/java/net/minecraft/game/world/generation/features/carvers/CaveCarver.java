package net.minecraft.game.world.generation.features.carvers;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.ocean.FloatingIsland;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.generation.features.LargeFeature;
import net.minecraft.util.math.MathHelper;

public class CaveCarver extends LargeFeature {
    protected void generateLargeCaveNode(int originalX, int originalZ, short[] blocks, byte[] metadata, double posX, double posY, double posZ) {
        this.generateCaveNode(
            originalX, originalZ, blocks, metadata, posX, posY, posZ, 1.0F + rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5, new Random(rand.nextLong())
        );
    }

    protected void generateCaveNode(
        int originalX,
        int originalZ,
        short[] blocks,
        byte[] metadata,
        double posX,
        double posY,
        double posZ,
        float dirX,
        float dirY,
        float dirZ,
        int left,
        int right,
        double tunnelWidth,
        Random rand
    ) {
        double offX = originalX << 4 | 8;
        double offZ = originalZ << 4 | 8;
        float recursiveX = 0.0F;
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

        int facing = left < (right >>> 2) * 3 ? left + rand.nextInt(right - left >>> 1) + (right - left >>> 2) : -1;
        boolean pass = rand.nextInt(6) == 0;

        while (left < right) {
            double widthVariation = 1.5 + MathHelper.sin(left * (float) Math.PI / right) * dirX * 1.0F;
            if (posY > this.world.getSeaLevel() && this.world.getBiomeAt((int)posX, (int)posZ) instanceof FloatingIsland
                || posY < this.world.getSeaLevel() / 2 && this.world.settings.getType() != WorldType.OLD) {
                widthVariation = 3.5 + MathHelper.sin(left * (float) Math.PI / right) * dirX * 1.0F;
            }

            double finalWidth = widthVariation * tunnelWidth;
            float cos = MathHelper.cos(dirZ);
            float sin = MathHelper.sin(dirZ);
            posX += MathHelper.cos(dirY) * cos;
            posY += sin;
            posZ += MathHelper.sin(dirY) * cos;
            if (pass) {
                dirZ *= 0.92F;
            } else {
                dirZ *= 0.7F;
            }

            dirZ += recursiveZ * 0.1F;
            dirY += recursiveX * 0.1F;
            recursiveZ *= 0.9F;
            recursiveX *= 0.75F;
            recursiveZ += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
            recursiveX += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4.0F;
            if (!turn && left == facing && dirX > 1.0F) {
                this.generateCaveNode(
                    originalX,
                    originalZ,
                    blocks,
                    metadata,
                    posX,
                    posY,
                    posZ,
                    rand.nextFloat() * 0.5F + 0.5F,
                    dirY - (float) (Math.PI / 2),
                    dirZ / 3.0F,
                    left,
                    right,
                    1.0,
                    new Random(rand.nextLong())
                );
                this.generateCaveNode(
                    originalX,
                    originalZ,
                    blocks,
                    metadata,
                    posX,
                    posY,
                    posZ,
                    rand.nextFloat() * 0.5F + 0.5F,
                    dirY + (float) (Math.PI / 2),
                    dirZ / 3.0F,
                    left,
                    right,
                    1.0,
                    new Random(rand.nextLong())
                );
                return;
            }

            if (turn || rand.nextInt(4) != 0) {
                double distX = posX - offX;
                double distZ = posZ - offZ;
                double offset = right - left;
                double add = dirX + 2.0F + 16.0F;
                if (distX * distX + distZ * distZ > (offset + add) * (offset + add)) {
                    return;
                }

                if (posX >= offX - 16.0 - widthVariation * 2.0
                    && posZ >= offZ - 16.0 - widthVariation * 2.0
                    && posX <= offX + 16.0 + widthVariation * 2.0
                    && posZ <= offZ + 16.0 + widthVariation * 2.0) {
                    int startX = MathHelper.floor_double(posX - widthVariation) - (originalX << 4) - 1;
                    int endX = MathHelper.floor_double(posX + widthVariation) - (originalX << 4) + 1;
                    int startY = MathHelper.floor_double(posY - finalWidth) - 1;
                    int endY = MathHelper.floor_double(posY + finalWidth) + 1;
                    int startZ = MathHelper.floor_double(posZ - widthVariation) - (originalZ << 4) - 1;
                    int endZ = MathHelper.floor_double(posZ + widthVariation) - (originalZ << 4) + 1;
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

                    for (int x = startX; !suddenStop && x < endX; x++) {
                        for (int z = startZ; !suddenStop && z < endZ; z++) {
                            for (int y = endY + 1; !suddenStop && y >= startY - 1; y--) {
                                int id = y << 8 | z << 4 | x;
                                if (y >= 0 && y < this.world.getWorldHeight()) {
                                    if (blocks[id] == BlockList.waterStill.id || blocks[id] == BlockList.waterMoving.id) {
                                        suddenStop = true;
                                    }

                                    if (this.world.currDim instanceof HellDimension
                                        && (blocks[id] == BlockList.lavaMoving.id || blocks[id] == BlockList.lavaStill.id)) {
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
                            double finalX = ((x | originalX << 4) + 0.5 - posX) / widthVariation;

                            for (int z = startZ; z < endZ; z++) {
                                double finalZ = ((z | originalZ << 4) + 0.5 - posZ) / widthVariation;
                                int id = endY << 8 | z << 4 | x;
                                boolean end = false;

                                for (int y = endY - 1; y >= startY; y--) {
                                    double finalY = (y + 0.5 - posY) / finalWidth;
                                    if (finalY > -0.7 && finalX * finalX + finalY * finalY + finalZ * finalZ < 1.0) {
                                        short carveThruBlock = blocks[id];
                                        Biome biome = this.world.getBiomeAt(x | originalX << 4, z | originalZ << 4);
                                        if (carveThruBlock == biome.topBlock) {
                                            end = true;
                                        }

                                        if (carveThruBlock == BlockList.stone.id
                                            || carveThruBlock == BlockList.sandStone.id
                                            || carveThruBlock == BlockList.clusterBlock.id
                                            || carveThruBlock == BlockList.moss.id
                                            || carveThruBlock == biome.fillerBlock
                                            || carveThruBlock == BlockList.packedIce.id
                                            || carveThruBlock == biome.topBlock
                                            || carveThruBlock == BlockList.dirt.id
                                            || carveThruBlock == BlockList.netherrack.id
                                            || carveThruBlock == BlockList.snowLayer.id
                                            || carveThruBlock == BlockList.voidGrass.id
                                            || carveThruBlock == BlockList.voidRock.id
                                            || carveThruBlock == BlockList.snowBlock.id) {
                                            if (y <= 5) {
                                                blocks[id] = (short)BlockList.lavaMoving.id;
                                                metadata[id] = 0;
                                            } else {
                                                blocks[id] = 0;
                                                if (end && blocks[id - 256] == BlockList.dirt.id) {
                                                    blocks[id - 256] = biome.topBlock;
                                                    metadata[id - 256] = biome.topMeta;
                                                }
                                            }
                                        }
                                    }

                                    id -= 256;
                                }
                            }
                        }

                        if (turn) {
                            break;
                        }
                    }
                }
            }

            left++;
        }
    }

    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originalX, int originalZ, short[] blocks, byte[] metadata) {
        int attempts = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1);
        if (rand.nextInt(15) != 0) {
            attempts = 0;
        }

        for (int attempt = 0; attempt < attempts; attempt++) {
            double x = chunkX << 4 | rand.nextInt(16);
            double y = rand.nextInt(rand.nextInt(this.world.getWorldHeight() - 8) + 8);
            double z = chunkZ << 4 | rand.nextInt(16);
            int iterations = 1;
            if (rand.nextInt(4) == 0) {
                this.generateLargeCaveNode(originalX, originalZ, blocks, metadata, x, y, z);
                iterations += rand.nextInt(4);
            }

            for (int i = 0; i < iterations; i++) {
                float offY = rand.nextFloat() * (float) Math.PI * 2.0F;
                float offZ = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float offX = rand.nextFloat() * 2.0F + rand.nextFloat();
                this.generateCaveNode(originalX, originalZ, blocks, metadata, x, y, z, offX, offY, offZ, 0, 0, 1.0, new Random(rand.nextLong()));
            }
        }
    }
}
