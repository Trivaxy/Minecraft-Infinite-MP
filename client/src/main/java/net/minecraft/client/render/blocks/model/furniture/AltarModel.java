package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;

public class AltarModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        boolean rendered = this.renderStandardBlock(block, x, y, z);
        if (rendered) {
            this.renderStatue(block, x, y, z);
        }

        return rendered;
    }

    private void renderStatue(Block block, int x, int y, int z) {
        if (!this.hasOverrideTexture()) {
            this.setOverrideTexture(BlockList.altar.getIcon(0, 1));
        }

        this.applyColorCorrection(block, x, y, z, 0.8F);
        this.renderCross(BlockList.altar, this.world.getBlockMetadata(x, y, z), x, y + 0.75, z);
        this.clearOverrideTexture();
    }
}
