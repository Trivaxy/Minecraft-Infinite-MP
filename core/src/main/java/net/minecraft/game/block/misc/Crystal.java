package net.minecraft.game.block.misc;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Crystal extends Block {
    private static String[] NAME_LIST = new String[]{"rubyCrystal", "sapphireCrystal", "emeraldCrystal", "amethystCrystal", "topazCrystal"};
    private Icon[] icons;

    public Crystal(int blockid) {
        super(blockid, MaterialList.rock);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
    }

    @Override
    public final void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int blockID) {
        if (!this.canStay(world, xCoord, yCoord, zCoord)) {
            this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return world.getBlock(x, y + 1, z) == BlockList.clusterBlock
            || world.getBlock(x, y - 1, z) == BlockList.clusterBlock
            || world.getBlock(x, y, z + 1) == BlockList.clusterBlock
            || world.getBlock(x, y, z - 1) == BlockList.clusterBlock
            || world.getBlock(x + 1, y, z) == BlockList.clusterBlock
            || world.getBlock(x - 1, y, z) == BlockList.clusterBlock;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canStay(world, x, y, z);
    }

    @Override
    public int dropMeta(int id) {
        return 0;
    }

    @Override
    public int getMaxMeta() {
        return 4;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("crystals/" + NAME_LIST[i]);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.icons[meta];
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CRYSTAL;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int dropID(int blockid, Random random2) {
        switch (blockid) {
            case 0:
                return ItemList.ruby.id;
            case 1:
                return ItemList.sapphire.id;
            case 2:
                return ItemList.emerald.id;
            case 3:
                return ItemList.amethyst.id;
            case 4:
                return ItemList.topaz.id;
            default:
                return this.id;
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(3) + 1;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return null;
    }
}
