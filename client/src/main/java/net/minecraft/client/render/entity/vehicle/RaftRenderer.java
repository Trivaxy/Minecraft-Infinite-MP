package net.minecraft.client.render.entity.vehicle;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.misc.BoatModel;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class RaftRenderer extends EntityRenderer<Raft> {
    protected BoatModel modelBoat = new BoatModel();

    public RaftRenderer() {
        this.setShadowSize(0.5F);
    }

    public void render(Raft entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        float lastHit = entity.getHitTime() - pitch;
        float damage = entity.getDamageTaken() - pitch;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (lastHit > 0.0F) {
            GL11.glRotatef(MathHelper.sin(lastHit) * lastHit * damage / 10.0F * entity.getDirection(), 1.0F, 0.0F, 0.0F);
        }

        this.loadTexture("/terrain.png");
        float scale = 0.55F;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-1.2F, 0.15F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        BlocksRenderer.baseModel.renderInGUI(BlockList.chest, 0);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(0.0F, -0.3F, 0.0F);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPopMatrix();
        this.loadTexture("/entity/vehicle/raft.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScalef(1.3F, 1.0F, 1.3F);
        this.modelBoat.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}
