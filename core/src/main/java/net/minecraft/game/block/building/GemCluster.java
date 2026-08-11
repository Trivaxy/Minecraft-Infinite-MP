package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class GemCluster extends Block {
    private static String[] NAME_LIST = new String[]{"rubyCluster", "sapphireCluster", "emeraldCluster", "amethystCluster", "topazCluster"};
    private Icon[] icons;

    public GemCluster(int blockid) {
        super(blockid, MaterialList.ice);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 4;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("clusters/" + NAME_LIST[i]);
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
}
