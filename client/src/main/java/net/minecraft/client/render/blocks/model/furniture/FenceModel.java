package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Fence;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class FenceModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Fence fence = (Fence)block;
        boolean render = false;
        float startWidth = 0.375F;
        float endWidth = 0.625F;
        int meta = this.world.getBlockMetadata(x, y, z);
        if (!this.hasOverrideTexture()) {
            if (meta >= 128) {
                meta -= 128;
                this.setOverrideTexture(BlockList.log.getIcon(2, meta));
            } else {
                this.setOverrideTexture(BlockList.wood.getIcon(2, meta));
            }
        }

        block.setBounds(startWidth, 0.0, startWidth, endWidth, 1.0, endWidth);
        render = this.renderStandardBlock(block, x, y, z);
        if (!this.hasOverrideTexture() || this.textureOverride == BlockList.wood.getIcon(2, meta) || this.textureOverride == BlockList.log.getIcon(2, meta)) {
            this.clearOverrideTexture();
        }

        boolean negXConn = fence.connectsTo(this.world, x - 1, y, z);
        boolean posXConn = fence.connectsTo(this.world, x + 1, y, z);
        boolean negZConn = fence.connectsTo(this.world, x, y, z - 1);
        boolean posZConn = fence.connectsTo(this.world, x, y, z + 1);
        startWidth = 0.4375F;
        endWidth = 0.5625F;
        float startHeight = 0.75F;
        float endHeight = 0.9375F;
        float topPoleStart = negXConn ? 0.0F : startWidth;
        float topPoleEnd = posXConn ? 1.0F : endWidth;
        float bottomPoleStart = negZConn ? 0.0F : startWidth;
        float bottomPoleEnd = posZConn ? 1.0F : endWidth;
        if (posXConn || negXConn) {
            block.setBounds(topPoleStart, startHeight, startWidth, topPoleEnd, endHeight, endWidth);
            render = this.renderStandardBlock(block, x, y, z);
        }

        if (posZConn || negZConn) {
            block.setBounds(startWidth, startHeight, bottomPoleStart, endWidth, endHeight, bottomPoleEnd);
            render = this.renderStandardBlock(block, x, y, z);
        }

        startHeight = 0.375F;
        endHeight = 0.5625F;
        if (posXConn || negXConn) {
            block.setBounds(topPoleStart, startHeight, startWidth, topPoleEnd, endHeight, endWidth);
            render = this.renderStandardBlock(block, x, y, z);
        }

        if (posZConn || negZConn) {
            block.setBounds(startWidth, startHeight, bottomPoleStart, endWidth, endHeight, bottomPoleEnd);
            render = this.renderStandardBlock(block, x, y, z);
        }

        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Icon icn = BlockList.wood.getIcon(2, dat);
        Tessellator tess = Tessellator.instance;
        if (dat >= 128) {
            dat -= 128;
            icn = BlockList.log.getIcon(2, dat);
        }

        for (int part = 0; part < 4; part++) {
            float offset = 0.125F;
            if (part == 0) {
                block.setBounds(0.5F - offset, 0.0, 0.0, 0.5F + offset, 1.0, offset * 2.0F);
            }

            if (part == 1) {
                block.setBounds(0.5F - offset, 0.0, 1.0F - offset * 2.0F, 0.5F + offset, 1.0, 1.0);
            }

            if ((part == 0 || part == 1) && !this.hasOverrideTexture()) {
                this.setOverrideTexture(icn);
            }

            offset = 0.0625F;
            if (part == 2) {
                block.setBounds(0.5F - offset, 1.0F - offset * 3.0F, -offset * 2.0F, 0.5F + offset, 1.0F - offset, 1.0F + offset * 2.0F);
            }

            if (part == 3) {
                block.setBounds(0.5F - offset, 0.5F - offset * 3.0F, -offset * 2.0F, 0.5F + offset, 0.5F - offset, 1.0F + offset * 2.0F);
            }

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.begin();
            tess.setNormals(0.0F, -1.0F, 0.0F);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            this.clearOverrideTexture();
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
}
