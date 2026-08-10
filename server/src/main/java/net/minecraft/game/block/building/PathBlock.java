package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class PathBlock extends Block {
    private Icon top;
    private Icon side;

    public PathBlock(int blockid) {
        super(blockid, MaterialList.ground);
        this.setTickable(true);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
        this.setLightOpacity(255);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return BoundingBox.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (side == 1) {
            return this.top;
        } else {
            return side == 0 ? BlockList.dirt.getSideIcon(side) : this.side;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("grasses/path_top");
        this.side = r.registerIcon("grasses/path_side");
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int side) {
        if (world.getBlockMaterial(xCoord, yCoord + 1, zCoord).isSolid()) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.dirt.id);
        }
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.dirt.dropID(0, random);
    }
}
