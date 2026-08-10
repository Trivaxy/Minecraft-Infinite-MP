package net.minecraft.client.render.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.client.render.entity.model.bipedal.BipedalModel;
import net.minecraft.client.render.entity.model.bipedal.SkeletonModel;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.mob.monster.boss.Guardian;
import net.minecraft.game.entity.mob.monster.boss.Witch;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.game.world.pathfinder.PathNode;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class MobRenderer<T extends Mob> extends EntityRenderer<T> {
    protected Model mainModel;
    protected Model overlayModel;
    protected Model accessoryModel;

    public MobRenderer(Model mainModel, float shadowSize) {
        this.mainModel = mainModel;
        this.setShadowSize(shadowSize);
    }

    public void setOverlay(Model renderPassModel) {
        this.overlayModel = renderPassModel;
    }

    public final void setAccessories(Model modelBase) {
        this.accessoryModel = modelBase;
    }

    public void render(T entity, double x, double y, double z, float offset, float tick) {
        if (!entity.hasEffect(EffectList.invisibility)) {
            GL11.glPushMatrix();
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            this.mainModel.swingMain = this.getSwing(entity, tick);
            this.mainModel.swingOff = this.getSwingOffhand(entity, tick);
            if (this.overlayModel != null) {
                this.overlayModel.swingMain = this.mainModel.swingMain;
            }

            if (this.accessoryModel != null) {
                this.accessoryModel.swingMain = this.mainModel.swingMain;
            }

            try {
                float yaw = entity.prevYawOffset + (entity.yawOffset - entity.prevYawOffset) * tick;
                float yawHead = entity.prevFacing + (entity.headFacing - entity.prevFacing) * tick;
                float pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * tick;
                this.transform(entity, x, y, z);
                float lookAngle = this.getDefaultAngle(entity, tick);
                this.rotate(entity, lookAngle, yaw, tick);
                float defaultOffset = 0.0625F;
                GL11.glEnable(32826);
                GL11.glScalef(-1.0F, -1.0F, 1.0F);
                this.preRender(entity, tick);
                GL11.glTranslatef(0.0F, -24.0F * defaultOffset - 0.0078125F, 0.0F);
                float limbYaw = entity.prevLimbYaw + (entity.limbYaw - entity.prevLimbYaw) * tick;
                float limbSwing = entity.limbSwing - entity.limbYaw * (1.0F - tick);
                if (limbYaw > 1.0F) {
                    limbYaw = 1.0F;
                }

                this.loadSkin(entity.skinUrl, entity.getTexture());
                this.setOverlayColor(entity, tick);
                this.mainModel.animateSpecial(entity, limbSwing, limbYaw, tick);
                this.renderOutline(entity, limbSwing, limbYaw, lookAngle, yawHead - yaw, pitch, defaultOffset);
                this.renderModel(entity, limbSwing, limbYaw, lookAngle, yawHead - yaw, pitch, defaultOffset);

                for (int type = 0; type < 4; type++) {
                    if (this.renderOverlay(entity, type, tick)) {
                        this.overlayModel.animateSpecial(entity, limbSwing, limbYaw, tick);
                        this.overlayModel.render(entity, limbSwing, limbYaw, lookAngle, yawHead - yaw, pitch, defaultOffset);
                        this.renderGlint(entity, type, limbSwing, limbYaw, lookAngle, yawHead, yaw, pitch, defaultOffset, tick);
                        GL11.glDisable(3042);
                        GL11.glEnable(3008);
                    }

                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                    if (this.renderAccessories(entity, type)) {
                        this.accessoryModel.render(entity, limbSwing, limbYaw, lookAngle, yawHead - yaw, pitch, defaultOffset);
                        GL11.glDisable(3042);
                        GL11.glEnable(3008);
                    }
                }

                GL11.glTexEnvi(8960, 8704, 8448);
                this.renderEquippedItems(entity, tick);
                GL11.glDisable(32826);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            GL11.glDisable(3042);
            GL11.glEnable(2884);
            GL11.glPopMatrix();
            this.renderLabels(entity, x, y, z);
            this.renderPath(entity);
        }
    }

    private void renderOutline(T entity, float limbSwing, float limbYaw, float lookAngle, float yawHead, float pitch, float defaultOffset) {
        if (entity.hasEffect(EffectList.glowing)
            || Minecraft.getMinecraft().player.inventory.accessorySlots[3] != null
                && Minecraft.getMinecraft().player.inventory.accessorySlots[3].getItem() == ItemList.spectacles) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(2884);
            GL11.glCullFace(1028);
            GL11.glPolygonMode(1029, 6914);
            GL11.glDisable(2929);
            GL11.glPushMatrix();
            GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
            GL11.glScalef(1.05F, 1.05F, 1.05F);
            this.renderModel(entity, limbSwing, limbYaw, lookAngle, yawHead, pitch, defaultOffset);
            GL11.glPopMatrix();
            GL11.glEnable(2929);
            GL11.glPopAttrib();
        }
    }

    protected void setOverlayColor(T entity, float tick) {
        float brightness = entity.getBrightness(tick);
        int color = this.getColorMultiplier(entity, brightness, tick);
        GL11.glEnable(3008);
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;
        if (entity.hurtTime > 0 || entity.deathTimer > 0) {
            red = 1.0F;
            green = 0.5F;
            blue = 0.5F;
        }

        if (entity.freeze > 0) {
            red = 0.5F;
            green = 0.5F;
            blue = 1.0F;
        }

        if (entity.hasEffect(EffectList.poison)) {
            red = 0.5F;
            green = 1.0F;
            blue = 0.5F;
        }

        if ((color >> 24 & 0xFF) > 0) {
            float flashAlpha = color >> 24 & 0xFF;
            red = (color >> 16 & 0xFF) * flashAlpha;
            green = (color >> 8 & 0xFF) * flashAlpha;
            blue = (color & 0xFF) * flashAlpha;
        }

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(3553);
        GL11.glTexEnvi(8960, 8704, 34160);
        GL11.glTexEnvi(8960, 34161, 8448);
        GL11.glTexEnvi(8960, 34176, 5890);
        GL11.glTexEnvi(8960, 34177, 34167);
        GL11.glTexEnvi(8960, 34192, 768);
        GL11.glTexEnvi(8960, 34193, 768);
        GL11.glTexEnvi(8960, 34162, 7681);
        GL11.glTexEnvi(8960, 34184, 5890);
    }

    protected boolean renderOverlay(T entity, int type, float tick) {
        return false;
    }

    protected boolean renderAccessories(T entity, int type) {
        return false;
    }

    protected boolean renderGlint(
        T entity, int type, float limbSwing, float limbYaw, float lookAngle, float yawHead, float yaw, float pitch, float defaultOffset, float tick
    ) {
        return false;
    }

    protected void renderModel(T entity, float limbSwing, float animSpeed, float angle, float yaw, float pitch, float tick) {
        this.mainModel.render(entity, limbSwing, animSpeed, angle, yaw, pitch, tick);
    }

    protected float getDefaultAngle(T entity, float tick) {
        return entity.ticksExisted + tick;
    }

    protected int getColorMultiplier(T entity, float brightness, float color) {
        return 0;
    }

    protected void preRender(T entity, float tick) {
        if (entity.boss && !(entity instanceof Witch) && !(entity instanceof Guardian)) {
            GL11.glScalef(1.2F, 1.2F, 1.2F);
        } else {
            GL11.glScalef(1.0F, 1.0F, 1.0F);
        }
    }

    protected void renderEquippedItems(T entity, float tick) {
        if (this.mainModel instanceof BipedalModel && entity instanceof Monster) {
            Monster mob = (Monster)entity;
            BipedalModel model = (BipedalModel)this.mainModel;
            if (mob.heldItem != null) {
                float scale = 0.55F;
                boolean hasThinArms = this.mainModel instanceof SkeletonModel;
                GL11.glPushMatrix();
                model.rightArm.applyRotation(0.0625F);
                model.heldItemRight = true;
                GL11.glTranslatef(hasThinArms ? -0.0625F : -0.1625F, 0.4375F, mob.heldItem == ItemList.bow ? 0.0625F : -0.225F);
                GL11.glTranslatef(0.1F, 0.15F, 0.3F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                this.renderManager.itemRenderer.renderItemModel(new ItemStack(mob.heldItem));
                GL11.glPopMatrix();
            }

            this.renderArrows(entity, tick);
        }
    }

    protected void renderArrows(Mob entity, float tick) {
        int arrows = entity.getArrows();
        if (arrows > 0) {
            Arrow arrow = new Arrow(entity.world, entity.posX, entity.posY, entity.posZ);
            Random rand = new Random(entity.entityId);
            Lighting.disable();

            for (int arrowCount = 0; arrowCount < arrows; arrowCount++) {
                GL11.glPushMatrix();
                ModelPart modelPart = this.mainModel.getRandomModelPart(rand);
                Cuboid cuboid = modelPart.cuboids.get(rand.nextInt(modelPart.cuboids.size()));
                modelPart.applyRotation(0.0625F);
                double randX = rand.nextDouble();
                double randY = rand.nextDouble();
                double randZ = rand.nextDouble();
                double x = (cuboid.posX1 + (cuboid.posX2 - cuboid.posX1) * randX) / 16.0;
                double y = (cuboid.posY1 + (cuboid.posY2 - cuboid.posY1) * randY) / 16.0;
                double z = (cuboid.posZ1 + (cuboid.posZ2 - cuboid.posZ1) * randZ) / 16.0;
                GL11.glTranslated(x, y, z);
                randX = randX * 2.0 - 1.0;
                randY = randY * 2.0 - 1.0;
                randZ = randZ * 2.0 - 1.0;
                randX *= -1.0;
                randY *= -1.0;
                randZ *= -1.0;
                double angle = MathHelper.sqrt_double(randX * randX + randZ * randZ);
                arrow.prevYaw = arrow.yaw = (float)(Math.atan2(randX, randZ) * 180.0 / Math.PI);
                arrow.prevPitch = arrow.pitch = (float)(Math.atan2(randY, angle) * 180.0 / Math.PI);
                this.renderManager.renderEntityAt(arrow, 0.0, 0.0, 0.0, 0.0F, tick);
                GL11.glPopMatrix();
            }

            Lighting.enable();
        }
    }

    protected float getSwing(T entity, float tick) {
        return entity.getSwing(tick);
    }

    protected float getSwingOffhand(T entity, float tick) {
        return entity.getSwingOffhand(tick);
    }

    protected void transform(T entity, double x, double y, double z) {
        GL11.glTranslated(x, y, z);
    }

    protected void rotate(T entity, float x, float y, float z) {
        GL11.glRotatef(180.0F - y, 0.0F, 1.0F, 0.0F);
        if (entity.deathTimer > 0) {
            float deathTime = (entity.deathTimer + z - 1.0F) / 20.0F * 1.6F;
            deathTime = MathHelper.sqrt_float(deathTime);
            if (deathTime > 1.0F) {
                deathTime = 1.0F;
            }

            GL11.glRotatef(deathTime * this.maxRotation(entity), 0.0F, 0.0F, 1.0F);
        }
    }

    protected float maxRotation(T entityLiving) {
        return 90.0F;
    }

    protected void renderLabels(T entity, double xoffset, double yoffset, double zoffset) {
        if (!entity.customName.isEmpty()) {
            this.renderLabel(entity, entity.customName, xoffset, yoffset, zoffset, 32);
        }

        if (Minecraft.isDebugScreenFull()) {
            this.renderLabel(entity, Integer.toString(entity.entityId), xoffset, yoffset + 0.5, zoffset, 64);
        }
    }

    protected void renderLabel(T entity, String label, double xoffset, double yoffset, double zoffset, int distance) {
        float eDistance = entity.getDistanceTo(this.renderManager.player);
        if (eDistance <= distance && (this.renderManager.player.isVisible(entity) || entity instanceof Player)) {
            Font font = this.renderManager.getFont();
            Tessellator tess = Tessellator.instance;
            int nameWidth = font.getStringWidth(label) / 2;
            float scale = 0.02666667F;
            scale = (float)(scale * (Math.sqrt(eDistance) / 2.0));
            GL11.glPushMatrix();
            GL11.glTranslatef((float)xoffset, (float)yoffset + entity.getEyeHeight() + entity.getEyeHeight() / 2.0F, (float)zoffset);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-scale, -scale, scale);
            GL11.glDisable(2896);
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            tess.begin();
            tess.setColorAlpha(0.0F, 0.0F, 0.0F, 0.25F);
            tess.addVertex(-nameWidth - 1, -1.0, 0.0);
            tess.addVertex(-nameWidth - 1, 8.0, 0.0);
            tess.addVertex(nameWidth + 1, 8.0, 0.0);
            tess.addVertex(nameWidth + 1, -1.0, 0.0);
            tess.draw();
            GL11.glEnable(3553);
            font.drawString(label, -font.getStringWidth(label) / 2, 0, 553648127);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            font.drawString(label, -font.getStringWidth(label) / 2, 0, -1);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    protected void renderPath(T entity) {
        Path path = entity.navigator.getPath();
        if (path != null && path.nodes != null && path.nodes.length != 0 && !path.isFinished() && OptionValues.advCoords) {
            double camX = EntityRenderManager.renderPosX;
            double camY = EntityRenderManager.renderPosY;
            double camZ = EntityRenderManager.renderPosZ;
            double cubeSize = 0.4;
            double half = cubeSize / 2.0;
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(2884);
            GL11.glLineWidth(2.5F);
            Tessellator t = Tessellator.instance;
            GL11.glColor4f(0.2F, 0.8F, 1.0F, 1.0F);
            t.begin(3);

            for (PathNode point : path.nodes) {
                t.addVertex(point.xCoord + 0.5 - camX, point.yCoord + 0.5 - camY, point.zCoord + 0.5 - camZ);
            }

            t.draw();
            t.begin(1);
            PathNode start = path.nodes[0];
            GL11.glColor4f(1.0F, 0.2F, 0.2F, 1.0F);
            this.drawWireCube(t, start.xCoord + 0.5 - half - camX, start.yCoord + 0.5 - half - camY, start.zCoord + 0.5 - half - camZ, cubeSize);
            t.draw();
            t.begin(1);
            PathNode end = path.nodes[path.nodes.length - 1];
            GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
            this.drawWireCube(t, end.xCoord + 0.5 - half - camX, end.yCoord + 0.5 - half - camY, end.zCoord + 0.5 - half - camZ, cubeSize);
            t.draw();
            GL11.glEnable(2884);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
        }
    }

    private void drawWireCube(Tessellator t, double x, double y, double z, double s) {
        double x2 = x + s;
        double y2 = y + s;
        double z2 = z + s;
        this.line(t, x, y, z, x2, y, z);
        this.line(t, x2, y, z, x2, y, z2);
        this.line(t, x2, y, z2, x, y, z2);
        this.line(t, x, y, z2, x, y, z);
        this.line(t, x, y2, z, x2, y2, z);
        this.line(t, x2, y2, z, x2, y2, z2);
        this.line(t, x2, y2, z2, x, y2, z2);
        this.line(t, x, y2, z2, x, y2, z);
        this.line(t, x, y, z, x, y2, z);
        this.line(t, x2, y, z, x2, y2, z);
        this.line(t, x2, y, z2, x2, y2, z2);
        this.line(t, x, y, z2, x, y2, z2);
    }

    private void line(Tessellator t, double x1, double y1, double z1, double x2, double y2, double z2) {
        t.addVertex(x1, y1, z1);
        t.addVertex(x2, y2, z2);
    }
}
