package net.minecraft.client.render.entity.misc;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.PrimedTNT;
import org.lwjgl.opengl.GL11;

public class PrimedTNTRenderer extends EntityRenderer<PrimedTNT> {
    public PrimedTNTRenderer() {
        this.setShadowSize(0.5F);
    }

    public void render(PrimedTNT entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        if (entity.fuse - pitch + 1.0F < 10.0F) {
            float scaleOffset = 1.0F - (entity.fuse - pitch + 1.0F) / 10.0F;
            if (scaleOffset < 0.0F) {
                scaleOffset = 0.0F;
            }

            if (scaleOffset > 1.0F) {
                scaleOffset = 1.0F;
            }

            scaleOffset *= scaleOffset;
            scaleOffset *= scaleOffset;
            float scale = 1.0F + scaleOffset * 0.3F;
            GL11.glScalef(scale, scale, scale);
        }

        float scaleOffset = (1.0F - (entity.fuse - pitch + 1.0F) / 100.0F) * 0.8F;
        this.loadTexture("/terrain.png");
        BlocksRenderer.baseModel.renderInGUI(BlockList.tnt, 0);
        if (entity.fuse / 5 % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, scaleOffset);
            GL11.glEnable(32823);
            GL11.glPolygonOffset(-1.0F, -1.0F);
            BlocksRenderer.baseModel.renderInGUI(BlockList.tnt, 0);
            GL11.glDisable(32823);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }

        GL11.glPopMatrix();
    }
}
