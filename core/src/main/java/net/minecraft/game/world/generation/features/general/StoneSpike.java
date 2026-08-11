package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;

public class StoneSpike extends IceSpike {
    public static final StoneSpike INSTANCE = new StoneSpike();

    public StoneSpike() {
        this.heightmultiplier = 12;
        this.metadata = 2;
    }

    @Override
    protected boolean canGrow(Random rng, int shart) {
        return false;
    }

    @Override
    protected boolean snowCheck(World level, int x, int y, int z) {
        return level.getBlockId(x, y, z) != BlockList.dirt.id && level.getBlockId(x, y, z) != BlockList.ash.id;
    }

    @Override
    protected boolean airCheck(World world, int x, int y, int z) {
        return (world.isAir(x, y, z) || world.getBlockId(x, y, z) == BlockList.ash.id) && y > 2;
    }

    @Override
    protected int randomBlockId(Random rand) {
        return rand.nextInt(100) == 0 ? BlockList.lavaMoving.id : BlockList.stone.id;
    }

    @Override
    protected boolean checkBlock(Block block, int blockID) {
        return block == null || block.material == MaterialList.air || blockID == BlockList.dirt.id || blockID == BlockList.stone.id;
    }
}
