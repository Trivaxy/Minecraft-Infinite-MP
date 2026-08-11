package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class SpikeFeature extends Feature {
    public static final SpikeFeature INSTANCE = new SpikeFeature();
    public static final SpikeFeature UNDERGROUND = new SpikeFeature().placeAnywhere(false).generateUnderground(true).height(8, 20).radius(3, 4);
    public static final SpikeFeature VOLCANIC = new SpikeFeature().block(1, 2).placeAnywhere(false).generateUnderground(true).height(8, 20).radius(3, 4);
    public static final SpikeFeature STALACTITE = new SpikeFeature().block(1, 7).placeAnywhere(false).generateUnderground(true).height(8, 20).radius(3, 4);
    public static final SpikeFeature GLOWSTONE = new SpikeFeature()
        .block(BlockList.glowStone.id, 0)
        .generateUnderground(true)
        .height(8, 20)
        .radius(3, 4)
        .skew(1.0F);
    public static final SpikeFeature GEMS = new SpikeFeature()
        .block(BlockList.clusterBlock.id, 3)
        .generateUnderground(true)
        .height(8, 20)
        .radius(3, 4)
        .skew(1.0F);
    public static final SpikeFeature ICESTONE = new SpikeFeature().block(1, 6).placeAnywhere(false).generateUnderground(true).height(8, 20).radius(3, 4);
    public static final SpikeFeature ICE = new SpikeFeature()
        .block(BlockList.packedIce.id, 0)
        .placeAnywhere(false)
        .height(8, 20)
        .radius(6, 8)
        .skew(1.0F)
        .blockPlacement(BlockList.snowBlock.id);
    public static final SpikeFeature ICE2 = new SpikeFeature()
        .block(BlockList.packedIce.id, 0)
        .placeAnywhere(false)
        .height(8, 20)
        .radius(6, 8)
        .skew(1.0F)
        .blockPlacement(BlockList.snowLayer.id);
    public static final SpikeFeature VOID = new SpikeFeature()
        .block(BlockList.voidRock.id, 0)
        .placeAnywhere(false)
        .height(8, 16)
        .blockPlacement(BlockList.voidRock.id)
        .radius(3, 4);
    public static final SpikeFeature SKY = new SpikeFeature()
        .block(BlockList.stone.id, 6)
        .placeAnywhere(false)
        .height(8, 16)
        .blockPlacement(BlockList.stone.id)
        .radius(4, 5)
        .skew(1.0F);
    private int block = 1;
    private int metadata = 0;
    private float skew = 0.0F;
    private int baseHeight = 4;
    private int heightRange = 14;
    private int baseRadius = 3;
    private int radiusRange = 6;
    private boolean placeAnywhere = true;
    private boolean generateUnderground = false;
    private int blockPlacement = 1;

    public SpikeFeature block(int block, int metadata) {
        this.block = block;
        this.metadata = metadata;
        return this;
    }

    public SpikeFeature skew(float skew) {
        this.skew = skew;
        return this;
    }

    public SpikeFeature placeAnywhere(boolean placement) {
        this.placeAnywhere = placement;
        return this;
    }

    public SpikeFeature generateUnderground(boolean placement) {
        this.generateUnderground = placement;
        return this;
    }

    public SpikeFeature blockPlacement(int placement) {
        this.blockPlacement = placement;
        return this;
    }

    public SpikeFeature height(int base, int range) {
        this.baseHeight = base;
        this.heightRange = range;
        return this;
    }

    public SpikeFeature radius(int base, int range) {
        this.baseRadius = base;
        this.radiusRange = range;
        return this;
    }

    @Override
    public boolean place(World world, Random random, int x0, int y0, int z0) {
        int height = random.nextInt(this.heightRange) + this.baseHeight;
        int radius = random.nextInt(this.radiusRange) + this.baseRadius;
        float radiusSquare = radius * radius;
        float skewX = (random.nextFloat() - 0.5F) * this.skew;
        float skewZ = (random.nextFloat() - 0.5F) * this.skew;
        int[] groundArray = new int[(radius * 2 + 1) * (radius * 2 + 1)];

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (this.generateUnderground) {
                    groundArray[(x + radius) * (radius * 2 + 1) + z + radius] = y0;
                } else {
                    groundArray[(x + radius) * (radius * 2 + 1) + z + radius] = world.getTopTileSafe(x + x0, z + z0);
                }

                int ground = groundArray[(x + radius) * (radius * 2 + 1) + z + radius];
                if (this.placeAnywhere && !world.isSolid(x + x0, ground - 1, z + z0)) {
                    return false;
                }

                if (!this.placeAnywhere && world.getBlockId(x + x0, ground - 1, z + z0) != this.blockPlacement) {
                    return false;
                }
            }
        }

        for (int x = -radius - 8; x <= radius + 8; x++) {
            for (int z = -radius - 8; z <= radius + 8; z++) {
                float distSquare = x * x + z * z;
                float slope = 1.0F - distSquare / radiusSquare;
                if (!(slope < 0.0F)) {
                    if (slope > 1.0F) {
                        slope = 1.0F;
                    }

                    slope *= slope;
                    int intSlope = (int)(slope * height);
                    int ground = groundArray[(x + radius) * (radius * 2 + 1) + z + radius];
                    if (intSlope > 0 && world.getBlockId(x + x0, ground - 1, z + z0) == BlockList.snowLayer.id) {
                        world.setBlockAndMetadata(x + x0, ground - 1, z + z0, this.block, this.metadata);
                    }

                    for (int y = 0; y < intSlope; y++) {
                        int ox = (int)(x + skewX * y);
                        int oz = (int)(z + skewZ * y);
                        world.setBlockAndMetadata(ox + x0, y + ground, oz + z0, this.block, this.metadata);
                    }
                }
            }
        }

        return true;
    }
}
