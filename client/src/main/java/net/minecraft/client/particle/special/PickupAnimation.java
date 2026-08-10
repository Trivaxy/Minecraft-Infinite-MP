package net.minecraft.client.particle.special;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PickupAnimation extends Particle {
    private Entity entityToPickUp;
    private Entity entityPickingUp;
    private float yOffs;

    public PickupAnimation(World world, Entity item, Entity target, float yOffset) {
        super(world, item.posX, item.posY, item.posZ, item.motionX, item.motionY, item.motionZ);
        this.entityToPickUp = item;
        this.entityPickingUp = target;
        this.maxAge = 3;
        this.yOffs = yOffset;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float existTime = (this.age + deltaTime) / this.maxAge;
        existTime *= existTime;
        double itemX = this.entityToPickUp.posX;
        double itemY = this.entityToPickUp.posY;
        double itemZ = this.entityToPickUp.posZ;
        double targetX = this.entityPickingUp.lastTickPosX + (this.entityPickingUp.posX - this.entityPickingUp.lastTickPosX) * deltaTime;
        double targetY = this.entityPickingUp.lastTickPosY + (this.entityPickingUp.posY - this.entityPickingUp.lastTickPosY) * deltaTime + this.yOffs;
        double targetZ = this.entityPickingUp.lastTickPosZ + (this.entityPickingUp.posZ - this.entityPickingUp.lastTickPosZ) * deltaTime;
        double newX = itemX + (targetX - itemX) * existTime;
        double newY = itemY + (targetY - itemY) * existTime;
        double newZ = itemZ + (targetZ - itemZ) * existTime;
        int brightness = this.getBrightnessInt(deltaTime);
        int vertU = brightness % 65536;
        int vertV = brightness / 65536;
        GL13.glMultiTexCoord2f(33985, vertU / 1.0F, vertV / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        newX -= interpX;
        newY -= interpY;
        newZ -= interpZ;
        EntityRenderManager.instance.renderEntityAt(this.entityToPickUp, newX, newY, newZ, this.entityToPickUp.yaw, deltaTime);
    }

    @Override
    public void onTick() {
        this.age++;
        if (this.age == this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public int layer() {
        return 3;
    }
}
