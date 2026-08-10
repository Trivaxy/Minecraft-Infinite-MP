package net.minecraft.client.render.entity.projectile;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BobberRenderer extends EntityRenderer<Bobber> {
    public void render(Bobber bobber, double x, double y, double z, float offset, float tick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(32826);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.loadTexture("/particles/particles.png");
        Tessellator tess = Tessellator.instance;
        byte horizontalOffset = 1;
        byte verticalOffset = 2;
        float startU = horizontalOffset * 8 / 128.0F;
        float startV = (horizontalOffset * 8 + 8) / 128.0F;
        float endU = verticalOffset * 8 / 128.0F;
        float endV = (verticalOffset * 8 + 8) / 128.0F;
        float width = 1.0F;
        float length = 0.5F;
        float height = 0.5F;
        GL11.glRotatef(180.0F - this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
        tess.begin();
        tess.setNormals(0.0F, 1.0F, 0.0F);
        tess.addTexturedVertex(0.0F - length, 0.0F - height, 0.0, startU, endV);
        tess.addTexturedVertex(width - length, 0.0F - height, 0.0, startV, endV);
        tess.addTexturedVertex(width - length, 1.0F - height, 0.0, startV, endU);
        tess.addTexturedVertex(0.0F - length, 1.0F - height, 0.0, startU, endU);
        tess.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        if (bobber.angler != null) {
            float angle = (bobber.angler.prevYaw + (bobber.angler.yaw - bobber.angler.prevYaw) * tick) * (float) Math.PI / 180.0F;
            double yaw = MathHelper.sin(angle);
            double pitch = MathHelper.cos(angle);
            float swing = bobber.angler.getSwing(tick);
            float off = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
            int retracted = bobber.angler.inventory.canUseItem() ? -1 : 1;
            Vector vector = Vector.createVector(-0.65 * retracted, 0.03, 0.8);
            vector.rotateAroundX(-(bobber.angler.prevPitch + (bobber.angler.pitch - bobber.angler.prevPitch) * tick) * (float) Math.PI / 180.0F);
            vector.rotateAroundY(-(bobber.angler.prevYaw + (bobber.angler.yaw - bobber.angler.prevYaw) * tick) * (float) Math.PI / 180.0F);
            vector.rotateAroundY(off * 0.5F);
            vector.rotateAroundX(-off * 0.7F);
            double startX = bobber.angler.prevX + (bobber.angler.posX - bobber.angler.prevX) * tick + vector.xCoord;
            double startY = bobber.angler.prevY + (bobber.angler.posY - bobber.angler.prevY) * tick + vector.yCoord;
            double startZ = bobber.angler.prevZ + (bobber.angler.posZ - bobber.angler.prevZ) * tick + vector.zCoord;
            if (OptionValues.cameraMode > 0) {
                angle = (bobber.angler.prevYawOffset + (bobber.angler.yawOffset - bobber.angler.prevYawOffset) * tick) * (float) Math.PI / 180.0F;
                yaw = MathHelper.sin(angle);
                pitch = MathHelper.cos(angle);
                startX = bobber.angler.prevX + (bobber.angler.posX - bobber.angler.prevX) * tick - pitch * 0.35 - yaw * 0.85;
                startY = bobber.angler.prevY + (bobber.angler.posY - bobber.angler.prevY) * tick - 0.45;
                startZ = bobber.angler.prevZ + (bobber.angler.posZ - bobber.angler.prevZ) * tick - yaw * 0.35 + pitch * 0.85;
            }

            double endX = bobber.prevX + (bobber.posX - bobber.prevX) * tick;
            double endY = bobber.prevY + (bobber.posY - bobber.prevY) * tick + 0.25;
            double endZ = bobber.prevZ + (bobber.posZ - bobber.prevZ) * tick;
            double distX = (float)(startX - endX);
            double distY = (float)(startY - endY);
            double distZ = (float)(startZ - endZ);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            tess.begin(3);
            tess.setColor(0);
            byte passes = 16;

            for (int pass = 0; pass <= passes; pass++) {
                float point = (float)pass / passes;
                tess.addVertex(x + distX * point, y + distY * (point * point + point) * 0.5 + 0.25, z + distZ * point);
            }

            tess.draw();
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
    }
}
