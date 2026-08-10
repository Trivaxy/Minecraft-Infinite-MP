package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.misc.TransparentBlock;
import net.minecraft.game.texture.IconRegister;

public class Glass extends TransparentBlock {
    public Glass(int id, Material mat, boolean transparent) {
        super(id, mat, transparent);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("glass/" + this.name);
    }
}
