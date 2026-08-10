package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class MetalBlock extends Block {
    public Icon top;
    public Icon btm;
    boolean hasSides;

    public MetalBlock(int blockid, boolean sides) {
        super(blockid, MaterialList.metal);
        this.hasSides = sides;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (side == 0 && this.hasSides) {
            return this.btm;
        } else {
            return side == 1 && this.hasSides ? this.top : this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        if (this.hasSides) {
            this.top = r.registerIcon("metals/" + this.name + "_top");
            this.btm = r.registerIcon("metals/" + this.name + "_bottom");
        }

        this.blockIcon = r.registerIcon("metals/" + this.name);
    }
}
