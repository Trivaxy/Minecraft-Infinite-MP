package net.minecraft.client.particle;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Particle extends Entity {
    protected int textureIndex;
    protected int age = 0;
    protected int maxAge = 0;
    protected float scale;
    protected float gravity;
    protected float red;
    protected float green;
    protected float blue;
    protected float textureJitterX;
    protected float textureJitterY;
    public static double interpX;
    public static double interpY;
    public static double interpZ;
    public boolean glows = false;

    public Particle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world);
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.motionX = motionX + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.motionY = motionY + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.motionZ = motionZ + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        float offset = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
        float spdLimit = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / spdLimit * offset * 0.4F;
        this.motionY = this.motionY / spdLimit * offset * 0.4F + 0.1F;
        this.motionZ = this.motionZ / spdLimit * offset * 0.4F;
        this.textureJitterX = this.rand.nextFloat() * 3.0F;
        this.textureJitterY = this.rand.nextFloat() * 3.0F;
        this.red = this.green = this.blue = 1.0F;
        this.scale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.age = 0;
        this.produceFootsteps = false;
    }

    @Override
    protected void init() {
    }

    public Particle multiplyVelocity(float str) {
        this.motionX *= str;
        this.motionY = (this.motionY - 0.1F) * str + 0.1F;
        this.motionZ *= str;
        return this;
    }

    public Particle scaleParticle(float scale) {
        this.setSize(0.2F * scale, 0.2F * scale);
        this.scale *= scale;
        return this;
    }

    public void setTextureIndex(int index) {
        this.textureIndex = index;
    }

    public void forceMaxAge(int index) {
        this.maxAge = index;
    }

    public void setParticleColors(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }

        this.motionY = this.motionY - 0.04 * this.gravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }

    public void renderParticle(Tessellator tesselator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float vertU = this.textureIndex % 16 / 16.0F;
        float imgStart = vertU + 0.0624375F;
        float vertV = this.textureIndex / 16 / 16.0F;
        float imgEnd = vertV + 0.0624375F;
        float imgScale = 0.1F * this.scale;
        double x = this.prevX + (this.posX - this.prevX) * deltaTime - interpX;
        double y = this.prevY + (this.posY - this.prevY) * deltaTime - interpY;
        double z = this.prevZ + (this.posZ - this.prevZ) * deltaTime - interpZ;
        tesselator.setColor(this.red, this.green, this.blue);
        tesselator.addTexturedVertex(
            x - yaw * imgScale - pitchOffset * imgScale, y - pitch * imgScale, z - yawOffset * imgScale - offset * imgScale, vertU, imgEnd
        );
        tesselator.addTexturedVertex(
            x - yaw * imgScale + pitchOffset * imgScale, y + pitch * imgScale, z - yawOffset * imgScale + offset * imgScale, vertU, vertV
        );
        tesselator.addTexturedVertex(
            x + yaw * imgScale + pitchOffset * imgScale, y + pitch * imgScale, z + yawOffset * imgScale + offset * imgScale, imgStart, vertV
        );
        tesselator.addTexturedVertex(
            x + yaw * imgScale - pitchOffset * imgScale, y - pitch * imgScale, z + yawOffset * imgScale - offset * imgScale, imgStart, imgEnd
        );
    }

    public int layer() {
        return 0;
    }

    @Override
    public void writeToNBT(CompoundTag nbtTagCompound) {
    }

    @Override
    public void readFromNBT(CompoundTag nbtTagCompound) {
    }
}
