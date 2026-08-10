package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Boulder extends Feature {
    public static final Boulder rock = new Boulder(BlockList.stone.id, 0, 1);
    public static final Boulder ice = new Boulder(BlockList.stone.id, 6, 1);
    public static final Boulder angelite = new Boulder(BlockList.stone.id, 8, 1);
    public static final Boulder sandstone = new Boulder(BlockList.sandStone.id, 0, 1);
    public static final Boulder granite = new Boulder(BlockList.stone.id, 4, 1);
    public static final Boulder chlorite = new Boulder(BlockList.stone.id, 5, 1);
    public static final Boulder limestone = new Boulder(BlockList.stone.id, 3, 1);
    public static final Boulder moss = new Boulder(BlockList.moss.id, 0, 0);
    public static final Boulder volcanic = new Boulder(BlockList.stone.id, 2, 1);
    public static final Boulder hell = new Boulder(BlockList.netherrack.id, 0, 1);
    public static final Boulder glowing = new Boulder(BlockList.glowStone.id, 0, 1);
    public static final Boulder obsidian = new Boulder(BlockList.clusterBlock.id, 3, 1);
    int block;
    int meta;
    int radius;

    public Boulder(int block, int meta, int radi) {
        this.block = block;
        this.meta = meta;
        this.radius = radi;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        while (y > 3) {
            if (!world.isAir(x, y - 1, z)) {
                int belowBlock = world.getBlockId(x, y - 1, z);
                if (this == glowing && belowBlock == BlockList.mushroomCap.id
                    || belowBlock == BlockList.snowLayer.id
                    || belowBlock == BlockList.netherrack.id
                    || belowBlock == BlockList.grass.id
                    || belowBlock == BlockList.dirt.id
                    || belowBlock == BlockList.stone.id
                    || belowBlock == BlockList.silverSand.id
                    || belowBlock == BlockList.sand.id
                    || belowBlock == BlockList.slowSand.id
                    || belowBlock == BlockList.soulSoil.id) {
                    break;
                }
            }

            y--;
        }

        if (y <= 3) {
            return false;
        }

        int rad = this.radius;

        for (int attempt = 0; rad >= 0 && attempt < 3; attempt++) {
            int randX = rad + random.nextInt(2);
            int randY = rad + random.nextInt(2);
            int randZ = rad + random.nextInt(2);
            float length = (randX + randY + randZ) * 0.333F + 0.5F;

            for (int xpos = x - randX; xpos <= x + randX; xpos++) {
                for (int zpos = z - randZ; zpos <= z + randZ; zpos++) {
                    for (int ypos = y - randY; ypos <= y + randY; ypos++) {
                        float sizeX = xpos - x;
                        float sizeZ = zpos - z;
                        float sizeY = ypos - y;
                        if (sizeX * sizeX + sizeZ * sizeZ + sizeY * sizeY <= length * length) {
                            world.setBlockAndMetadata(xpos, ypos, zpos, this.block, this.meta);
                        }
                    }
                }
            }

            x += -(rad + 1) + random.nextInt(2 + rad * 2);
            z += -(rad + 1) + random.nextInt(2 + rad * 2);
            y += 0 - random.nextInt(2);
        }

        return true;
    }
}
