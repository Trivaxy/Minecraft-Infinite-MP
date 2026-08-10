package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.elemental.ElementalModel;
import net.minecraft.game.entity.mob.monster.elemental.Flood;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ElementalRenderer extends MobRenderer<Flood> {
    public ElementalRenderer() {
        super(new ElementalModel(), 0.6F);
    }

    public void render(Flood entity, double x, double y, double z, float offset, float tick) {
        super.render(entity, x, y, z, offset, tick);
        if (entity.attackTarget != null && entity.isVisible(entity.attackTarget) && entity.getDistanceTo(entity.attackTarget) < 32.0F) {
            float yaw = entity.attackTarget.yaw + tick;
            float facing = MathHelper.sin(yaw * 0.2F) / 2.0F + 0.5F;
            facing = (facing * facing + facing) * 0.2F;
            float distX = (float)(entity.attackTarget.posX - entity.posX - (entity.prevX - entity.posX) * (1.0F - tick));
            float distY = (float)(facing + entity.attackTarget.posY - 3.0 - entity.posY - (entity.prevY - entity.posY) * (1.0F - tick));
            float distZ = (float)(entity.attackTarget.posZ - entity.posZ - (entity.prevZ - entity.posZ) * (1.0F - tick));
            float distance = MathHelper.sqrt_float(distX * distX + distZ * distZ);
            float distanceVert = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ);
            float aliveTime = 0.0F - (entity.ticksExisted + tick) * 0.01F;
            float beamLength = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ) / 32.0F - (entity.ticksExisted + tick) * 0.01F;
            byte passes = 4;
            Tessellator tess = Tessellator.instance;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z);
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
