package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class Pebble extends Block {
    public Pebble(int blockid, Material material) {
        super(blockid, material);
        this.setBounds(0.4F, 0.0, 0.4F, 0.6F, 0.1F, 0.6F);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.PEBBLES;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return world.isBlockFull(x, y - 1, z);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }

        super.onNeighborChange(world, x, y, z, id);
    }

    @Override
    public int getMaxMeta() {
        return BlockList.stone.getMaxMeta();
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.stone.getIcon(side, mdata);
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return world.isSolid(x, y - 1, z);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int dropID(int blockid, Random random) {
        return ItemList.rock.id;
    }

    @Override
    public int pickID(int blockid) {
        return ItemList.rock.id;
    }

    @Override
    public int guaranteedID(int id) {
        return ItemList.rock.id;
    }
}
