package net.minecraft.game.block.plants;

import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class CoralFan extends Flower {
    public Icon[] icons;
    private static String[] NAME_LIST = new String[]{"tubeCoral", "fireCoral", "bubbleCoral", "brainCoral"};

    public CoralFan(int blockid) {
        super(blockid);
        this.material = MaterialList.water;
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAME_LIST;
        float offset = 0.4F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.6F, 0.5F + offset);
    }

    @Override
    public boolean canGrowHere(int blockid) {
        return blockid == BlockList.coral.id;
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return this.canGrowHere(world.getBlockId(xCoord, yCoord - 1, zCoord));
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.icons = new Icon[NAME_LIST.length];

        for (int i = 0; i < NAME_LIST.length; i++) {
            this.icons[i] = r.registerIcon("plants/" + NAME_LIST[i]);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta > this.icons.length) {
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
