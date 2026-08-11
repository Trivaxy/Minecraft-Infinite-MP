package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.WorldSource;

public class HardenedClay extends Block {
    public static String[] CLAYS = new String[]{
        "hardened_clay",
        "hardened_clay_0",
        "hardened_clay_1",
        "hardened_clay_2",
        "hardened_clay_3",
        "hardened_clay_4",
        "hardened_clay_5",
        "hardened_clay_6",
        "hardened_clay_7",
        "hardened_clay_8",
        "hardened_clay_9",
        "hardened_clay_10",
        "hardened_clay_11",
        "hardened_clay_12",
        "hardened_clay_13",
        "hardened_clay_14",
        "hardened_clay_15",
        "hardened_clay_16",
        "hardened_clay_17"
    };
    private Icon[] icons;

    public HardenedClay(int blockid, Material material) {
        super(blockid, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = CLAYS;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta >= 0 && meta < CLAYS.length ? this.icons[meta] : this.blockIcon;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("clays/hardened_clay");
        this.icons = new Icon[CLAYS.length * 2];

        for (int i = 0; i < CLAYS.length; i++) {
            this.icons[i] = ir.registerIcon("clays/" + CLAYS[i]);
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta >= CLAYS.length ? this.blockIcon : this.icons[meta];
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 18;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
