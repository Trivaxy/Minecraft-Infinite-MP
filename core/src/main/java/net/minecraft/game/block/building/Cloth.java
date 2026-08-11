package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.WorldSource;

public class Cloth extends Block {
    public static String[] CLOTHS = new String[]{
        "cloth_0",
        "cloth_1",
        "cloth_2",
        "cloth_3",
        "cloth_4",
        "cloth_5",
        "cloth_6",
        "cloth_7",
        "cloth_8",
        "cloth_9",
        "cloth_10",
        "cloth_11",
        "cloth_12",
        "cloth_13",
        "cloth_14",
        "cloth_15",
        "cloth_16",
        "cloth_17"
    };
    private Icon[] icons;

    public Cloth() {
        super(35, MaterialList.cloth);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = CLOTHS;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[CLOTHS.length * 2];

        for (int meta = 0; meta < CLOTHS.length; meta++) {
            this.icons[meta] = ir.registerIcon("cloth/" + CLOTHS[meta]);
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        return this.icons[world.getBlockMetadata(x, y, z)];
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    public static int getClothColor(int id) {
        return ~id & 15;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.carpet.id, 2, dmg);
            default:
                return null;
        }
    }
}
