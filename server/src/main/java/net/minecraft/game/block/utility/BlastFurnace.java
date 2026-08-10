package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.BlastFurnaceEntity;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class BlastFurnace extends Furnace {
    public BlastFurnace(int blockid, boolean isActive) {
        super(blockid, isActive);
        this.material = MaterialList.metal;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/blastFurnace_side");
        this.front = ir.registerIcon(this.active ? "workstations/blastFurnaceActive" : "workstations/blastFurnace");
        this.top = ir.registerIcon("workstations/blastFurnace_top");
        this.bottom = ir.registerIcon("workstations/blastFurnace_bottom");
    }

    public static void updateState(boolean isActive, World world, int xCoord, int yCoord, int zCoord) {
        int facingMeta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        TileEntity tileEntity = world.getTileEntity(xCoord, yCoord, zCoord);
        keepInventory = true;
        if (isActive) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.steelOvenActive.id);
        } else {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.steelOvenIdle.id);
        }

        keepInventory = false;
        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facingMeta);
        tileEntity.validate();
        world.setBlockTileEntity(xCoord, yCoord, zCoord, tileEntity);
    }

    @Override
    public TileEntity getBlockEntity() {
        return new BlastFurnaceEntity();
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.steelOvenIdle.id;
    }

    @Override
    public int pickID(int blockid) {
        return BlockList.steelOvenIdle.id;
    }
}
