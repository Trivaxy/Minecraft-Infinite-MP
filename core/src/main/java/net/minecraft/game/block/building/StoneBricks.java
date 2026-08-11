package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class StoneBricks extends Block {
    public static String[] BRICKS = new String[]{
        "stoneBrick",
        "stoneBrickMossy",
        "stoneBrickCracked",
        "marbleBrick",
        "basaltBrick",
        "limestoneBrick",
        "graniteBrick",
        "chloriteBrick",
        "iceBrick",
        "dripstoneBrick",
        "holyBrick",
        "holyChiseledBrick"
    };
    Icon[] icons;

    public StoneBricks(int blockid) {
        super(blockid, MaterialList.rock);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = BRICKS;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata < 0 || mdata >= this.icons.length) {
            mdata = 0;
        }

        return this.icons[mdata];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[BRICKS.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("stoneBrick/" + BRICKS[index]);
        }
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 11;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        if (dmg != 11) {
            switch (id) {
                case 0:
                    return new ItemStack(BlockList.stairBrick.id, 2, dmg * 8);
                case 1:
                    return new ItemStack(BlockList.slabStoneBrick.id, 2, dmg);
                case 2:
                    return new ItemStack(BlockList.panelStoneBrick.id, 2, dmg * 4);
                case 3:
                    return new ItemStack(BlockList.wallStoneBrick.id, 1, dmg);
            }
        }

        return null;
    }
}
