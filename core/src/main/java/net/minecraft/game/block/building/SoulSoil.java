package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.IconRegister;

public class SoulSoil extends Block {
    public SoulSoil(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("sands/" + this.name);
    }
}
