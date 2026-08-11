package net.minecraft.game.block.liquid;

import net.minecraft.game.block.material.Material;
import net.minecraft.game.world.World;

public class StillLiquid extends LiquidBlock {
    public StillLiquid(int id, Material mat) {
        super(id, mat);
        this.setTickable(false);
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        super.onNeighborChange(world, xCoord, yCoord, zCoord, id);
        if (world.getBlockId(xCoord, yCoord, zCoord) == this.id) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            world.editingBlocks = true;
            world.setBlockAndMetadata(xCoord, yCoord, zCoord, this.id - 1, meta);
            world.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
            world.forceTick(xCoord, yCoord, zCoord, this.id - 1);
            world.editingBlocks = false;
        }
    }
}
