package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Planks extends Block {
    public static final String[] woods = new String[]{
        "oakPlanks",
        "birchPlanks",
        "sprucePlanks",
        "palmPlanks",
        "acaciaPlanks",
        "junglePlanks",
        "mangrovePlanks",
        "baobabPlanks",
        "scorchedPlanks",
        "maplePlanks",
        "cherryPlanks",
        "mahoganyPlanks",
        "oldenOakPlanks",
        "hellbarkPlanks",
        "nylliumPlanks",
        "purpurPlanks",
        "willowPlanks",
        "limePlanks"
    };
    private Icon[] icons;

    public Planks(int id) {
        super(id, MaterialList.wood);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = woods;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[woods.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("planks/" + woods[index]);
        }
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.stairWood.id, 2, dmg * 8);
            case 1:
                return new ItemStack(BlockList.slabWood.id, 2, dmg);
            case 2:
                return new ItemStack(BlockList.panelWood.id, 2, dmg * 4);
            case 3:
                return new ItemStack(BlockList.fence.id, 1, dmg);
            case 4:
                return new ItemStack(BlockList.fenceGate.id, 1, dmg * 8);
            case 5:
                return new ItemStack(BlockList.chair.id, 1, dmg * 8);
            case 6:
                return new ItemStack(BlockList.table.id, 1, dmg);
            case 7:
                if (dmg > 15) {
                    return new ItemStack(BlockList.trapdoorGlass.id, 2, (dmg - 15) * 8);
                }

                return new ItemStack(BlockList.trapdoorWood.id, 2, dmg * 8);
            default:
                return null;
        }
    }
}
