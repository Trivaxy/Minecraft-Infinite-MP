package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Coral extends Block {
    public Icon[] icons;
    private static String[] NAME_LIST = new String[]{"coralYellow", "coralRed", "coralBlue", "coralPink"};

    public Coral(int blockID, Material material) {
        super(blockID, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
        this.compostable = true;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("corals/" + NAME_LIST[i]);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public int dropMeta(int meta) {
        return meta;
    }

    @Override
    public int getMaxMeta() {
        return 3;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
