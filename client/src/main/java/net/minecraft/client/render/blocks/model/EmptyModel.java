package net.minecraft.client.render.blocks.model;

import net.minecraft.game.block.Block;

public class EmptyModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        return false;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
