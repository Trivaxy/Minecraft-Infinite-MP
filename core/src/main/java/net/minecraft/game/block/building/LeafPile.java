package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class LeafPile extends LayerBlock {
    public LeafPile(int id, Material mat) {
        super(id, mat);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = Leaves.LEAVES;
        this.particleGravity = 0.3F;
        this.compostable = true;
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord) & 7;
        int realMeta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord);
        if (this.id == world.getBlockId(xCoord, yCoord - 1, zCoord) && meta < 7) {
            world.setBlockMetadata(xCoord, yCoord - 1, zCoord, realMeta + 1);
            world.setBlock(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return (world.getBlockMetadata(x, y - 1, z) & 7) < 7 ? true : super.placeableAt(world, x, y, z);
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.leaves.opacity ? BlockList.leaves.standard[mdata >> 3] : BlockList.leaves.fast[mdata >> 3];
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    @Override
    public int pickMeta(int id) {
        return (id >> 3) * 8;
    }

    @Override
    public int dropMeta(int id) {
        return (id >> 3) * 8;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta() * 8; i += 8) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
