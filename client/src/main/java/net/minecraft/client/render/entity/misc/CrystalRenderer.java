package net.minecraft.client.render.entity.misc;

import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.misc.CrystalModel;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class CrystalRenderer extends EntityRenderer<VoidCrystal> {
    private Model crystalModel = new CrystalModel();

    public CrystalRenderer() {
        this.shadowSize = 0.75F;
    }

    public void render(VoidCrystal crystal, double x, double y, double z, float offset, float tick) {
        float rotation = crystal.rotation + tick;
        float facing = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        facing += facing * facing;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y - 0.5F, (float)z);
        this.loadTexture("/entity/endcrystal/endercrystal.png");
        this.crystalModel.render(crystal, 0.0F, rotation * 3.0F, facing * 0.2F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        if (crystal.playerPlaced && crystal.beamTarget != null && crystal.getDistanceTo(crystal.beamTarget) < 32.0F) {
            float yaw = crystal.beamTarget.yaw + tick;
            facing = MathHelper.sin(yaw * 0.2F) / 2.0F + 0.5F;
            facing = (facing * facing + facing) * 0.2F;
            float distX = (float)(crystal.beamTarget.posX - crystal.posX - (crystal.prevX - crystal.posX) * (1.0F - tick));
            float distY = (float)(facing + crystal.beamTarget.posY - crystal.posY - (crystal.prevY - crystal.posY) * (1.0F - tick));
            float distZ = (float)(crystal.beamTarget.posZ - crystal.posZ - (crystal.prevZ - crystal.posZ) * (1.0F - tick));
            float distance = MathHelper.sqrt_float(distX * distX + distZ * distZ);
            float distanceVert = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ);
            float aliveTime = 0.0F - (crystal.ticksExisted + tick) * 0.01F;
            float beamLength = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ) / 32.0F - (crystal.ticksExisted + tick) * 0.01F;
            byte passes = 8;
            Tessellator tess = Tessellator.instance;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y + 1.0, z);
            GL11.glRotatef((float)(-Math.atan2(distZ, distX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-Math.atan2(distance, distY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
            Lighting.disable();
            GL11.glDisable(2884);
            this.loadTexture("/entity/endcrystal/endercrystal_beam.png");
            GL11.glShadeModel(7425);
            tess.begin(5);

            for (int renderPass = 0; renderPass <= passes; renderPass++) {
                float beamStart = MathHelper.sin(renderPass % passes * (float) Math.PI * 2.0F / passes) * 0.75F;
                float beamEnd = MathHelper.cos(renderPass % passes * (float) Math.PI * 2.0F / passes) * 0.75F;
                float animFrame = renderPass % passes * 1.0F / passes;
                tess.setColor(0);
                tess.addTexturedVertex(beamStart * 0.2F, beamEnd * 0.2F, 0.0, animFrame, beamLength);
                tess.setColor(16777215);
                tess.addTexturedVertex(beamStart, beamEnd, distanceVert, animFrame, aliveTime);
            }

            tess.draw();
            GL11.glEnable(2884);
            GL11.glShadeModel(7424);
            Lighting.enable();
            GL11.glPopMatrix();
        }
    }
}
