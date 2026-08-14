package net.minecraft.game.entity;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.primitives.DoubleTag;
import com.mojang.nbt.tag.primitives.FloatTag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.List;
import java.util.Random;
import net.minecraft.Profiler;
import net.minecraft.game.ParticleContext;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Wall;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.SlimeBlock;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public abstract class Entity {
    private static int nextEntityID = 0;
    public static float renderDistance = 0.0F;
    public BoundingBox boundingBox = BoundingBox.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    protected DataSyncer dataSync = new DataSyncer();
    public Random rand = new Random();
    public Entity rider;
    public Entity mount;
    public World world;
    public String skinUrl;
    public String cloakUrl;
    public int entityId = nextEntityID++;
    public int ticksExisted = 0;
    public int fire = 0;
    public int freeze = 0;
    public int air = 300;
    public int maxAir = 300;
    public int damage = 0;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    public int serverX;
    public int serverY;
    public int serverZ;
    public int prevLitY;
    public int prevLitX;
    public int prevLitZ;
    private int nextStep = 1;
    public double prevX;
    public double prevY;
    public double prevZ;
    public double posX;
    public double posY;
    public double posZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public double lastTickPosX;
    public double lastTickPosY;
    public double lastTickPosZ;
    private double riderPitch;
    private double riderYaw;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    public float yOffset = 0.0F;
    public float width = 0.6F;
    public float height = 1.8F;
    public float distance = 0.0F;
    public float prevDistance = 0.0F;
    public float fallDistance = 0.0F;
    public float colHeight = 0.0F;
    public float stepHeight = 0.0F;
    public float knockback = 0.4F;
    public boolean onGround = false;
    public boolean noClip = false;
    public boolean inWater = false;
    public boolean collidedHorizontally = false;
    public boolean collidedVertically = false;
    public boolean beenAttacked = false;
    public boolean isDead = false;
    public boolean preventSpawning = false;
    public boolean produceFootsteps = true;
    public boolean jumping = false;
    public boolean sneaking;
    public boolean crawling;
    public boolean sitting;
    public boolean flying;
    public boolean gliding;
    public boolean sleeping;
    public boolean visible;
    public boolean boss = false;
    public boolean addedToChunk = false;
    public boolean canGlow = false;
    public boolean wasLit;

    public Entity(World world) {
        this.world = world;
        this.setPosition(0.0, 0.0, 0.0);
        this.dataSync.addObject(0, (byte)0);
        this.init();
    }

    protected abstract void init();

    public DataSyncer getDataSync() {
        return this.dataSync;
    }

    public void setDead() {
        this.isDead = true;
    }

    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void updateRotation(float yaw, float pitch) {
        float prevPitch = this.pitch;
        float prevYaw = this.yaw;
        this.yaw = (float)(this.yaw + yaw * 0.15);
        this.pitch = (float)(this.pitch - pitch * 0.15);
        this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
        this.prevPitch = this.prevPitch + (this.pitch - prevPitch);
        this.prevYaw = this.prevYaw + (this.yaw - prevYaw);
    }

    public void setPosition(double xCoord, double yCoord, double zCoord) {
        this.posX = xCoord;
        this.posY = yCoord;
        this.posZ = zCoord;
        float widthOffset = this.width / 2.0F;
        float heightOffset = this.height;
        this.boundingBox
            .setBounds(
                xCoord - widthOffset,
                yCoord - this.yOffset + this.colHeight,
                zCoord - widthOffset,
                xCoord + widthOffset,
                yCoord - this.yOffset + this.colHeight + heightOffset,
                zCoord + widthOffset
            );
    }

    public void setPosAndRot(double x, double y, double z, float yaw, float pitch) {
        this.lastTickPosX = this.prevX = this.posX = x;
        this.lastTickPosY = this.prevY = this.posY = y + this.yOffset;
        this.lastTickPosZ = this.prevZ = this.posZ = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setTypeOnSpawn();
    }

    public void teleportTo(double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        this.prevX = this.posX = xCoord;
        this.prevY = this.posY = yCoord + this.yOffset;
        this.prevZ = this.posZ = zCoord;
        this.yaw = yaw;
        this.pitch = pitch;
        this.colHeight = 0.0F;
        double facingAngle = this.prevYaw - yaw;
        if (facingAngle < -180.0) {
            this.prevYaw += 360.0F;
        }

        if (facingAngle >= 180.0) {
            this.prevYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
        this.setTypeOnSpawn();
    }

    public void setTypeOnSpawn() {
    }

    public void onTick() {
        this.tick();
        Profiler.startSection("dynamicLights");
        boolean lit = this.isLit() > 0;
        if (lit) {
            int newLitX = MathHelper.floor_double(this.posX);
            int newLitY = MathHelper.floor_double(this.posY);
            int newLitZ = MathHelper.floor_double(this.posZ);
            if (newLitX != this.prevLitX || newLitY != this.prevLitY || newLitZ != this.prevLitZ) {
                if (this.wasLit) {
                    this.world.restoreBlockEmittance(this.prevLitX, this.prevLitY, this.prevLitZ);
                }

                this.world.setEmittanceToFull(newLitX, newLitY, newLitZ, this.isLit());
                this.prevLitX = newLitX;
                this.prevLitY = newLitY;
                this.prevLitZ = newLitZ;
            }
        } else if (this.wasLit) {
            this.world.restoreBlockEmittance(this.prevLitX, this.prevLitY, this.prevLitZ);
        }

        this.wasLit = lit;
        Profiler.endSection();
    }

    public void tick() {
        Profiler.startSection("entityBaseTick");
        if (this.mount != null && this.mount.isDead) {
            this.mount = null;
        }

        this.ticksExisted++;
        this.prevDistance = this.distance;
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        if (this.world.settings.border > 0) {
            if (this.posX > this.world.settings.border - 1) {
                this.motionX--;
            }

            if (this.posZ > this.world.settings.border - 1) {
                this.motionZ--;
            }

            if (this.posX <= -this.world.settings.border + 1) {
                this.motionX++;
            }

            if (this.posZ <= -this.world.settings.border + 1) {
                this.motionZ++;
            }
        }

        if (this.isSprinting()) {
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY - 0.2 - this.yOffset);
            int z = MathHelper.floor_double(this.posZ);
            int blockID = this.world.getBlockId(x, y, z);
            if (blockID > 0 && !this.inWater && !this.crawling) {
                this.world
                    .spawnParticle(
                        ParticleContext.setIconContext(ParticleType.TILECRACK, blockID, this.world.getBlockMetadata(x, y, z)),
                        this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                        this.boundingBox.minY + 0.1,
                        this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                        -this.motionX * 4.0,
                        1.5,
                        -this.motionZ * 4.0
                    );
            }
        }

        if (this.isInWater()) {
            if (!this.inWater) {
                float force = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F)
                    * 0.2F;
                float feetLoc = MathHelper.floor_double(this.boundingBox.minY);
                float amount = 1.0F + this.width * 20.0F;
                if (this.fallDistance > 5.0F) {
                    if (force > 1.0F) {
                        force = 1.0F;
                    }

                    this.world.playEntitySound(this, "random.splash", force, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);

                    for (int particleCount = 0; particleCount < amount; particleCount++) {
                        float offsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        float offsetY = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        this.world
                            .spawnParticle(
                                ParticleType.BUBBLE,
                                this.posX + offsetX,
                                feetLoc + 1.0F,
                                this.posZ + offsetY,
                                this.motionX,
                                this.motionY - this.rand.nextFloat() * 0.2F,
                                this.motionZ
                            );
                    }

                    for (int var21 = 0; var21 < amount; var21++) {
                        float offsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        float offsetY = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        this.world
                            .spawnParticle(
                                ParticleType.SPLASH, this.posX + offsetX, feetLoc + 1.0F, this.posZ + offsetY, this.motionX, this.motionY, this.motionZ
                            );
                    }
                } else {
                    force *= 5.0F;
                    this.world.playEntitySound(this, "misc.swim", force, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);

                    for (int particleCount = 0; particleCount < amount; particleCount++) {
                        float offsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        float offsetY = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        this.world
                            .spawnParticle(
                                ParticleType.BUBBLE,
                                this.posX + offsetX,
                                feetLoc + 1.0F,
                                this.posZ + offsetY,
                                this.motionX,
                                this.motionY - this.rand.nextFloat() * 0.2F,
                                this.motionZ
                            );
                    }

                    for (int var19 = 0; var19 < amount; var19++) {
                        float offsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        float offsetY = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                        this.world
                            .spawnParticle(
                                ParticleType.RAINDROP, this.posX + offsetX, feetLoc + 1.0F, this.posZ + offsetY, this.motionX, this.motionY, this.motionZ
                            );
                    }
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        } else {
            this.inWater = false;
        }

        if (this.world.isRaining()) {
            this.fire = 0;
        }

        int minX = MathHelper.floor_double(this.boundingBox.minX - 0.1F);
        int minY = MathHelper.floor_double(this.boundingBox.minY - 0.1F);
        int minZ = MathHelper.floor_double(this.boundingBox.minZ - 0.1F);
        int maxX = MathHelper.floor_double(this.boundingBox.maxX);
        int maxY = MathHelper.floor_double(this.boundingBox.maxY);
        int maxZ = MathHelper.floor_double(this.boundingBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int block = this.world.getBlockId(x, y, z);
                    if (block > 0) {
                        BlockList.blocks[block].onEntityCollision(this, this.world, x, y, z);
                    }
                }
            }
        }

        if (this.world.isOnline) {
            this.fire = 0;
        } else if (this.fire > 0 && this.canBurn()) {
            if (this.fire % 20 == 0) {
                this.damageEntity(DamageSource.onFire, 1);
            }

            this.fire--;
            this.freeze = 0;
        }

        if (this.freeze > 0) {
            this.freeze--;
        }

        if (this.isInLava() && !this.isRiding() && this.canBurn()) {
            this.damageEntity(DamageSource.lava, 4);
            this.fire = 300;
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0) {
            this.kill();
        }

        if (!this.world.isOnline) {
            this.setFlag(0, this.fire > 0);
            this.setFlag(2, this.mount != null);
        }

        Profiler.endSection();
    }

    public void despawnTick() {
    }

    protected void kill() {
        this.setDead();
    }

    public boolean offsetPosInLiquid(double xCoord, double yCoord, double zCoord) {
        BoundingBox boundingBox = this.boundingBox.getOffsetBoundingBox(xCoord, yCoord, zCoord);
        List<BoundingBox> bbList = this.world.getBoundingBoxes(this, boundingBox);
        return bbList.size() > 0 ? false : !this.world.containsLiquid(boundingBox);
    }

    public void moveEntity(double xCoord, double yCoord, double zCoord) {
        if (this.noClip) {
            this.boundingBox.offset(xCoord, yCoord, zCoord);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.colHeight;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
        } else {
            Profiler.startSection("move");
            double x = this.posX;
            double z = this.posZ;
            double posX = xCoord;
            double posY = yCoord;
            double posZ = zCoord;
            BoundingBox bbCopy = this.boundingBox.copy();
            boolean sneaking = this.onGround && this.sneaking && !this.flying;
            if (sneaking) {
                double offset = 0.05;

                while (xCoord != 0.0 && this.world.getBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(xCoord, -1.0, 0.0)).size() == 0) {
                    if (xCoord < offset && xCoord >= -offset) {
                        xCoord = 0.0;
                    } else if (xCoord > 0.0) {
                        xCoord -= offset;
                    } else {
                        xCoord += offset;
                    }

                    posX = xCoord;
                }

                for (; zCoord != 0.0 && this.world.getBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, -1.0, zCoord)).size() == 0; posZ = zCoord) {
                    if (zCoord < offset && zCoord >= -offset) {
                        zCoord = 0.0;
                    } else if (zCoord > 0.0) {
                        zCoord -= offset;
                    } else {
                        zCoord += offset;
                    }
                }
            }

            List<BoundingBox> surroundingBBs = this.world.getBoundingBoxes(this, this.boundingBox.addCoord(xCoord, yCoord, zCoord));

            for (int bb = 0; bb < surroundingBBs.size(); bb++) {
                yCoord = surroundingBBs.get(bb).calculateYOffset(this.boundingBox, yCoord);
            }

            this.boundingBox.offset(0.0, yCoord, 0.0);
            boolean movementCheck = this.onGround || posY != yCoord && posY < 0.0;

            for (int var34 = 0; var34 < surroundingBBs.size(); var34++) {
                xCoord = surroundingBBs.get(var34).calculateXOffset(this.boundingBox, xCoord);
            }

            this.boundingBox.offset(xCoord, 0.0, 0.0);

            for (int var35 = 0; var35 < surroundingBBs.size(); var35++) {
                zCoord = surroundingBBs.get(var35).calculateZOffset(this.boundingBox, zCoord);
            }

            this.boundingBox.offset(0.0, 0.0, zCoord);
            if (this.getStepHeight() > 0.0F && movementCheck && (sneaking || this.colHeight < 0.05F) && (posX != xCoord || posZ != zCoord)) {
                double oldX = xCoord;
                double oldY = yCoord;
                double oldZ = zCoord;
                xCoord = posX;
                yCoord = this.getStepHeight();
                zCoord = posZ;
                BoundingBox axisAlignedBB26 = this.boundingBox.copy();
                this.boundingBox.setBB(bbCopy);
                surroundingBBs = this.world.getBoundingBoxes(this, this.boundingBox.addCoord(posX, yCoord, posZ));

                for (int var36 = 0; var36 < surroundingBBs.size(); var36++) {
                    yCoord = surroundingBBs.get(var36).calculateYOffset(this.boundingBox, yCoord);
                }

                this.boundingBox.offset(0.0, yCoord, 0.0);

                for (int var37 = 0; var37 < surroundingBBs.size(); var37++) {
                    xCoord = surroundingBBs.get(var37).calculateXOffset(this.boundingBox, xCoord);
                }

                this.boundingBox.offset(xCoord, 0.0, 0.0);

                for (int var38 = 0; var38 < surroundingBBs.size(); var38++) {
                    zCoord = surroundingBBs.get(var38).calculateZOffset(this.boundingBox, zCoord);
                }

                this.boundingBox.offset(0.0, 0.0, zCoord);
                if (oldX * oldX + oldZ * oldZ >= xCoord * xCoord + zCoord * zCoord) {
                    xCoord = oldX;
                    yCoord = oldY;
                    zCoord = oldZ;
                    this.boundingBox.setBB(axisAlignedBB26);
                } else {
                    this.colHeight = (float)(this.colHeight + 0.5);
                }
            }

            Profiler.endSection();
            Profiler.startSection("rest");
            if (!this.world.isOnline || this instanceof Player || !(this instanceof Mob)) {
                this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
                this.posY = this.boundingBox.minY + this.yOffset - this.colHeight;
                this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
            }

            this.collidedHorizontally = posX != xCoord || posZ != zCoord;
            this.onGround = posY != yCoord && posY < 0.0;
            this.collidedVertically = this.collidedHorizontally || posY != yCoord;
            this.updateFallState(yCoord, this.onGround);
            if (!(this instanceof Minecart)) {
                if (posX != xCoord) {
                    this.motionX = 0.0;
                }

                if (posZ != zCoord) {
                    this.motionZ = 0.0;
                }

                if (posY != yCoord) {
                    Block b = this.world
                        .getBlock(
                            MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2 - this.yOffset), MathHelper.floor_double(this.posZ)
                        );
                    if (b instanceof SlimeBlock) {
                        this.motionY = -this.motionY;
                    } else {
                        this.motionY = 0.0;
                    }
                }
            }

            double oldX = this.posX - x;
            double oldY = this.posZ - z;
            this.updateFootstep(oldX, oldY);

            this.colHeight *= 0.4F;
            if (this.world.containsFire(this.boundingBox) && !this.isRiding()) {
                this.dealFireDamage(1);
                if (!this.isInWater()) {
                    this.fire++;
                    if (this.fire == 0) {
                        this.fire = 300;
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -1;
            }

            if (this.isInWater() && this.fire > 0) {
                this.world.playEntitySound(this, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -1;
            }

            Profiler.endSection();
        }
    }

    public boolean isGliding() {
        return false;
    }

    public void walkOnBlock() {
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean flag) {
        this.sneaking = flag;
    }

    public void updateFootstep(double dx, double dz) {
        if (this.produceFootsteps && !this.sneaking && !this.crawling && !this.flying && !this.gliding && this.onGround && !this.isGliding() && !this.isRiding() && !this.isInWater()) {
            double horizDist = MathHelper.sqrt_double(dx * dx + dz * dz);
            if (horizDist > 0.0 && horizDist < 1.0) {
                this.distance = (float)(this.distance + horizDist * 0.6);
                int blockX = MathHelper.floor_double(this.posX);
                int blockY = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
                int blockZ = MathHelper.floor_double(this.posZ);
                int blockID = this.world.getBlockId(blockX, blockY, blockZ);
                if (this.world.getBlockId(blockX, blockY - 1, blockZ) == BlockList.fence.id || this.world.getBlock(blockX, blockY - 1, blockZ) instanceof Wall) {
                    blockID = this.world.getBlockId(blockX, blockY - 1, blockZ);
                }

                if (this.distance > this.nextStep && blockID > 0) {
                    this.nextStep = (int)this.distance + 1;
                    this.playStepSound();
                    BlockList.blocks[blockID].onEntityWalking(this.world, blockX, blockY, blockZ, this);
                }

                this.walkOnBlock();
            }
        }
    }

    public void playStepSound() {
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
        int blockZ = MathHelper.floor_double(this.posZ);
        int blockID = this.world.getBlockId(blockX, blockY, blockZ);
        int blockMeta = this.world.getBlockMetadata(blockX, blockY, blockZ);
        if (blockID > 0) {
            StepSound sound = BlockList.blocks[blockID].getStepSound(blockMeta);
            if (BlockList.blocks[this.world.getBlockId(blockX, blockY + 1, blockZ)] != null
                && BlockList.blocks[this.world.getBlockId(blockX, blockY + 1, blockZ)].maxY <= 0.3F) {
                sound = BlockList.blocks[this.world.getBlockId(blockX, blockY + 1, blockZ)].getStepSound(blockMeta);
            }

            if (!BlockList.blocks[blockID].material.getIsLiquid()) {
                this.world.playEntitySound(this, sound.getSound(), sound.getVolume() * 0.15F, sound.getPitch());
            }
        }
    }

    public CreatureType getCreatureType() {
        return CreatureType.UNDEFINED;
    }

    protected void updateFallState(double yCoord, boolean ground) {
        if (ground) {
            if (this.fallDistance > 0.0F) {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        } else if (yCoord < 0.0) {
            this.fallDistance = (float)(this.fallDistance - yCoord);
        }
    }

    public BoundingBox getBoundingBox() {
        return null;
    }

    public BoundingBox getCollisionBox(Entity entity) {
        return null;
    }

    public void dealFireDamage(int damage) {
        this.damageEntity(DamageSource.inFire, damage);
    }

    protected void fall(float dist) {
    }

    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox.expand(0.0, -0.4, 0.0), MaterialList.water, this);
    }

    public boolean isInLava() {
        return this.world.handleWaterMovement(this.boundingBox.expand(0.0, -0.4, 0.0), MaterialList.lava, this);
    }

    public boolean isInMaterial(Material material) {
        double yOff = this.posY + this.getEyeHeight();
        if (!material.getIsLiquid()) {
            yOff = this.posY;
        }

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_float(MathHelper.floor_double(yOff));
        int z = MathHelper.floor_double(this.posZ);
        int id = this.world.getBlockId(x, y, z);
        if (id != 0 && BlockList.blocks[id].material == material) {
            float fluidOff = LiquidBlock.getHeight(this.world.getBlockMetadata(x, y, z)) - 0.11111111F;
            float finalOff = y + 1 - fluidOff;
            return yOff < finalOff;
        } else {
            return false;
        }
    }

    public float getEyeHeight() {
        return 0.0F;
    }

    public void moveRelative(float xCoord, float yCoord, float zCoord) {
        float dist = MathHelper.sqrt_float(xCoord * xCoord + yCoord * yCoord);
        if (dist >= 0.01F) {
            if (dist < 1.0F) {
                dist = 1.0F;
            }

            dist = zCoord / dist;
            xCoord *= dist;
            yCoord *= dist;
            float xConst = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
            float zConst = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
            this.motionX += xCoord * zConst - yCoord * xConst;
            this.motionZ += yCoord * zConst + xCoord * xConst;
        }
    }

    public void moveFlying(float x, float y, float z) {
        if (this.flying) {
            float dist = MathHelper.sqrt_float(x * x + y * y);
            if (dist < 0.1F) {
                return;
            }

            dist = 0.125F / dist;
            if (this.isSprinting()) {
                dist += 0.04F;
            }

            x *= dist;
            y *= dist;
            float xConst = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
            float zConst = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
            this.motionX += x * zConst - y * xConst;
            this.motionZ += y * zConst + x * xConst;
        }
    }

    public float getBrightness(float lightVal) {
        double yOff = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY - this.yOffset + yOff);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBrightness(x, y, z);
    }

    public int getBrightnessInt(float f1) {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        if (this.world.blockExists(x, 0, z)) {
            double off = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
            int y = MathHelper.floor_double(this.posY - this.yOffset + off);
            return this.world.getBrightness(x, y, z, 0);
        } else {
            return 0;
        }
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public float getDistanceTo(Entity entity) {
        float x = (float)(this.posX - entity.posX);
        float y = (float)(this.posY - entity.posY);
        float z = (float)(this.posZ - entity.posZ);
        return MathHelper.sqrt_float(x * x + y * y + z * z);
    }

    public double getDistanceSq(double xCoord, double yCoord, double zCoord) {
        double x = this.posX - xCoord;
        double y = this.posY - yCoord;
        double z = this.posZ - zCoord;
        return x * x + y * y + z * z;
    }

    public double getDistance(double xCoord, double yCoord, double zCoord) {
        double x = this.posX - xCoord;
        double y = this.posY - yCoord;
        double z = this.posZ - zCoord;
        return MathHelper.sqrt_double(x * x + y * y + z * z);
    }

    public double getDistanceSqTo(Entity entity) {
        double x = this.posX - entity.posX;
        double y = this.posY - entity.posY;
        double z = this.posZ - entity.posZ;
        return x * x + y * y + z * z;
    }

    public void onCollideWith(Entity entity) {
    }

    public void applyCollision(Entity entity) {
        if (entity.rider != this && entity.mount != this && !entity.sitting) {
            double x = entity.posX - this.posX;
            double z = entity.posZ - this.posZ;
            double dist = MathHelper.abs_max(x, z);
            if (dist >= 0.01F) {
                dist = MathHelper.sqrt_double(dist);
                x /= dist;
                z /= dist;
                double off = 1.0 / dist;
                if (off > 1.0) {
                    off = 1.0;
                }

                x *= off;
                z *= off;
                x *= 0.05F;
                z *= 0.05F;
                x *= 1.0;
                z *= 1.0;
                this.addVelocity(-x, 0.0, -z);
                entity.addVelocity(x, 0.0, z);
            }
        }
    }

    public void addVelocity(double xCoord, double yCoord, double zCoord) {
        this.motionX += xCoord;
        this.motionY += yCoord;
        this.motionZ += zCoord;
    }

    protected void setAttacked() {
        this.beenAttacked = true;
    }

    public boolean damageEntity(DamageSource source, int amount) {
        this.setAttacked();
        return false;
    }

    public Vector getLookVector(float angle) {
        return null;
    }

    public boolean isSelectable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean canBurn() {
        return false;
    }

    public int getSpawnID() {
        return -1;
    }

    public void onStruckByLightning(LightningBolt lightning) {
        this.dealFireDamage(5);
        this.fire++;
        if (this.fire == 0) {
            this.fire = 300;
        }
    }

    public float getStepHeight() {
        return this.stepHeight;
    }

    public int isLit() {
        return 0;
    }

    public float getbbSize() {
        return 0.1F;
    }

    public boolean getLuckModifier() {
        return false;
    }

    public float getFacingAngle() {
        return 0.0F;
    }

    public boolean alwaysTick() {
        return true;
    }

    public boolean alwaysVisible() {
        return false;
    }

    public boolean canBeSlowed() {
        return true;
    }

    public void addScore(Entity entity, int score) {
    }

    public boolean inRange(Vector vector) {
        double x = this.posX - vector.xCoord;
        double y = this.posY - vector.yCoord;
        double z = this.posZ - vector.zCoord;
        double dist = x * x + y * y + z * z;
        return this.inRange(dist);
    }

    public boolean inRange(double distance) {
        double lgth = this.boundingBox.getLength();
        lgth *= 64.0 * (renderDistance + 1.0F);
        return distance < lgth * lgth;
    }

    public String getTexture() {
        return null;
    }

    public boolean addEntityID(CompoundTag nbt) {
        String entityStr = this.getEntityString();
        if (!this.isDead && entityStr != null && this.rider == null) {
            nbt.setString("id", entityStr);
            this.save(nbt);
            return true;
        } else {
            return false;
        }
    }

    public void save(CompoundTag nbt) {
        nbt.setTag("Pos", this.getDoubleList(this.posX, this.posY + this.colHeight, this.posZ));
        nbt.setTag("Motion", this.getDoubleList(this.motionX, this.motionY, this.motionZ));
        nbt.setTag("Rotation", this.getFloatList(this.yaw, this.pitch));
        nbt.setFloat("FallDistance", this.fallDistance);
        nbt.setShort("Fire", (short)this.fire);
        nbt.setShort("Air", (short)this.air);
        nbt.setShort("Freeze", (short)this.freeze);
        nbt.setBoolean("OnGround", this.onGround);
        nbt.setBoolean("Crawling", this.crawling);
        nbt.setBoolean("Flying", this.flying);
        this.writeToNBT(nbt);
        if (this.mount != null) {
            CompoundTag mountTag = new CompoundTag();
            if (this.mount.writeMountToNBT(mountTag)) {
                nbt.setTag("Riding", mountTag);
            }
        }
    }

    public void load(CompoundTag nbt) {
        ListTag<? extends Tag> posList = nbt.getTagList("Pos");
        ListTag<? extends Tag> motionList = nbt.getTagList("Motion");
        ListTag<? extends Tag> rotList = nbt.getTagList("Rotation");
        this.motionX = ((DoubleTag)motionList.get(0)).doubleValue;
        this.motionY = ((DoubleTag)motionList.get(1)).doubleValue;
        this.motionZ = ((DoubleTag)motionList.get(2)).doubleValue;
        this.prevX = this.lastTickPosX = this.posX = ((DoubleTag)posList.get(0)).doubleValue;
        this.prevY = this.lastTickPosY = this.posY = ((DoubleTag)posList.get(1)).doubleValue;
        this.prevZ = this.lastTickPosZ = this.posZ = ((DoubleTag)posList.get(2)).doubleValue;
        this.prevYaw = this.yaw = ((FloatTag)rotList.get(0)).floatValue;
        this.prevPitch = this.pitch = ((FloatTag)rotList.get(1)).floatValue;
        this.fallDistance = nbt.getFloat("FallDistance");
        this.fire = nbt.getShort("Fire");
        this.air = nbt.getShort("Air");
        this.freeze = nbt.getShort("Freeze");
        this.onGround = nbt.getBoolean("OnGround");
        this.crawling = nbt.getBoolean("Crawling");
        this.flying = nbt.getBoolean("Flying");
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(this.yaw, this.pitch);
        this.readFromNBT(nbt);
    }

    public boolean writeMountToNBT(CompoundTag tag) {
        String entity = this.getEntityString();
        if (!this.isDead && entity != null) {
            tag.setString("id", entity);
            this.save(tag);
            return true;
        } else {
            return false;
        }
    }

    public final String getEntityString() {
        return EntityManager.getEntityString(this);
    }

    protected abstract void readFromNBT(CompoundTag var1);

    protected abstract void writeToNBT(CompoundTag var1);

    protected ListTag<DoubleTag> getDoubleList(double... d) {
        ListTag<DoubleTag> list = new ListTag<>();
        double[] dArray = d;

        for (int index = 0; index < d.length; index++) {
            list.add(new DoubleTag(dArray[index]));
        }

        return list;
    }

    protected ListTag<FloatTag> getFloatList(float... f) {
        ListTag<FloatTag> list = new ListTag<>();
        float[] fArray = f;

        for (int index = 0; index < f.length; index++) {
            list.add(new FloatTag(fArray[index]));
        }

        return list;
    }

    public DroppedItem dropItem(int id, int size) {
        return this.dropItem(id, size, 0.0F);
    }

    public final DroppedItem dropItem(int id, int size, int meta, float offset) {
        DroppedItem item = new DroppedItem(this.world, this.posX, this.posY + offset, this.posZ, new ItemStack(id, size, meta));
        item.pickupDelay = 10;
        this.world.spawnEntity(item);
        return item;
    }

    public DroppedItem dropItem(int id, int size, float offset) {
        DroppedItem item = new DroppedItem(this.world, this.posX, this.posY + offset, this.posZ, new ItemStack(id, size));
        this.world.spawnEntity(item);
        return item;
    }

    public boolean isAlive() {
        return !this.isDead;
    }

    public double getYOffset() {
        return this.height * 0.7;
    }

    public boolean interact(Player player) {
        return false;
    }

    public void updateRiderPosition() {
        this.rider.setPosition(this.posX, this.posY + this.getYOffset() + this.rider.getYOffset(), this.posZ);
    }

    public double getMountYOffset() {
        return this.height * 0.75;
    }

    public int maxPathFallDist() {
        return 3;
    }

    public void updateRidden() {
        if (this.mount.isDead) {
            this.mount = null;
        } else {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.onTick();
            if (this.mount != null) {
                this.mount.updateRiderPosition();
                this.riderYaw = this.riderYaw + (this.mount.yaw - this.mount.prevYaw);
                this.riderPitch = this.riderPitch + (this.mount.pitch - this.mount.prevPitch);

                while (this.riderYaw >= 180.0) {
                    this.riderYaw -= 360.0;
                }

                while (this.riderYaw < -180.0) {
                    this.riderYaw += 360.0;
                }

                while (this.riderPitch >= 180.0) {
                    this.riderPitch -= 360.0;
                }

                while (this.riderPitch < -180.0) {
                    this.riderPitch += 360.0;
                }

                double newYaw = this.riderYaw * 0.5;
                double newPitch = this.riderPitch * 0.5;
                float size = 10.0F;
                newYaw = MathHelper.clamp(newYaw, -size, size);
                newPitch = MathHelper.clamp(newPitch, -size, size);
                this.riderYaw -= newYaw;
                this.riderPitch -= newPitch;
            }
        }
    }

    public void mountEntity(Entity entity) {
        if (!this.crawling && !this.sneaking && !this.flying) {
            this.riderPitch = 0.0;
            this.riderYaw = 0.0;
            if (entity == null) {
                if (this.mount != null) {
                    this.setPosAndRot(this.mount.posX, this.mount.boundingBox.minY + this.mount.height, this.mount.posZ, this.yaw, this.pitch);
                    this.mount.rider = null;
                }

                this.mount = null;
            } else if (this.mount == entity) {
                this.mount.rider = null;
                this.mount = null;
                this.setPosAndRot(entity.posX, entity.boundingBox.minY + entity.height, entity.posZ, this.yaw, this.pitch);
            } else {
                if (this.mount != null) {
                    this.mount.rider = null;
                }

                if (entity.rider != null) {
                    entity.rider.mount = null;
                }

                this.mount = entity;
                entity.rider = this;
            }
        }
    }

    public void teleportInServer(double x, double y, double z, float yaw, float pitch, int state) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
        List<BoundingBox> bbList = this.world.getBoundingBoxes(this, this.boundingBox.shrink(0.03125, 0.0, 0.03125));
        if (!bbList.isEmpty()) {
            double max = 0.0;

            for (BoundingBox aabb : bbList) {
                if (aabb.maxY > max) {
                    max = aabb.maxY;
                }
            }

            y += max - this.boundingBox.minY;
            this.setPosition(x, y, z);
        }
    }

    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void updateState(byte state) {
    }

    public void hurtAnim() {
    }

    public void equipItem(int slot, int id, int dmg) {
    }

    public boolean isBurning() {
        return this.fire > 0 || this.getFlag(0);
    }

    public boolean isRiding() {
        return this.mount != null || this.getFlag(2);
    }

    public ItemStack[] getInventory() {
        return null;
    }

    protected boolean getFlag(int i) {
        return (this.dataSync.getObjectByte(0) & 1 << i) != 0;
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean flag) {
        this.setFlag(3, flag);
    }

    public void setCanSprint(boolean flag) {
        this.setFlag(4, flag);
    }

    public boolean canSprint() {
        return false;
    }

    protected void setFlag(int i, boolean flag) {
        byte b = this.dataSync.getObjectByte(0);
        if (flag) {
            this.dataSync.updateObject(0, (byte)(b | 1 << i));
        } else {
            this.dataSync.updateObject(0, (byte)(b & ~(1 << i)));
        }
    }

    public boolean isEating() {
        return this.getFlag(4);
    }

    public void setEating(boolean flag) {
        this.setFlag(4, flag);
    }
}
