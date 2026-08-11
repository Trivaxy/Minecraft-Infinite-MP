package net.minecraft.game.entity.boat;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Boat extends Entity {
    protected Material mat = MaterialList.water;
    private int rotation;
    private double posXMP;
    private double posYMP;
    private double posZMP;
    private double rotYawMP;
    private double rotPitchMP;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    public Boat(World world) {
        super(world);
        this.setSize(1.5F, 0.6F);
        this.yOffset = this.height / 2.0F;
        this.preventSpawning = true;
    }

    public Boat(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + this.yOffset, z);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    protected void init() {
        this.dataSync.addObject(17, 0);
        this.dataSync.addObject(18, 1);
        this.dataSync.addObject(19, 0);
    }

    public int getHitTime() {
        return this.dataSync.getObjectInt(17);
    }

    public void setHitTime(int ticks) {
        this.dataSync.updateObject(17, ticks);
    }

    public int getDirection() {
        return this.dataSync.getObjectInt(18);
    }

    public void setDirection(int dir) {
        this.dataSync.updateObject(18, dir);
    }

    public int getDamageTaken() {
        return this.dataSync.getObjectInt(19);
    }

    public void setDamageTaken(int damage) {
        this.dataSync.updateObject(19, damage);
    }

    @Override
    public BoundingBox getCollisionBox(Entity entity) {
        return entity.boundingBox;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getMountYOffset() {
        return -0.3;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (!this.world.isOnline && !this.isDead) {
            this.setDirection(-this.getDirection());
            this.setHitTime(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10);
            this.setAttacked();
            if (this.getDamageTaken() > 40) {
                if (this.rider != null) {
                    this.rider.mountEntity(this);
                }

                this.onDead();
            }
        }

        return true;
    }

    protected void onDead() {
        this.dropItem(ItemList.boat.id, 1, 0.0F);
        this.setDead();
    }

    @Override
    public void hurtAnim() {
        this.setDirection(-this.getDirection());
        this.setHitTime(10);
        this.setDamageTaken(this.getDamageTaken() + this.getDamageTaken() * 10);
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public void teleportInServer(double x, double y, double z, float yaw, float pitch, int dir) {
        this.posXMP = x;
        this.posYMP = y;
        this.posZMP = z;
        this.rotYawMP = yaw;
        this.rotPitchMP = pitch;
        this.rotation = dir + 4;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.getHitTime() > 0) {
            this.setHitTime(this.getHitTime() - 1);
        }

        if (this.getDamageTaken() > 0) {
            this.setDamageTaken(this.getDamageTaken() - 1);
        }

        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        byte div = 5;
        double spdMultiplier = 0.0;

        for (int i = 0; i < div; i++) {
            double minY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 0) / div - 0.125;
            double maxY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 1) / div - 0.125;
            BoundingBox boundingBox = BoundingBox.getBoundingBox(
                this.boundingBox.minX, minY, this.boundingBox.minZ, this.boundingBox.maxX, maxY, this.boundingBox.maxZ
            );
            if (this.world.containsMaterial(boundingBox, this.mat)) {
                spdMultiplier += 1.0 / div;
            }
        }

        if (this.world.isOnline) {
            if (this.rotation > 0) {
                double x = this.posX + (this.posXMP - this.posX) / this.rotation;
                double y = this.posY + (this.posYMP - this.posY) / this.rotation;
                double z = this.posZ + (this.posZMP - this.posZ) / this.rotation;
                double yaw = MathHelper.computeAngle((float)this.rotYawMP - this.yaw);
                this.yaw = (float)(this.yaw + yaw / this.rotation);
                this.pitch = (float)(this.pitch + (this.rotPitchMP - this.pitch) / this.rotation);
                this.rotation--;
                this.setPosition(x, y, z);
                this.setRotation(this.yaw, this.pitch);
            } else {
                double x = this.posX + this.motionX;
                double y = this.posY + this.motionY;
                double z = this.posZ + this.motionZ;
                this.setPosition(x, y, z);
                if (this.onGround) {
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                }

                this.motionX *= 0.99F;
                this.motionY *= 0.95F;
                this.motionZ *= 0.99F;
            }
        } else {
            if (spdMultiplier < 1.0) {
                double x = spdMultiplier * 2.0 - 1.0;
                this.motionY += 0.04F * x;
            } else {
                if (this.motionY < 0.0) {
                    this.motionY /= 2.0;
                }

                this.motionY += 0.007F;
            }

            if (this.rider != null) {
                this.motionX = this.motionX + this.rider.motionX * 0.4;
                this.motionZ = this.motionZ + this.rider.motionZ * 0.4;
            }

            double maxSpd = 0.6;
            if (this.motionX < -maxSpd) {
                this.motionX = -maxSpd;
            }

            if (this.motionX > maxSpd) {
                this.motionX = maxSpd;
            }

            if (this.motionZ < -maxSpd) {
                this.motionZ = -maxSpd;
            }

            if (this.motionZ > maxSpd) {
                this.motionZ = maxSpd;
            }

            if (this.onGround) {
                this.motionX *= 0.5;
                this.motionY *= 0.5;
                this.motionZ *= 0.5;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
            this.pitch = 0.0F;
            float rot = this.yaw;
            double distX = this.prevX - this.posX;
            double distZ = this.prevZ - this.posZ;
            double distTotal = distX * distX + distZ * distZ;
            if (distTotal > 0.001) {
                rot = (float)(Math.atan2(distZ, distX) * 180.0 / Math.PI);
            }

            double angle = MathHelper.computeAngle(rot - this.yaw);
            if (angle > 20.0) {
                angle = 20.0;
            }

            if (angle < -20.0) {
                angle = -20.0;
            }

            this.yaw = (float)(this.yaw + angle);
            this.setRotation(this.yaw, this.pitch);
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
            if (entities != null && !entities.isEmpty()) {
                for (int index = 0; index < entities.size(); index++) {
                    Entity entity = entities.get(index);
                    if (entity.isPushable()) {
                        entity.applyCollision(this);
                    }
                }
            }

            for (int i = -2; i < 2; i++) {
                for (int j = -2; j < 2; j++) {
                    int blockX = MathHelper.floor_double(this.posX) + i;
                    int blockZ = MathHelper.floor_double(this.posZ) + j;
                    int blockY = MathHelper.floor_double(this.posY);
                    int blockID = this.world.getBlockId(blockX, blockY, blockZ);
                    int blockMeta = this.world.getBlockMetadata(blockX, blockY, blockZ);
                    if (blockID == BlockList.waterLily.id || blockID == BlockList.lilyPad.id) {
                        BlockList.blocks[blockID].dropBlock(this.world, blockX, blockY, blockZ, blockMeta);
                        this.world.setBlockWithNotify(blockX, blockY, blockZ, 0);
                    }
                }
            }

            if (this.rider != null && this.rider.isDead) {
                this.rider = null;
            }
        }

        this.spawnWake(this.posX - this.prevX, this.posY - this.prevY, this.posZ - this.prevZ);
    }

    private void spawnWake(double travelX, double travelY, double travelZ) {
        double dist = Math.sqrt(travelX * travelX + travelZ * travelZ);
        if (dist > 0.15) {
            double angleX = Math.cos(this.yaw * Math.PI / 180.0);
            double angleZ = Math.sin(this.yaw * Math.PI / 180.0);

            for (int i = 0; i < 1.0 + dist * 15.0; i++) {
                double offset1 = this.rand.nextFloat() * 2.0F - 1.0F;
                double offset2 = (this.rand.nextInt(2) * 2 - 1) * 0.7;
                if (this.rand.nextBoolean()) {
                    double particleX = this.posX - angleX * offset1 * 0.8 + angleZ * offset2;
                    double particleZ = this.posZ - angleZ * offset1 * 0.8 - angleX * offset2;
                    this.world
                        .spawnParticle(
                            this.mat == MaterialList.lava ? ParticleType.SMOKE : ParticleType.RAINDROP,
                            particleX,
                            this.posY - 0.125,
                            particleZ,
                            travelX,
                            travelY,
                            travelZ
                        );
                } else {
                    double particleX = this.posX + angleX + angleZ * offset1 * 0.7;
                    double particleZ = this.posZ + angleZ - angleX * offset1 * 0.7;
                    this.world
                        .spawnParticle(
                            this.mat == MaterialList.lava ? ParticleType.SMOKE : ParticleType.SPLASH,
                            particleX,
                            this.posY - 0.125,
                            particleZ,
                            travelX,
                            travelY,
                            travelZ
                        );
                }
            }
        }
    }

    @Override
    public void updateRiderPosition() {
        if (this.rider != null) {
            double x = Math.cos(this.yaw * Math.PI / 180.0) * 0.4;
            double z = Math.sin(this.yaw * Math.PI / 180.0) * 0.4;
            this.rider.setPosition(this.posX + x, this.posY + this.getMountYOffset() + this.rider.getYOffset(), this.posZ + z);
        }
    }

    @Override
    public boolean interact(Player player) {
        if ((this.rider == null || !(this.rider instanceof Player) || this.rider == player) && !this.world.isOnline) {
            player.mountEntity(this);
        }

        return true;
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
    }
}
