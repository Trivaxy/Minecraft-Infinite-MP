package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.GraveEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class PlayerGrave extends ContainerBlock {
    public PlayerGrave(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facing == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facing == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facing == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.GRAVE;
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return 0;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new GraveEntity();
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.dirt.getIcon(side, 0);
    }
}
