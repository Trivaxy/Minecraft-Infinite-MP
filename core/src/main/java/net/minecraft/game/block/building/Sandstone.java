package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Sandstone extends Block {
    public static String[] STONES = new String[]{
        "sandstone", "sandstoneSmooth", "sandstoneRed", "sandstoneRedSmooth", "sandstoneSilver", "sandstoneSilverSmooth"
    };
    public Icon[] icons;
    public Icon[] topIcons;
    public Icon[] bottomIcons;
    public Icon[] smoothIcons;
    public Icon iconChiseled;

    public Sandstone(int blockID) {
        super(blockID, MaterialList.rock);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = STONES;
    }

    @Override
    public Icon getIcon(int face, int meta) {
        if (meta == 2 || meta == 3) {
            return this.getSideIcon(face, meta, 2, 3, 1);
        } else if (meta == 4 || meta == 5) {
            return this.getSideIcon(face, meta, 4, 5, 2);
        } else if (meta != 255) {
            return this.getSideIcon(face, meta, 0, 1, 0);
        } else {
            return face != 0 && face != 1 ? this.iconChiseled : this.topIcons[0];
        }
    }

    private Icon getSideIcon(int face, int meta, int targetMeta1, int targetMeta2, int targetIcon) {
        if (face == 0 && meta == targetMeta1) {
            return this.bottomIcons[targetIcon];
        } else if (face != 1 && (meta != targetMeta2 || face != 0)) {
            return meta == targetMeta2 ? this.smoothIcons[targetIcon] : this.icons[targetIcon];
        } else {
            return this.topIcons[targetIcon];
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.topIcons = new Icon[3];
        this.bottomIcons = new Icon[3];
        this.icons = new Icon[3];
        this.smoothIcons = new Icon[3];

        for (int i = 0; i < 3; i++) {
            int baseIndex = i * 2;
            int smoothIndex = baseIndex + 1;
            String base = STONES[baseIndex];
            String smooth = STONES[smoothIndex];
            this.topIcons[i] = ir.registerIcon("sandstone/" + base + "Top");
            this.bottomIcons[i] = ir.registerIcon("sandstone/" + base + "Bottom");
            this.icons[i] = ir.registerIcon("sandstone/" + base + "Side");
            this.smoothIcons[i] = ir.registerIcon("sandstone/" + smooth);
        }

        this.iconChiseled = ir.registerIcon("sandstone/sandstoneChiseled");
    }

    @Override
    public int getMaxMeta() {
        return 5;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }

        ls.add(new ItemStack(id, 1, 255));
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        if (dmg != 255) {
            switch (id) {
                case 0:
                    return new ItemStack(BlockList.stairSandstone.id, 2, dmg * 8);
                case 1:
                    return new ItemStack(BlockList.slabSandstone.id, 2, dmg);
                case 2:
                    return new ItemStack(BlockList.panelSandstone.id, 2, dmg * 4);
                case 3:
                    return new ItemStack(BlockList.wallSandstone.id, 1, dmg);
                case 4:
                    if (dmg % 2 == 0) {
                        return new ItemStack(BlockList.sandStone.id, 1, dmg + 1);
                    }
                case 5:
                    if (dmg == 0) {
                        return new ItemStack(BlockList.sandStone.id, 1, 255);
                    }
            }
        }

        return null;
    }
}
