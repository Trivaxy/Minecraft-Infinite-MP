package net.minecraft.client.render.entity.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import org.lwjgl.opengl.GL11;

public class SpawnerRenderer extends TileEntityRenderer<SpawnerEntity> {
    private Map<String, Entity> entityMap = new HashMap<>();

    public void render(SpawnerEntity spawner, double x, double y, double z, float tick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
        Entity entity = null;
        if (spawner.mobID != null && spawner.mobID.length() != 0) {
            entity = this.entityMap.get(spawner.mobID);
            if (entity == null) {
                entity = EntityManager.createEntity(spawner.mobID, null);
                this.entityMap.put(spawner.mobID, entity);
            }
        }

        if (entity != null && spawner.spawns < spawner.limit) {
            entity.setWorld(spawner.world);
            float scale = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GL11.glRotatef((float)(spawner.prevYaw + (spawner.yaw - spawner.prevYaw) * tick) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(scale, scale, scale);
            EntityRenderManager.instance.renderEntityAt(entity, 0.0, 0.0, 0.0, 0.0F, tick);
        }

        GL11.glPopMatrix();
    }
}
