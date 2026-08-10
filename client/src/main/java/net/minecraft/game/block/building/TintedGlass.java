package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.WorldSource;

public class TintedGlass extends Block {
    public TintedGlass(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int renderPass() {
        return 1;
    }

    @Override
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return world.getBlockId(xCoord, yCoord, zCoord) == this.id ? false : super.renderFace(world, xCoord, yCoord, zCoord, side);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("glass/" + this.name);
    }
}
