package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.misc.TransparentBlock;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.util.LightLayer;

public class Ice extends TransparentBlock {
    public Ice(int blockID, Material material) {
        super(blockID, material, false);
        this.setTickable(true);
        this.slipperiness = 1.0F;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void onBroken(World world, int xCoord, int yCoord, int zCoord, int facing) {
        if (!(world.currDim instanceof HellDimension)) {
            world.setBlock(xCoord, yCoord, zCoord, BlockList.waterMoving.id);
        }
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        if (!(world.currDim instanceof HellDimension) && world.getBrightness(LightLayer.Block, x, y, z) > 11) {
            world.setBlockWithNotify(x, y, z, BlockList.waterMoving.id);
        }
    }

    @Override
    public int renderPass() {
        return 1;
    }
}
