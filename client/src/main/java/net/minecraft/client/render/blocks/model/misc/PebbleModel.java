package net.minecraft.client.render.blocks.model.misc;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class PebbleModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        long seed = x * 3129871 ^ y * 116129781L ^ z;
        seed = seed * seed * 42317861L + seed * 11L;
        int type = (int)(seed >> 16 & 3L);
        boolean render = false;
        switch (type) {
            case 0:
                block.setBounds(0.2F, 0.0, 0.2F, 0.5, 0.2F, 0.5);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.7F, 0.0, 0.7F, 0.9F, 0.1F, 0.9F);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.1F, 0.0, 0.1F, 0.9F, 0.2F, 0.9F);
                break;
            case 1:
                block.setBounds(0.2F, 0.0, 0.1F, 0.5, 0.1F, 0.4F);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.6F, 0.0, 0.3F, 0.9F, 0.2F, 0.6F);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.3F, 0.0, 0.7F, 0.5, 0.1F, 0.9F);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.1F, 0.0, 0.1F, 0.9F, 0.2F, 0.9F);
                break;
            default:
                block.setBounds(0.4F, 0.0, 0.4F, 0.6F, 0.1F, 0.6F);
                render = this.renderStandardBlock(block, x, y, z);
                block.setBounds(0.1F, 0.0, 0.1F, 0.9F, 0.2F, 0.9F);
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
