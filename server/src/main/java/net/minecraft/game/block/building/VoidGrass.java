package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class VoidGrass extends Block {
    private Icon top;
    private Icon side;

    public VoidGrass(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (side == 1) {
            return this.top;
        } else {
            return side == 0 ? BlockList.voidRock.blockIcon : this.side;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("grasses/" + this.name + "_top");
        this.side = r.registerIcon("grasses/" + this.name + "_side");
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return BlockList.voidRock.id;
    }
}
