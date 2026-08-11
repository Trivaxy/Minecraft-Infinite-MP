package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Dirt extends Block {
    protected Icon[] icons;
    private static String[] NAMES = new String[]{"dirt", "permafrost", "mud", "coarseDirt", "rooted_dirt", "soil", "dryDirt", "aerdirt"};

    public Dirt(int blockid) {
        super(blockid, MaterialList.ground);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAMES;
        this.setTickable(true);
    }

    @Override
    public final void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (random.nextInt(20) == 0 && world.getBlockMetadata(xCoord, yCoord, zCoord) == 6) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (world.getBlockMaterial(xCoord + i, yCoord, zCoord + j) == MaterialList.water) {
                        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3);
                    }
                }
            }
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 2;
        }

        return this.icons[meta];
    }

    @Override
    public int dropMeta(int meta) {
        return meta > this.getMaxMeta() ? 2 : meta;
    }

    @Override
    public int getMaxMeta() {
        return 7;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[this.names.length];

        for (int i = 0; i < this.names.length; i++) {
            this.icons[i] = ir.registerIcon("soils/" + this.names[i]);
        }
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return this.rotationRand(x, y, z, side);
    }
}
