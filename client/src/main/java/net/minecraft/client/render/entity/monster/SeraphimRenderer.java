package net.minecraft.client.render.entity.monster;

import java.util.Random;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.elemental.SeraphimModel;
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class SeraphimRenderer extends MobRenderer<Seraphim> {
    public static Seraphim seraph;
    protected SeraphimModel model = (SeraphimModel)this.mainModel;

    public SeraphimRenderer() {
        super(new SeraphimModel(), 0.5F);
        this.setOverlay(this.model);
    }

    protected void renderModel(Seraphim seraph, float swing, float speed, float angle, float yaw, float pitch, float tick) {
        if (seraph.deathTimer > 0) {
            float opacity = seraph.deathTimer / 200.0F;
            GL11.glDepthFunc(515);
            GL11.glEnable(3008);
            GL11.glAlphaFunc(516, opacity);
            this.loadSkin(seraph.skinUrl, "/mob/enderdragon/shuffle.png");
            this.mainModel.render(seraph, swing, speed, angle, yaw, pitch, tick);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glDepthFunc(514);
        }

        this.loadSkin(seraph.skinUrl, seraph.getTexture());
        this.mainModel.render(seraph, swing, speed, angle, yaw, pitch, tick);
        if (seraph.hurtTime > 0) {
            GL11.glDepthFunc(514);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
            this.mainModel.render(seraph, swing, speed, angle, yaw, pitch, tick);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDepthFunc(515);
        }
    }

    public void render(Seraphim entity, double x, double y, double z, float offset, float tick) {
        seraph = entity;
        super.render(entity, x, y, z, offset, tick);
        if (entity.healingCrystal != null) {
            float rotation = entity.healingCrystal.rotation + tick;
            float facing = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
            facing = (facing * facing + facing) * 0.2F;
            float distX = (float)(entity.healingCrystal.posX - entity.posX - (entity.prevX - entity.posX) * (1.0F - tick));
            float distY = (float)(facing + entity.healingCrystal.posY - 1.0 - entity.posY - (entity.prevY - entity.posY) * (1.0F - tick));
            float distZ = (float)(entity.healingCrystal.posZ - entity.posZ - (entity.prevZ - entity.posZ) * (1.0F - tick));
            float sqrtDist1 = MathHelper.sqrt_float(distX * distX + distZ * distZ);
            float sqrtDist2 = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x, (float)y + 2.0F, (float)z);
            GL11.glRotatef((float)(-Math.atan2(distZ, distX)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-Math.atan2(sqrtDist1, distY)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
            Tessellator tess = Tessellator.instance;
            Lighting.disable();
            GL11.glDisable(2884);
            this.loadTexture("/entity/endcrystal/endercrystal_beam.png");
            GL11.glShadeModel(7425);
            float aliveTime = 0.0F - (entity.ticksExisted + tick) * 0.01F;
            float distance = MathHelper.sqrt_float(distX * distX + distY * distY + distZ * distZ) / 32.0F - (entity.ticksExisted + tick) * 0.01F;
            tess.begin(5);
            byte passes = 8;

            for (int pass = 0; pass <= passes; pass++) {
                float u = MathHelper.sin(pass % passes * (float) Math.PI * 2.0F / passes) * 0.75F;
                float v = MathHelper.cos(pass % passes * (float) Math.PI * 2.0F / passes) * 0.75F;
                float transparency = pass % passes * 1.0F / passes;
                tess.setColor(0);
                tess.addTexturedVertex(u * 0.2F, v * 0.2F, 0.0, transparency, distance);
                tess.setColor(16777215);
                tess.addTexturedVertex(u, v, sqrtDist2, transparency, aliveTime);
            }

            tess.draw();
            GL11.glEnable(2884);
            GL11.glShadeModel(7424);
            Lighting.enable();
            GL11.glPopMatrix();
        }
    }

    protected boolean renderOverlay(Seraphim entity, int layer, float tick) {
        if (layer == 1) {
            GL11.glDepthFunc(515);
            if (entity.healingCrystal != null || entity.health <= entity.maxHealth / 4 && entity.deathTimer <= 0 && entity.boss) {
                GL11.glPushMatrix();
                float aliveTime = entity.ticksExisted + tick;
                float offsetX = aliveTime * 0.01F;
                float offsetY = aliveTime * 0.01F;
                float brightness = 0.5F;
                this.loadTexture("/armor/power.png");
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glTranslatef(offsetX, offsetY, 0.0F);
                this.setOverlay(this.model);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                GL11.glColor4f(brightness, brightness, brightness, 1.0F);
                GL11.glDisable(2896);
                GL11.glBlendFunc(1, 1);
                GL11.glPopMatrix();
                return true;
            }
        }

        if (layer == 2) {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
        }

        return layer == 0;
    }

    protected void renderEquippedItems(Seraphim entity, float tick) {
        Tessellator tess = Tessellator.instance;
        if (entity.deathTimer > 0) {
            Lighting.disable();
            float deathTimer = (entity.deathTimer + tick) / 200.0F;
            float max = 0.0F;
            if (deathTimer > 0.8F) {
                max = (deathTimer - 0.8F) / 0.2F;
            }

            Random random = new Random(432L);
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glDisable(3008);
            GL11.glEnable(2884);
            GL11.glDepthMask(false);
            GL11.glPushMatrix();

            for (int index = 0; index < (deathTimer + deathTimer * deathTimer) / 2.0F * 60.0F; index++) {
                float u = random.nextFloat() * 20.0F + 5.0F + max * 10.0F;
                float v = random.nextFloat() * 2.0F + 1.0F + max * 2.0F;
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F + deathTimer * 90.0F, 0.0F, 0.0F, 1.0F);
                tess.begin(6);
                tess.setColorAlpha(16777215, (int)(255.0F * (1.0F - max)));
                tess.addVertex(0.0, 0.0, 0.0);
                tess.setColorAlpha(16711935, 0);
                tess.addVertex(-0.866 * v, u, -0.5F * v);
                tess.addVertex(0.866 * v, u, -0.5F * v);
                tess.addVertex(0.0, u, 1.0F * v);
                tess.addVertex(-0.866 * v, u, -0.5F * v);
                tess.draw();
            }

            GL11.glPopMatrix();
            GL11.glDepthMask(true);
            GL11.glDisable(2884);
            GL11.glDisable(3042);
            GL11.glShadeModel(7424);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            Lighting.enable();
        }
    }
}
