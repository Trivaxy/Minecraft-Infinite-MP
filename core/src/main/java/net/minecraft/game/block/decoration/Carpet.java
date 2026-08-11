package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Carpet extends Block {
    public static String[] CARPETS = new String[]{
        "carpet_0",
        "carpet_1",
        "carpet_2",
        "carpet_3",
        "carpet_4",
        "carpet_5",
        "carpet_6",
        "carpet_7",
        "carpet_8",
        "carpet_9",
        "carpet_10",
        "carpet_11",
        "carpet_12",
        "carpet_13",
        "carpet_14",
        "carpet_15",
        "carpet_16",
        "carpet_17"
    };

    public Carpet() {
        super(82, MaterialList.cloth);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.06F, 1.0);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = CARPETS;
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
        if (!this.placeableAt(world, x, y, z)) {
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
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.cloth.getIcon(side, mdata);
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        return BlockList.cloth.getTexture(world, x, y, z, side);
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public boolean blocksPathfinder(WorldSource world, int x, int y, int z) {
        return false;
    }
}
