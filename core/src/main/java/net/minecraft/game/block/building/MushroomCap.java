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

public class MushroomCap extends Block {
    private static String[] NAME_LIST = new String[]{"shroomlight", "brownCap", "redCap", "gloCap", "hellbarkCap", "nylliumCap", "ember"};
    private Icon[] icons;

    public MushroomCap(int blockID) {
        super(blockID, MaterialList.wood);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
        this.compostable = true;
    }

    @Override
    public final int quantityDropped(Random random) {
        return this.id == BlockList.shroomLight.id ? 1 : random.nextInt(3);
    }

    @Override
    public final int dropID(int id, Random random) {
        switch (id) {
            case 1:
                return BlockList.mushroomBrown.id;
            case 2:
                return BlockList.mushroomRed.id;
            case 3:
                return BlockList.mushroomGlowing.id;
            case 4:
                return BlockList.mushroomCrimson.id;
            case 5:
                return BlockList.mushroomNylium.id;
            default:
                return this.id;
        }
    }

    @Override
    public int dropMeta(int iD) {
        return 0;
    }

    @Override
    public int getMaxMeta() {
        return 5;
    }

    @Override
    public int pickMeta(int id) {
        return id;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("petals/" + NAME_LIST[i]);
        }
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata < 0 || mdata >= this.icons.length) {
            mdata = 0;
        }

        return this.icons[mdata];
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        if (this.id == BlockList.shroomLight.id) {
            ls.add(new ItemStack(id, 1, 0));
            ls.add(new ItemStack(id, 1, 3));
        } else {
            for (int i = 1; i <= this.getMaxMeta(); i++) {
                if (i != 3) {
                    ls.add(new ItemStack(id, 1, i & 15));
                }
            }
        }
    }
}
