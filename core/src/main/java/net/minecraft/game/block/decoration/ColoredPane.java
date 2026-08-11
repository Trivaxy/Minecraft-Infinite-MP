package net.minecraft.game.block.decoration;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;

public class ColoredPane extends WindowPane {
    public static String[] NAMES = new String[]{
        "glasspane_0",
        "glasspane_1",
        "glasspane_2",
        "glasspane_3",
        "glasspane_4",
        "glasspane_5",
        "glasspane_6",
        "glasspane_7",
        "glasspane_8",
        "glasspane_9",
        "glasspane_10",
        "glasspane_11",
        "glasspane_12",
        "glasspane_13",
        "glasspane_14",
        "glasspane_15",
        "glasspane_16",
        "glasspane_17"
    };

    public ColoredPane(int blockID, Material material) {
        super(blockID, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAMES;
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
    public Icon getIcon(int side, int mdata) {
        return BlockList.glassStained.getIcon(side, mdata);
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return BlockList.glassStained.getMaxMeta();
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
