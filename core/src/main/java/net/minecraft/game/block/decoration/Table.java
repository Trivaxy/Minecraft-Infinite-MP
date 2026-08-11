package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Table extends Block {
    public static final String[] tables = new String[]{
        "oakTable",
        "birchTable",
        "spruceTable",
        "palmTable",
        "acaciaTable",
        "jungleTable",
        "mangroveTable",
        "baobabTable",
        "scorchedTable",
        "mapleTable",
        "cherryTable",
        "mahoganyTable",
        "oldenOakTable",
        "hellbarkTable",
        "nylliumTable",
        "purpurTable",
        "willowTable",
        "limeTable"
    };
    public static boolean renderSides;

    public Table(int blockID, Material material) {
        super(blockID, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = tables;
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
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return !renderSides && world.getBlockId(xCoord, yCoord, zCoord) == this.id && side > 1 ? false : super.renderFace(world, xCoord, yCoord, zCoord, side);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.TABLE;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int getMaxMeta() {
        return BlockList.planks.getMaxMeta();
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata == 255) {
            return side != 1 && side != 0 ? this.blockIcon : BlockList.glass.blockIcon;
        } else {
            return BlockList.planks.getIcon(side, mdata);
        }
    }

    @Override
    public StepSound getStepSound(int meta) {
        return meta == 255 ? StepSoundsList.glassSound : super.getStepSound(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("glassTable_side");
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }

        ls.add(new ItemStack(id, 1, 255));
    }
}
