package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.misc.TransparentBlock;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class ColoredGlass extends TransparentBlock {
    public static String[] GLASS = new String[]{
        "glass_0",
        "glass_1",
        "glass_2",
        "glass_3",
        "glass_4",
        "glass_5",
        "glass_6",
        "glass_7",
        "glass_8",
        "glass_9",
        "glass_10",
        "glass_11",
        "glass_12",
        "glass_13",
        "glass_14",
        "glass_15",
        "glass_16",
        "glass_17"
    };
    private Icon[] icons;

    public ColoredGlass(int blockID, Material material, boolean localFlag) {
        super(blockID, material, localFlag);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = GLASS;
    }

    @Override
    public final int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int renderPass() {
        return 1;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[GLASS.length * 2];

        for (int meta = 0; meta < GLASS.length; meta++) {
            this.icons[meta] = ir.registerIcon("glass/" + GLASS[meta]);
        }
    }

    @Override
    public int dropMeta(int meta) {
        return meta;
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
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.coloredPane.id, 4, dmg);
            default:
                return null;
        }
    }
}
