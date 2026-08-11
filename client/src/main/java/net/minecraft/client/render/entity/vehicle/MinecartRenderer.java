package net.minecraft.client.render.entity.vehicle;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.misc.MinecartModel;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class MinecartRenderer extends EntityRenderer<Minecart> {
    protected MinecartModel model = new MinecartModel();

    public MinecartRenderer() {
        this.setShadowSize(0.5F);
    }

    public void render(Minecart entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pitch;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pitch;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pitch;
        double size = 0.3F;
        Vector vector = entity.getPos(posX, posY, posZ);
        float facing = entity.prevPitch + (entity.pitch - entity.prevPitch) * pitch;
        if (vector != null) {
            Vector startVector = entity.getPosOffset(posX, posY, posZ, size);
            Vector endVector = entity.getPosOffset(posX, posY, posZ, -size);
            if (startVector == null) {
                startVector = vector;
            }

            if (endVector == null) {
                endVector = vector;
            }

            x += vector.xCoord - posX;
            y += (startVector.yCoord + endVector.yCoord) / 2.0 - posY;
            z += vector.zCoord - posZ;
            Vector finalVector = endVector.addVector(-startVector.xCoord, -startVector.yCoord, -startVector.zCoord);
            if (finalVector.lengthVector() != 0.0) {
                finalVector = finalVector.normalize();
                yaw = (float)(Math.atan2(finalVector.zCoord, finalVector.xCoord) * 180.0 / Math.PI);
                facing = (float)(Math.atan(finalVector.yCoord) * 73.0);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-facing, 0.0F, 0.0F, 1.0F);
        float lastHit = entity.getHitTime() - pitch;
        float damage = entity.getDamageTaken() - pitch;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (lastHit > 0.0F) {
            GL11.glRotatef(MathHelper.sin(lastHit) * lastHit * damage / 10.0F * entity.getDirection(), 1.0F, 0.0F, 0.0F);
        }

        this.loadTexture("/terrain.png");
        float scale = 0.75F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(0.0F, 0.3F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        switch (entity.type) {
            case 1:
                BlocksRenderer.baseModel.renderInGUI(BlockList.chest, 0);
                break;
            case 2:
                BlocksRenderer.baseModel.renderInGUI(BlockList.stoneOvenIdle, 0);
                break;
            case 3:
                BlocksRenderer.baseModel.renderInGUI(BlockList.tnt, 0);
        }

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(0.0F, -0.3F, 0.0F);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        this.loadTexture("/entity/vehicle/cart.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}
