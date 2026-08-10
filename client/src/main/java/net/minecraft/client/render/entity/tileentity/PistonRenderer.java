package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.machines.Piston;
import net.minecraft.game.block.tileentity.other.PistonEntity;
import org.lwjgl.opengl.GL11;

public class PistonRenderer extends TileEntityRenderer<PistonEntity> {
    public void render(PistonEntity piston, double x, double y, double z, float tick) {
        Block block = BlockList.blocks[piston.getStoredBlock()];
        if (block != null && piston.getProgress(tick) < 1.0F) {
            Tessellator tess = Tessellator.instance;
            this.bindTexture("/terrain.png");
            Lighting.disable();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(2884);
            if (OptionValues.smoothLighting) {
                GL11.glShadeModel(7425);
            } else {
                GL11.glShadeModel(7424);
            }

            tess.begin();
            tess.setTranslation(x - piston.x + piston.getOffsetX(tick), y - piston.y + piston.getOffsetY(tick), z - piston.z + piston.getOffsetZ(tick));
            tess.setColor(1, 1, 1);
            if (block == BlockList.pulleyExtension && piston.getProgress(tick) < 0.5F) {
                BlocksRenderer.pistonHeadModel.renderPistonHead(block, piston.x, piston.y, piston.z, false);
            } else if (piston.shouldRenderHead() && !piston.isExtending()) {
                BlockList.pulleyExtension.setHeadTexture(((Piston)block).extensionTexture(), ((Piston)block).sideTexture());
                BlocksRenderer.pistonHeadModel.isGold = ((Piston)block).sticky;
                BlocksRenderer.pistonHeadModel.renderPistonHead(BlockList.pulleyExtension, piston.x, piston.y, piston.z, piston.getProgress(tick) < 0.5F);
                BlocksRenderer.pistonHeadModel.isGold = false;
                BlockList.pulleyExtension.clearHeadTexture();
                tess.setTranslation(x - piston.x, y - piston.y, z - piston.z);
                BlocksRenderer.pistonModel.renderPiston(block, piston.x, piston.y, piston.z, true);
            } else {
                BlocksRenderer.pistonModel.render(block, piston.x, piston.y, piston.z);
            }

            tess.setTranslation(0.0, 0.0, 0.0);
            tess.draw();
            Lighting.enable();
        }
    }
}
