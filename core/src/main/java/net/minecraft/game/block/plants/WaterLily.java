package net.minecraft.game.block.plants;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class WaterLily extends LilyPad {
    public static final String[] lillies = new String[]{"waterLily_pink", "waterLily_blue", "waterLily_white"};
    private Icon[] icons;

    public WaterLily(int id) {
        super(id);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = lillies;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return side == 1 ? this.blockIcon : this.icons[meta];
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
        for (int i = 0; i < this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/lilyPad_heaven");
        this.icons = new Icon[lillies.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("plants/" + lillies[index]);
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.WATERLILY;
    }
}
