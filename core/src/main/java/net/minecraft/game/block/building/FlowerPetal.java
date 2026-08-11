package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class FlowerPetal extends Block {
    private static String[] NAME_LIST = new String[]{
        "yellowPetal", "redPetal", "purplePetal", "bluePetal", "orangePetal", "whitePetal", "pinkPetal", "cyanPetal"
    };
    private Icon[] icons;

    public FlowerPetal(int blockID) {
        super(blockID, MaterialList.wood);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
        this.compostable = true;
    }

    @Override
    public final int quantityDropped(Random random) {
        return random.nextInt(3);
    }

    @Override
    public final int dropID(int meta, Random random) {
        switch (meta) {
            case 0:
                return BlockList.dandelion.id;
            case 1:
                return BlockList.rose.id;
            case 2:
                return BlockList.petunia.id;
            case 3:
                return BlockList.lilac.id;
            case 4:
                return BlockList.tulip.id;
            case 5:
                return BlockList.daisy.id;
            case 6:
                return BlockList.pinkTulip.id;
            case 7:
                return BlockList.paeonia.id;
            default:
                return BlockList.whiteTulip.id;
        }
    }

    @Override
    public int dropMeta(int id) {
        return 0;
    }

    @Override
    public int getMaxMeta() {
        return 7;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("petals/" + NAME_LIST[i]);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 5;
        }

        return this.icons[meta];
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
