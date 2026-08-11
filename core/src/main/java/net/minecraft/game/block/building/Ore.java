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

public class Ore extends Block {
    private Icon altIcon;
    private int dropID;
    boolean variants;

    public Ore(int blockid, int dropID, boolean variants) {
        super(blockid, MaterialList.rock);
        this.hasStates = true;
        this.dropID = dropID;
        this.variants = variants;
    }

    @Override
    public int dropID(int blockid, Random random) {
        return this.dropID;
    }

    @Override
    public int quantityDropped(Random random) {
        return this.id != BlockList.netherGold.id && this.id != BlockList.aetherIron.id ? 1 : random.nextInt(4) + 1;
    }

    @Override
    public int dropMeta(int id) {
        return 0;
    }

    @Override
    public int getMaxMeta() {
        return this.variants ? 1 : 0;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("ores/" + this.name);
        this.altIcon = r.registerIcon("ores/" + this.name + "_black");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 1 ? this.altIcon : this.blockIcon;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
