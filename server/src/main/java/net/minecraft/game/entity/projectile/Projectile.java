package net.minecraft.game.entity.projectile;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.staff.GabrielStaff;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public abstract class Projectile extends Entity {
    public Mob thrower;
    public Item itemAssigned = ItemList.snowball;
    private int ticksInAir = 0;
    public int damage = 0;
    protected double bbSize = 0.3;
    protected double gravityForce = 0.03;

    public Projectile(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public Projectile(World world, Mob thrower, int dmg) {
        super(world);
        this.thrower = thrower;
        this.setSize(0.25F, 0.25F);
        this.teleportTo(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ, thrower.yaw, thrower.pitch);
        this.posX = this.posX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        float multiplier = 0.4F;
        this.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * multiplier;
        this.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * multiplier;
        this.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * multiplier;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
        this.damage = dmg;
    }

    public Projectile(World world, Mob thrower, double x, double y, double z, int dmg) {
        super(world);
        this.thrower = thrower;
        this.setSize(1.0F, 1.0F);
        this.setPosAndRot(thrower.posX, thrower.posY, thrower.posZ, thrower.yaw, thrower.pitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0;
        x += this.rand.nextGaussian() * 0.4;
        y += this.rand.nextGaussian() * 0.4;
        z += this.rand.nextGaussian() * 0.4;
        double divider = MathHelper.sqrt_double(x * x + y * y + z * z);
        this.motionX = x / divider * 0.1;
        this.motionY = y / divider * 0.1;
        this.motionZ = z / divider * 0.1;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.5F, 0.5F);
        this.damage = dmg;
    }

    public Projectile(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.yOffset = 0.0F;
    }

    @Override
    protected void init() {
    }

    @Override
    public void setDead() {
        this.isDead = true;
        if (this.damage < 0) {
            GabrielStaff.canUseStaff = true;
        }
    }

    public void setHeading(double x, double y, double z, float yaw, float pitch) {
        float dist = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= dist;
        y /= dist;
        z /= dist;
        x += this.rand.nextGaussian() * 0.0075F * pitch;
        y += this.rand.nextGaussian() * 0.0075F * pitch;
        z += this.rand.nextGaussian() * 0.0075F * pitch;
        x *= yaw;
        y *= yaw;
        z *= yaw;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float angle = MathHelper.sqrt_double(x * x + z * z);
        this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0 / (float) Math.PI);
        this.prevPitch = this.pitch = (float)(Math.atan2(y, angle) * 180.0 / (float) Math.PI);
    }

    @Override
    public void tick() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.tick();
        this.ticksInAir++;
        if (this.ticksInAir > 200 && !(this instanceof Hook)) {
            this.setDead();
        }

        Vector currPos = new Vector(this.posX, this.posY, this.posZ);
        Vector targetPos = new Vector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        TargetingSystem hit = this.world.rayTraceBlocks(currPos, targetPos);
        currPos = new Vector(this.posX, this.posY, this.posZ);
        targetPos = new Vector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (hit != null) {
            targetPos = currPos.addVector(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
        }

        if (!this.world.isOnline) {
            Entity target = null;
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double dist = 0.0;

            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                if (entity.isSelectable() && (entity != this.thrower || this.ticksInAir >= 5)) {
                    BoundingBox boundingBox = entity.boundingBox.expand(this.bbSize, this.bbSize, this.bbSize);
                    TargetingSystem entityHit = boundingBox.calculateIntercept(currPos, targetPos);
                    if (entityHit != null) {
                        double targetDist = currPos.distanceTo(entityHit.hitVec);
                        if (targetDist < dist || dist == 0.0) {
                            target = entity;
                            dist = targetDist;
                        }
                    }
                }
            }

            if (target != null && this.itemAssigned != ItemList.glowStick) {
                hit = new TargetingSystem(target);
            }
        }

        if (hit != null) {
            this.onImpact(hit);
            if (hit.entityHit != null && hit.entityHit instanceof Mob && this.thrower instanceof Player) {
                this.thrower.attackTarget = (Mob)hit.entityHit;
            }
        }

        this.posX = this.posX + this.motionX;
        this.posY = this.posY + this.motionY;
        this.posZ = this.posZ + this.motionZ;
        float angle = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.yaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / (float) Math.PI);
        this.pitch = (float)(Math.atan2(this.motionY, angle) * 180.0 / (float) Math.PI);

        while (this.pitch - this.prevPitch < -180.0F) {
            this.prevPitch -= 360.0F;
        }

        while (this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
        }

        while (this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
        }

        while (this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
        }

        this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
        this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
        float motion = 0.99F;
        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
                float offset = 0.25F;
                this.world
                    .spawnParticle(
                        ParticleType.BUBBLE,
                        this.posX - this.motionX * offset,
                        this.posY - this.motionY * offset,
                        this.posZ - this.motionZ * offset,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    );
            }

            motion = 0.8F;
        }

        this.spawnParticles();
        this.motionX *= motion;
        this.motionY *= motion;
        this.motionZ *= motion;
        this.motionY = this.motionY - this.gravityForce;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    protected void spawnParticles() {
    }

    protected void onImpact(TargetingSystem mop) {
    }

    @Override
    public float getbbSize() {
        return 1.0F;
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        this.setAttacked();
        if (source.getEntity() != null) {
            Vector lookVec = source.getEntity().getLookVector(1.0F);
            if (lookVec != null) {
                this.motionX = lookVec.xCoord;
                this.motionY = lookVec.yCoord;
                this.motionZ = lookVec.zCoord;
            }

            if (source.getEntity() instanceof Mob) {
                this.thrower = (Mob)source.getEntity();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setInteger("type", this.damage);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.damage = tag.getInteger("type");
    }

    @Override
    public boolean inRange(Vector vec) {
        return true;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }
}
