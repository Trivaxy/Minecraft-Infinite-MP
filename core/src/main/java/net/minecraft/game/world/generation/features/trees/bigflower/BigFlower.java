package net.minecraft.game.world.generation.features.trees.bigflower;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public abstract class BigFlower extends Tree {
    static final int bigFlowerId = BlockList.flowerPetal.id;
    protected boolean vinesGrow;
    protected int minHeight;
    public static final Map<Integer, BigFlower> FLOWERS = new HashMap<>();

    public BigFlower(int minTreeHeight, boolean vinesGrow) {
        this.minHeight = minTreeHeight;
        this.vinesGrow = vinesGrow;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(4) + this.minHeight;
        if (y < 1 || y + height + 1 > world.getWorldHeight()) {
            return false;
        }

        if (!this.checkCanPlace(world, x, y, z, height)) {
            return false;
        }

        int id = world.getBlockId(x, y - 1, z);
        if ((id == BlockList.grass.id || id == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
            world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, world.getBiomeAt(x, z).fillerMeta);

            for (int y0 = 0; y0 < height; y0++) {
                Block block = world.getBlock(x, y + y0, z);
                if (block == null || block instanceof Flower) {
                    this.createShape(world, x, y, z, height, y0);
                    this.placeVinesOnStem(world, random, x, y, z, y0);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected abstract void createShape(World var1, int var2, int var3, int var4, int var5, int var6);

    protected void growVines(World world, int x, int y, int z, int meta) {
        world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);

        for (int var6 = 4; world.getBlockId(x, --y, z) == 0 && var6 > 0; var6--) {
            world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);
        }
    }

    protected boolean checkCanPlace(World world, int x, int y, int z, int height) {
        boolean canPlace = true;

        for (int y0 = y; y0 <= y + 1 + height; y0++) {
            byte offset = 1;
            if (y0 == y) {
                offset = 0;
            }

            if (y0 >= y + 1 + height - 2) {
                offset = 2;
            }

            for (int x0 = x - offset; x0 <= x + offset && canPlace; x0++) {
                for (int z0 = z - offset; z0 <= z + offset && canPlace; z0++) {
                    if (y0 >= 0 && y0 < world.getWorldHeight()) {
                        Block block = world.getBlock(x0, y0, z0);
                        if (block != null && !(block instanceof Flower)) {
                            canPlace = false;
                        }
                    } else {
                        canPlace = false;
                    }
                }
            }
        }

        return canPlace;
    }

    public void placeVinesOnStem(World world, Random random, int x, int y, int z, int y0) {
        if (this.vinesGrow && y0 > 0) {
            if (random.nextInt(3) > 0 && world.isAir(x - 1, y + y0, z)) {
                world.setBlockAndMetadata(x - 1, y + y0, z, BlockList.vine.id, 4);
            }

            if (random.nextInt(3) > 0 && world.isAir(x + 1, y + y0, z)) {
                world.setBlockAndMetadata(x + 1, y + y0, z, BlockList.vine.id, 5);
            }

            if (random.nextInt(3) > 0 && world.isAir(x, y + y0, z - 1)) {
                world.setBlockAndMetadata(x, y + y0, z - 1, BlockList.vine.id, 2);
            }

            if (random.nextInt(3) > 0 && world.isAir(x, y + y0, z + 1)) {
                world.setBlockAndMetadata(x, y + y0, z + 1, BlockList.vine.id, 3);
            }
        }
    }

    static {
        FLOWERS.put(BlockList.dandelion.id, new BigDandelion());
        FLOWERS.put(BlockList.rose.id, new BigRose());
        FLOWERS.put(BlockList.lilac.id, new BigLilac());
        FLOWERS.put(BlockList.petunia.id, new BigPetunia());
        FLOWERS.put(BlockList.tulip.id, new BigTulip());
        FLOWERS.put(BlockList.whiteTulip.id, new BigTulip(255));
        FLOWERS.put(BlockList.pinkTulip.id, new BigTulip(6));
        FLOWERS.put(BlockList.daisy.id, new BigLilac(5, 0));
        FLOWERS.put(BlockList.paeonia.id, new BigLilac(7, 7));
    }
}
