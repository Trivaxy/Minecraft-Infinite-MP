package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;

public class FlagModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
        boolean render = this.renderStandardBlock(block, x, y, z);
        Tessellator tess = Tessellator.instance;
        if (this.world.getBlockMetadata(x, y, z) == 0) {
            if (this.textureOverride == null) {
                this.setOverrideTexture(BlockList.planks.getIcon(1, 0));
            }

            block.setBounds(0.325F, 0.0, 0.325F, 0.675F, 0.1F, 0.675F);
            this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
            this.clearOverrideTexture();
        } else {
            if (this.textureOverride == null) {
                this.setOverrideTexture(BlockList.cloth.getIcon(1, (this.world.getBlockMetadata(x, y, z) - 1) / 4));
            }

            switch (this.world.getBlockMetadata(x, y, z) & 3) {
                case 0:
                    block.setBounds(0.0, 0.1F, 0.5, 1.0, 0.9F, 0.5);
                    tess.addTranslation(-0.5F, 0.0F, 0.0F);
                    this.renderStandardBlock(block, x, y, z);
                    tess.addTranslation(0.5F, 0.0F, 0.0F);
                    block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
                    break;
                case 1:
                    block.setBounds(0.5, 0.1F, 0.0, 0.5, 0.9F, 1.0);
                    tess.addTranslation(0.0F, 0.0F, 0.5F);
                    this.renderStandardBlock(block, x, y, z);
                    tess.addTranslation(0.0F, 0.0F, -0.5F);
                    block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
                    break;
                case 2:
                    block.setBounds(0.5, 0.1F, 0.0, 0.5, 0.9F, 1.0);
                    tess.addTranslation(0.0F, 0.0F, -0.5F);
                    this.renderStandardBlock(block, x, y, z);
                    tess.addTranslation(0.0F, 0.0F, 0.5F);
                    block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
                    break;
                case 3:
                    block.setBounds(0.0, 0.1F, 0.5, 1.0, 0.9F, 0.5);
                    tess.addTranslation(0.5F, 0.0F, 0.0F);
                    this.renderStandardBlock(block, x, y, z);
                    tess.addTranslation(-0.5F, 0.0F, 0.0F);
                    block.setBounds(0.41666666F, 0.0, 0.41666666F, 0.5833333F, 1.0, 0.5833333F);
            }

            this.clearOverrideTexture();
        }

        return render;
    }

    @Override
    public boolean renderItemIn3d() {
        return false;
    }
}
