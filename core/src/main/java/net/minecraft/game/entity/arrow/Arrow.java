package net.minecraft.game.entity.arrow;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Arrow extends Entity {
    public Mob shooter;
    protected int xTile = -1;
    protected int yTile = -1;
    protected int zTile = -1;
    protected int inTile = 0;
    protected int ticksInGround;
    protected int ticksInAir = 0;
    public int shakeFactor = 0;
    public int arrowDamage = 4;
    private int knockback;
    public int pickupID = ItemList.arrowFlint.id;
    private boolean inGround = false;
    public boolean shotByPlayer = false;

    public Arrow(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public Arrow(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this.yOffset = 0.0F;
    }

    public Arrow(World world, Mob mob, float tickDelta) {
        super(world);
        this.shooter = mob;
        this.shotByPlayer = mob instanceof Player;
        this.setSize(0.5F, 0.5F);
        this.teleportTo(mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ, mob.yaw, mob.pitch);
        float multiplier = 0.16F;
        if (this.shotByPlayer && ((Player)mob).inventory.canUseItem()) {
            multiplier = -0.16F;
        }

        this.posX = this.posX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * multiplier;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * multiplier;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
        this.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
        this.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI);
        this.setHeading(this.motionX, this.motionY, this.motionZ, tickDelta * 1.5F, 1.0F);
    }

    @Override
    protected void init() {
    }

    public void setHeading(double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        float velocity = MathHelper.sqrt_double(xCoord * xCoord + yCoord * yCoord + zCoord * zCoord);
        xCoord /= velocity;
        yCoord /= velocity;
        zCoord /= velocity;
        xCoord += this.rand.nextGaussian() * 0.0075F * pitch;
        yCoord += this.rand.nextGaussian() * 0.0075F * pitch;
        zCoord += this.rand.nextGaussian() * 0.0075F * pitch;
        xCoord *= yaw;
        yCoord *= yaw;
        zCoord *= yaw;
        this.motionX = xCoord;
        this.motionY = yCoord;
        this.motionZ = zCoord;
        float angle = MathHelper.sqrt_double(xCoord * xCoord + zCoord * zCoord);
        this.prevYaw = this.yaw = (float)(Math.atan2(xCoord, zCoord) * 180.0 / (float) Math.PI);
        this.prevPitch = this.pitch = (float)(Math.atan2(yCoord, angle) * 180.0 / (float) Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float angle = MathHelper.sqrt_double(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0 / (float) Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, angle) * 180.0 / (float) Math.PI);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPosAndRot(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.shakeFactor > 0) {
            this.shakeFactor--;
        }

        if (this.inGround) {
            int block = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
            if (block == this.inTile) {
                this.ticksInGround++;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }

                return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        } else {
            this.ticksInAir++;
        }

        Vector currPos = Vector.createVector(this.posX, this.posY, this.posZ);
        Vector targetPos = Vector.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        TargetingSystem hit = this.world.rayTraceBlocks(currPos, targetPos);
        currPos = Vector.createVector(this.posX, this.posY, this.posZ);
        targetPos = Vector.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (hit != null) {
            targetPos = Vector.createVector(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
        }

        Entity entityHit = null;
        List<Entity> entities = this.world.getEntities(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double dist = 0.0;

        for (int index = 0; index < entities.size(); index++) {
            Entity entity = entities.get(index);
            if (entity.isSelectable() && (entity != this.shooter || this.ticksInAir >= 5)) {
                BoundingBox boundingBox = entity.boundingBox.expand(0.3F, 0.3F, 0.3F);
                hit = boundingBox.calculateIntercept(currPos, targetPos);
                if (hit != null) {
                    double currDist = currPos.distanceTo(hit.hitVec);
                    if (currDist < dist || dist == 0.0) {
                        entityHit = entity;
                        dist = currDist;
                    }
                }
            }
        }

        if (entityHit != null) {
            hit = new TargetingSystem(entityHit);
        }

        if (hit != null) {
            if (hit.entityHit != null) {
                if (hit.entityHit.damageEntity(DamageSource.arrowDamage(this, this.shooter), this.arrowDamage)) {
                    if (this.knockback > 0) {
                        float force = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (force > 0.0F) {
                            hit.entityHit.addVelocity(this.motionX * this.knockback * 0.6F / force, 0.1, this.motionZ * this.knockback * 0.6F / force);
                        }
                    }

                    this.world.playEntitySound(this, "random.drr", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    if (hit.entityHit instanceof Mob && this.shooter instanceof Player) {
                        this.shooter.attackTarget = (Mob)hit.entityHit;
                    }

                    this.onEntityHit(hit.entityHit);
                } else if (this.pickupID != ItemList.arrowPiercing.id) {
                    this.motionX *= -0.1F;
                    this.motionY *= -0.1F;
                    this.motionZ *= -0.1F;
                    this.yaw += 180.0F;
                    this.prevYaw += 180.0F;
                    this.ticksInAir = 0;
                }
            } else {
                this.xTile = hit.blockX;
                this.yTile = hit.blockY;
                this.zTile = hit.blockZ;
                this.inTile = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
                this.motionX = (float)(hit.hitVec.xCoord - this.posX);
                this.motionY = (float)(hit.hitVec.yCoord - this.posY);
                this.motionZ = (float)(hit.hitVec.zCoord - this.posZ);
                float velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX = this.posX - this.motionX / velocity * 0.05F;
                this.posY = this.posY - this.motionY / velocity * 0.05F;
                this.posZ = this.posZ - this.motionZ / velocity * 0.05F;
                this.world.playEntitySound(this, "random.drr", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;
                this.shakeFactor = 7;
                this.onBlockHit();
            }
        }

        this.produceParticles();
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
        float velocity = 0.99F;
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

            velocity = 0.8F;
        }

        this.motionX *= velocity;
        this.motionY *= velocity;
        this.motionZ *= velocity;
        this.motionY -= 0.03F;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    protected void onBlockHit() {
        if (this.world.getBlockId(this.xTile, this.yTile, this.zTile) == BlockList.pot.id) {
            this.world.playEffect(2001, this.xTile, this.yTile, this.zTile, BlockList.pot.id);
            this.world.setBlockWithNotify(this.xTile, this.yTile, this.zTile, 0);
        }
    }

    protected void onEntityHit(Entity entityHit) {
        if (this.shooter instanceof Player) {
            ((Player)this.shooter).triggerAchievement(AchievementList.bow);
        }

        if (entityHit instanceof Mob) {
            Mob target = (Mob)entityHit;
            target.setArrows(target.getArrows() + 1);
        }

        this.setDead();
    }

    protected void produceParticles() {
        this.world
            .spawnParticle(
                ParticleType.CRIT,
                this.posX + this.motionX,
                this.posY + this.motionY,
                this.posZ + this.motionZ,
                -this.motionX,
                -this.motionY + 0.2,
                -this.motionZ
            );
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setShort("xTile", (short)this.xTile);
        tag.setShort("yTile", (short)this.yTile);
        tag.setShort("zTile", (short)this.zTile);
        tag.setShort("inTile", (byte)this.inTile);
        tag.setByte("shake", (byte)this.shakeFactor);
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.setBoolean("player", this.shotByPlayer);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.xTile = tag.getShort("xTile");
        this.yTile = tag.getShort("yTile");
        this.zTile = tag.getShort("zTile");
        this.inTile = tag.getShort("inTile");
        this.shakeFactor = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        this.shotByPlayer = tag.getBoolean("player");
    }

    @Override
    public void onCollideWith(Entity entity) {
        if (!this.world.isOnline && this.inGround && this.shotByPlayer && this.shakeFactor <= 0 && entity instanceof Player) {
            Player newPlayer = (Player)entity;
            if (newPlayer.inventory.addItemToInventory(new ItemStack(this.pickupID, 1))) {
                this.world.playEntitySound(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                newPlayer.onItemPickup(this);
                this.setDead();
            }
        }
    }

    @Override
    public boolean inRange(Vector vector) {
        return true;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }

    public int getDamage() {
        return this.arrowDamage;
    }

    public void setDamage(int dmg) {
        this.arrowDamage = dmg;
    }

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }
}
