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

public class Stone extends Block {
    public static final String[] stoneType = new String[]{
        "stone", "marble", "basalt", "limestone", "granite", "chlorite", "iceStone", "dripStone", "holystone"
    };
    private Icon[] icons;

    public Stone(int blockid) {
        super(blockid, MaterialList.rock);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = stoneType;
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
        return 8;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[stoneType.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("stones/" + stoneType[index]);
        }
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return BlockList.cobblestone.id;
    }

    @Override
    public final int pickID(int meta) {
        return this.id;
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        if (dmg == 0) {
            switch (id) {
                case 0:
                    return new ItemStack(BlockList.stoneBrick.id, 1, 0);
                case 1:
                    return new ItemStack(BlockList.stoneBrick.id, 1, 2);
                case 2:
                    return new ItemStack(BlockList.concrete.id, 1);
                case 3:
                    return new ItemStack(BlockList.slabStone.id, 2, 0);
                case 4:
                    return new ItemStack(BlockList.panelStone.id, 2, 0);
            }
        } else {
            if (dmg == 8 && id == 1) {
                return new ItemStack(BlockList.stoneBrick.id, 2, 11);
            }

            if (id == 0) {
                return new ItemStack(BlockList.stoneBrick.id, 1, dmg + 2);
            }
        }

        return null;
    }
}
