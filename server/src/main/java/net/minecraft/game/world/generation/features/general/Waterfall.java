package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Waterfall extends Feature {
    public static final Waterfall WATERFALL = new Waterfall(BlockList.waterMoving.id);
    public static final Waterfall LAVAFALL = new Waterfall(BlockList.lavaMoving.id);
    public static final Waterfall HELLAVA = new Waterfall(BlockList.lavaMoving.id, BlockList.netherrack.id);
    public static final Waterfall VOIDWATER = new Waterfall(BlockList.waterMoving.id, BlockList.voidRock.id);
    private int fluidId;
    private int blockId = BlockList.stone.id;

    public Waterfall(int id) {
        this.fluidId = id;
    }

    public Waterfall(int id, int id2) {
        this.fluidId = id;
        this.blockId = id2;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (world.getBlockId(x, y + 1, z) == this.blockId && world.getBlockId(x, y - 1, z) == this.blockId && world.getBlockId(x, y, z) == this.blockId) {
            int side = 0;
            if (world.getBlockId(x - 1, y, z) == this.blockId) {
                side++;
            }

            if (world.getBlockId(x + 1, y, z) == this.blockId) {
                side++;
            }

            if (world.getBlockId(x, y, z - 1) == this.blockId) {
                side++;
            }

            if (world.getBlockId(x, y, z + 1) == this.blockId) {
                side++;
            }

            int side2 = 0;
            if (world.getBlockId(x - 1, y, z) == 0) {
                side2++;
            }

            if (world.getBlockId(x + 1, y, z) == 0) {
                side2++;
            }

            if (world.getBlockId(x, y, z - 1) == 0) {
                side2++;
            }

            if (world.getBlockId(x, y, z + 1) == 0) {
                side2++;
            }

            if (side == 3 && side2 == 1) {
                world.setBlockWithNotify(x, y, z, this.fluidId);
            }

            return true;
        } else {
            return false;
        }
    }
}
