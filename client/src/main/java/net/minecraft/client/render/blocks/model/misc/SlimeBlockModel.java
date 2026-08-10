package net.minecraft.client.render.blocks.model.misc;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.misc.SlimeBlock;

public class SlimeBlockModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        SlimeBlock slime = (SlimeBlock)block;
        if (!this.hasOverrideTexture()) {
            this.setOverrideTexture(slime.coreIcon);
        }

        slime.setBounds(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
        this.renderStandardBlock(slime, x, y, z);
        slime.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (this.hasOverrideTexture() && this.textureOverride == slime.coreIcon) {
            this.clearOverrideTexture();
        }

        return this.renderStandardBlock(slime, x, y, z);
    }
}
