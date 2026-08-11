package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class Moss extends Block {
    private Icon altIcon;

    public Moss(int blockid, Material material) {
        super(blockid, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.compostable = true;
        this.names = new String[]{"moss", "algae"};
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return mdata == 1 ? this.altIcon : this.blockIcon;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon(this.name);
        this.altIcon = ir.registerIcon("holyMoss");
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return 1;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return this.rotationRand(x, y, z, side);
    }
}
