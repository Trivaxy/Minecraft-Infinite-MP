package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.WorldSource;

public class Bricks extends Block {
    public static String[] BRICKS = new String[]{
        "brick",
        "bricks_0",
        "bricks_1",
        "bricks_2",
        "bricks_3",
        "bricks_4",
        "bricks_5",
        "bricks_6",
        "bricks_7",
        "bricks_8",
        "bricks_9",
        "bricks_10",
        "bricks_11",
        "bricks_12",
        "bricks_13",
        "bricks_14",
        "bricks_15",
        "bricks_16",
        "bricks_17"
    };
    private Icon[] icons;

    public Bricks(int blockid, Material material) {
        super(blockid, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = BRICKS;
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
        this.icons = new Icon[BRICKS.length * 2];

        for (int meta = 0; meta < BRICKS.length; meta++) {
            this.icons[meta] = ir.registerIcon("bricks/" + BRICKS[meta]);
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
        return 16;
    }

    public static int getColor(int id) {
        return id == 0 ? ~id & 16 : ~(id - 1) & 15;
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
                return new ItemStack(BlockList.stairRedBrick.id, 2, dmg * 8);
            case 1:
                return new ItemStack(BlockList.slabBrick.id, 2, dmg);
            case 2:
                return new ItemStack(BlockList.panelBrick.id, 2, dmg * 4);
            case 3:
                return new ItemStack(BlockList.wall.id, 1, dmg);
            default:
                return null;
        }
    }
}
