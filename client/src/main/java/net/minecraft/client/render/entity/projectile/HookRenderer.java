package net.minecraft.client.render.entity.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Hook;
import net.minecraft.game.item.tools.GrapplingHook;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class HookRenderer extends EntityRenderer<Hook> {
    public void render(Hook entity, double x, double y, double z, float yaw, float pitch) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(32826);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        float scale = 0.8F;
        GL11.glScalef(scale, scale, scale);
        this.loadTexture("/gui/items.png");
        float minU = GrapplingHook.hook.getMinU();
        float maxU = GrapplingHook.hook.getMaxU();
        float minV = GrapplingHook.hook.getMinV();
        float maxV = GrapplingHook.hook.getMaxV();
        GL11.glRotatef(180.0F - this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.1F, -0.1F, 0.0F);
        tessellator.begin();
        tessellator.setNormals(0.0F, 1.0F, 0.0F);
        tessellator.addTexturedVertex(-0.5, -0.25, 0.0, minU, maxV);
        tessellator.addTexturedVertex(0.5, -0.25, 0.0, maxU, maxV);
        tessellator.addTexturedVertex(0.5, 0.75, 0.0, maxU, minV);
        tessellator.addTexturedVertex(-0.5, 0.75, 0.0, minU, minV);
        tessellator.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        if (entity.thrower != null) {
            float swing = entity.thrower.getSwing(pitch);
            float angle = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
            int retracted = entity.thrower instanceof Player && ((Player)entity.thrower).inventory.canUseItem() ? -1 : 1;
            Vector length = Vector.createVector(-0.65 * retracted, 0.03, 0.8);
            length.rotateAroundX(-(entity.thrower.prevPitch + (entity.thrower.pitch - entity.thrower.prevPitch) * pitch) * (float) Math.PI / 180.0F);
            length.rotateAroundY(-(entity.thrower.prevYaw + (entity.thrower.yaw - entity.thrower.prevYaw) * pitch) * (float) Math.PI / 180.0F);
            length.rotateAroundY(angle * 0.5F);
            length.rotateAroundX(-angle * 0.7F);
            double endX = entity.thrower.prevX + (entity.thrower.posX - entity.thrower.prevX) * pitch + length.xCoord;
            double endY = entity.thrower.prevY + (entity.thrower.posY - entity.thrower.prevY) * pitch + length.yCoord;
            double endZ = entity.thrower.prevZ + (entity.thrower.posZ - entity.thrower.prevZ) * pitch + length.zCoord;
            double eyeHeight = entity.thrower != Minecraft.getMinecraft().player ? entity.thrower.getEyeHeight() : 0.0;
            if (OptionValues.cameraMode > 0 || entity.thrower != Minecraft.getMinecraft().player) {
                float offset = (entity.thrower.prevYawOffset + (entity.thrower.yawOffset - entity.thrower.prevYawOffset) * pitch) * (float) Math.PI / 180.0F;
                endX = entity.thrower.prevX
                    + (entity.thrower.posX - entity.thrower.prevX) * pitch
                    - MathHelper.cos(offset) * 0.35
                    - MathHelper.sin(offset) * 0.85;
                endY = entity.thrower.prevY + eyeHeight + (entity.thrower.posY - entity.thrower.prevY) * pitch - 0.45;
                endZ = entity.thrower.prevZ
                    + (entity.thrower.posZ - entity.thrower.prevZ) * pitch
                    - MathHelper.sin(offset) * 0.35
                    + MathHelper.cos(offset) * 0.85;
            }

            double startX = entity.prevX + (entity.posX - entity.prevX) * pitch;
            double startY = entity.prevY + (entity.posY - entity.prevY) * pitch + 0.25;
            double startZ = entity.prevZ + (entity.posZ - entity.prevZ) * pitch;
            double despX = endX - startX;
            double despY = endY - startY;
            double despZ = endZ - startZ;
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            double hang = entity.getRadius() - entity.getDistanceTo(entity.thrower);
            if (hang < 0.2) {
                hang = 0.0;
            }

            tessellator.begin(5);

            for (int pass = 0; pass <= 24; pass++) {
                if (pass % 2 == 0) {
                    tessellator.setColorAlpha(0.4F, 0.4F, 0.4F, 1.0F);
                } else {
                    tessellator.setColorAlpha(0.2F, 0.2F, 0.2F, 1.0F);
                }

                float offset = pass / 24.0F;
                tessellator.addVertex(x + despX * offset + 0.0, y + despY * (offset * offset + offset) * 0.5, z + despZ * offset);
                tessellator.addVertex(x + despX * offset + 0.025, y + despY * (offset * offset + offset) * 0.5 + 0.025, z + despZ * offset);
            }

            tessellator.draw();
            tessellator.begin(5);

            for (int var46 = 0; var46 <= 24; var46++) {
                if (var46 % 2 == 0) {
                    tessellator.setColorAlpha(0.4F, 0.4F, 0.4F, 1.0F);
                } else {
                    tessellator.setColorAlpha(0.2F, 0.2F, 0.2F, 1.0F);
                }

                float offset = var46 / 24.0F;
                tessellator.addVertex(x + despX * offset + 0.0, y + despY * (offset * offset + offset) * 0.5 + 0.025, z + despZ * offset);
                tessellator.addVertex(x + despX * offset + 0.025, y + despY * (offset * offset + offset) * 0.5, z + despZ * offset + 0.025);
            }

            tessellator.draw();
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
    }
}
