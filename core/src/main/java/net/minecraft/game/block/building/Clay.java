package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.IconRegister;

public class Clay extends Block {
    public Clay(int blockID) {
        super(blockID, MaterialList.ground);
    }

    @Override
    public int dropID(int id, Random random) {
        return ItemList.clay.id;
    }

    @Override
    public int quantityDropped(Random random) {
        return 4;
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return this.rotationRand(x, y, z, side);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("clays/" + this.name);
    }
}
