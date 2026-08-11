package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class Flag extends Block {
    public Flag(int blockid) {
        super(blockid, MaterialList.circuits);
        this.silkTouch = false;
        this.enableStats = false;
        this.hasStates = true;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        int meta = world.getBlockMetadata(x, y - 1, z);
        return block == BlockList.glass
            ? true
            : (
                block == BlockList.table
                    ? true
                    : (
                        block == BlockList.detector
                            ? true
                            : (
                                block instanceof Stairs && (meta & 4) != 0
                                    ? true
                                    : (
                                        block instanceof Slab && meta >= 128
                                            ? true
                                            : (
                                                world.getBlockMaterial(x, y, z) == MaterialList.water
                                                    ? false
                                                    : (!world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z))
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        int meta = world.getBlockMetadata(x, y, z);
        if (world.getBlockId(x, y - 1, z) == 0 || meta == 0 && world.getBlockId(x, y + 1, z) != this.id) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockMaterial(xCoord, yCoord - 1, zCoord).isSolid();
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.wood.getIcon(side, 0);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public final Block.RenderType getRenderType() {
        return Block.RenderType.FLAG;
    }

    @Override
    public int dropID(int blockid, Random rand) {
        return blockid == 0 ? 0 : ItemList.flag.id;
    }

    @Override
    public int pickID(int meta) {
        return ItemList.flag.id;
    }

    @Override
    public int dropMeta(int id) {
        return (id - 1) / 4;
    }

    @Override
    public int pickMeta(int id) {
        return (id - 1) / 4;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }
}
