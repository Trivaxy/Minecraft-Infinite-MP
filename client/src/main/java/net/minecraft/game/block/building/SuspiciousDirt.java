package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.IconRegister;

public class SuspiciousDirt extends Block {
    public SuspiciousDirt(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return BlockList.dirt.id;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("soils/" + this.name);
    }
}
