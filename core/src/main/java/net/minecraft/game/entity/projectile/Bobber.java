package net.minecraft.game.entity.projectile;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.WeightedRandom;

public class Bobber extends Entity {
    public Player angler;
    public Entity wrangledEntity = null;
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private int ticksInGround = 0;
    private int ticksInAir = 0;
    private int ticksCatchable = 0;
    private int airTime;
    private int catchDelay;
    private float fishAngle;
    private double netX;
    private double netY;
    private double netZ;
    private double netPitch;
    private double netYaw;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private boolean hasBait;
    private boolean inGround = false;
    private static Bobber.LootHolder[] trash = new Bobber.LootHolder[]{
        new Bobber.LootHolder(100, ItemList.stick),
        new Bobber.LootHolder(85, ItemList.rock),
        new Bobber.LootHolder(75, ItemList.reeds),
        new Bobber.LootHolder(95, ItemList.silk),
        new Bobber.LootHolder(65, BlockList.lilyPad),
        new Bobber.LootHolder(45, ItemList.bamboo),
        new Bobber.LootHolder(55, ItemList.bootsLeather),
        new Bobber.LootHolder(70, ItemList.leather),
        new Bobber.LootHolder(80, ItemList.bone),
        new Bobber.LootHolder(90, ItemList.rottenFlesh),
        new Bobber.LootHolder(65, ItemList.bowlEmpty),
        new Bobber.LootHolder(60, ItemList.potion)
    };
    private static Bobber.LootHolder[] treasure = new Bobber.LootHolder[]{
        new Bobber.LootHolder(100, ItemList.seaPickle),
        new Bobber.LootHolder(80, ItemList.bookAndQuill),
        new Bobber.LootHolder(85, ItemList.tentacle),
        new Bobber.LootHolder(65, ItemList.nameTag),
        new Bobber.LootHolder(60, ItemList.scroll),
        new Bobber.LootHolder(35, ItemList.saddle),
        new Bobber.LootHolder(50, ItemList.fishingRod),
        new Bobber.LootHolder(30, ItemList.backpack),
        new Bobber.LootHolder(40, ItemList.crossbow),
        new Bobber.LootHolder(45, ItemList.bow),
        new Bobber.LootHolder(55, ItemList.recordSea)
    };

    public Bobber(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public Bobber(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    public Bobber(World world, Player player, boolean bait) {
        super(world);
        this.angler = player;
        this.angler.bobber = this;
        this.hasBait = bait;
        this.setSize(0.25F, 0.25F);
        this.teleportTo(player.posX, player.posY + 1.62F - player.yOffset, player.posZ, player.yaw, player.pitch);
        this.posX = this.posX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        float offset = 0.4F;
        this.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
        this.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
        this.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * offset;
        this.setHeading(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
    }

    @Override
    protected void init() {
    }

    public void setHeading(double x, double y, double z, float yaw, float pitch) {
        float divider = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= divider;
        y /= divider;
        z /= divider;
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
        this.ticksInGround = 0;
    }

    public void setPositionServer(float x, float y, float z, float pitch, float yaw, int time) {
        this.netX = x;
        this.netY = y;
        this.netZ = z;
        this.netPitch = pitch;
        this.netYaw = yaw;
        this.airTime = time;
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
    public void tick() {
        super.tick();
        if (this.airTime > 0) {
            double x = this.posX + (this.netX - this.posX) / this.airTime;
            double y = this.posY + (this.netY - this.posY) / this.airTime;
            double z = this.posZ + (this.netZ - this.posZ) / this.airTime;
            double angle = MathHelper.computeAngle((float)this.netPitch - this.yaw);
            this.yaw = (float)(this.yaw + angle / this.airTime);
            this.pitch = (float)(this.pitch + (this.netYaw - this.pitch) / this.airTime);
            this.airTime--;
            this.setPosition(x, y, z);
            this.setRotation(this.yaw, this.pitch);
        } else {
            if (!this.world.isOnline) {
                ItemStack stack = this.angler.inventory.mainInventory[this.angler.inventory.currentSlot];
                if (this.angler.inventory.canUseItem()) {
                    stack = this.angler.inventory.getOffHandItem();
                }

                if (this.angler.isDead
                    || !this.angler.isAlive()
                    || stack == null
                    || stack.getItem() != ItemList.fishingRod
                    || this.getDistanceSqTo(this.angler) > 1024.0) {
                    this.setDead();
                    this.angler.bobber = null;
                    return;
                }

                if (this.wrangledEntity != null) {
                    if (!this.wrangledEntity.isDead) {
                        this.posX = this.wrangledEntity.posX;
                        this.posY = this.wrangledEntity.boundingBox.minY + this.wrangledEntity.height * 0.8F;
                        this.posZ = this.wrangledEntity.posZ;
                        return;
                    }

                    this.wrangledEntity = null;
                }
            }

            if (this.inGround) {
                int id = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
                if (id == this.inTile) {
                    this.ticksInGround++;
                    if (this.ticksInGround == 1200) {
                        this.setDead();
                    }

                    return;
                }

                this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
                this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
                this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                this.ticksInAir++;
            }

            Vector currPos = new Vector(this.posX, this.posY, this.posZ);
            Vector targetPos = new Vector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            TargetingSystem hit = this.world.rayTraceBlocks(currPos, targetPos);
            currPos = new Vector(this.posX, this.posY, this.posZ);
            targetPos = new Vector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (hit != null) {
                targetPos = new Vector(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
            }

            Entity target = null;
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double dist = 0.0;

            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                if (entity.isSelectable() && (entity != this.angler || this.ticksInAir >= 5)) {
                    BoundingBox axisAlignedBB11 = entity.boundingBox.expand(0.3F, 0.3F, 0.3F);
                    TargetingSystem movingObjectPosition12 = axisAlignedBB11.calculateIntercept(currPos, targetPos);
                    if (movingObjectPosition12 != null) {
                        double maxDist = currPos.distanceTo(movingObjectPosition12.hitVec);
                        if (maxDist < dist || dist == 0.0) {
                            target = entity;
                            dist = maxDist;
                        }
                    }
                }
            }

            if (target != null) {
                hit = new TargetingSystem(target);
            }

            if (hit != null) {
                if (hit.entityHit != null) {
                    if (hit.entityHit.damageEntity(DamageSource.projectileDamage(this, this.angler), 0)) {
                        this.wrangledEntity = hit.entityHit;
                    }
                } else {
                    this.inGround = true;
                }
            }

            if (!this.inGround) {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
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
                double forceExert = 0.92;
                if (this.onGround || this.collidedHorizontally) {
                    forceExert = 0.5;
                }

                byte amount = 5;
                double offset = 0.0;

                for (int pos = 0; pos < amount; pos++) {
                    double minY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (pos + 0) / amount - 0.125 + 0.125;
                    double maxY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (pos + 1) / amount - 0.125 + 0.125;
                    BoundingBox boundingBox = BoundingBox.getBoundingBox(
                        this.boundingBox.minX, minY, this.boundingBox.minZ, this.boundingBox.maxX, maxY, this.boundingBox.maxZ
                    );
                    if (this.world.containsMaterial(boundingBox, MaterialList.water)) {
                        offset += 1.0 / amount;
                    }
                }

                if (offset > 0.0 && !this.world.isOnline) {
                    if (this.ticksCatchable > 0) {
                        this.ticksCatchable--;
                    } else {
                        short chance = 500;
                        if (this.hasBait
                            || this.world.isRaining()
                                && this.world
                                    .isBlockExposed(
                                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1.0), MathHelper.floor_double(this.posZ)
                                    )) {
                            chance = 200;
                        }

                        if (this.rand.nextInt(chance) == 0 && this.catchDelay == 0) {
                            this.fishAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                            this.catchDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                        }

                        if (this.catchDelay > 0) {
                            this.catchDelay--;
                            this.fishAngle = (float)(this.fishAngle + this.rand.nextGaussian() * 4.0);
                            float fishAngle = this.fishAngle * (float) (Math.PI / 180.0);
                            float fishYaw = MathHelper.sin(fishAngle);
                            float fishPitch = MathHelper.cos(fishAngle);
                            double fishX = this.posX + fishYaw * this.catchDelay * 0.1F;
                            double fishY = MathHelper.floor_double(this.boundingBox.minY) + 1.0F;
                            double fishZ = this.posZ + fishPitch * this.catchDelay * 0.1F;
                            Block block = this.world
                                .getBlock(MathHelper.floor_double(fishX), MathHelper.floor_double(fishY) - 1, MathHelper.floor_double(fishZ));
                            if (block == BlockList.waterStill || block == BlockList.waterMoving) {
                                double yaw = fishYaw * 0.04;
                                double pitch = fishPitch * 0.04;
                                this.world.spawnParticle(ParticleType.FISH, fishX, fishY, fishZ, pitch, 0.01, -yaw);
                                this.world.spawnParticle(ParticleType.FISH, fishX, fishY, fishZ, -pitch, 0.01, yaw);
                            }
                        }

                        if (this.catchDelay != 1) {
                            if (this.catchDelay == 0 && this.rand.nextInt(3) == 0) {
                                this.fishAngle = (float)(this.fishAngle + this.rand.nextGaussian() * 4.0);
                                float fishAngle = this.fishAngle;
                                float fishYaw = MathHelper.sin(fishAngle);
                                float fishPitch = MathHelper.cos(fishAngle);
                                double fishX = this.posX + MathHelper.randomFloatClamp(this.rand, -10.0F, 10.0F);
                                double fishY = MathHelper.floor_double(this.boundingBox.minY) + 1.0F;
                                double fishZ = this.posZ + MathHelper.randomFloatClamp(this.rand, -10.0F, 10.0F);
                                Block block = this.world
                                    .getBlock(MathHelper.floor_double(fishX), MathHelper.floor_double(fishY) - 1, MathHelper.floor_double(fishZ));
                                if (block == BlockList.waterStill || block == BlockList.waterMoving) {
                                    float yaw = fishYaw * 0.04F;
                                    float pitch = fishPitch * 0.04F;
                                    this.world.spawnParticle(ParticleType.RAINDROP, fishX, fishY, fishZ, pitch, 0.01, yaw);
                                    this.world.spawnParticle(ParticleType.RAINDROP, fishX, fishY, fishZ, pitch, 0.01, -yaw);
                                }
                            }
                        } else {
                            this.ticksCatchable = this.rand.nextInt(30) + 10;
                            this.motionY -= 0.2;
                            this.world.playEntitySound(this, "random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                            float yOff = MathHelper.floor_double(this.boundingBox.minY);

                            for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
                                float xOff = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                                float zOff = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                                this.world
                                    .spawnParticle(
                                        ParticleType.BUBBLE,
                                        this.posX + xOff,
                                        yOff + 1.0F,
                                        this.posZ + zOff,
                                        this.motionX,
                                        this.motionY - this.rand.nextFloat() * 0.2F,
                                        this.motionZ
                                    );
                            }

                            for (int var45 = 0; var45 < 1.0F + this.width * 20.0F; var45++) {
                                float xOff = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                                float zOff = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                                this.world
                                    .spawnParticle(
                                        ParticleType.SPLASH, this.posX + xOff, yOff + 1.0F, this.posZ + zOff, this.motionX, this.motionY, this.motionZ
                                    );
                            }
                        }
                    }
                }

                if (this.ticksCatchable > 0) {
                    this.motionY = this.motionY - this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2;
                }

                double maxDist = offset * 2.0 - 1.0;
                this.motionY += 0.04 * maxDist;
                if (offset > 0.0) {
                    forceExert *= 0.9;
                    this.motionY *= 0.8;
                }

                this.motionX *= forceExert;
                this.motionY *= forceExert;
                this.motionZ *= forceExert;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setShort("xTile", (short)this.xTile);
        tag.setShort("yTile", (short)this.yTile);
        tag.setShort("zTile", (short)this.zTile);
        tag.setShort("inTile", (short)this.inTile);
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.xTile = tag.getShort("xTile");
        this.yTile = tag.getShort("yTile");
        this.zTile = tag.getShort("zTile");
        this.inTile = tag.getShort("inTile");
        this.inGround = tag.getByte("inGround") == 1;
    }

    public int reelIn() {
        byte reelType = 0;
        double distX = this.angler.posX - this.posX;
        double distY = this.angler.posY - this.posY;
        double distZ = this.angler.posZ - this.posZ;
        double distTotal = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);
        double distOffset = 0.1;
        if (this.wrangledEntity != null) {
            this.wrangledEntity.motionX += distX * distOffset;
            this.wrangledEntity.motionY = this.wrangledEntity.motionY + (distY * distOffset + MathHelper.sqrt_double(distTotal) * 0.08F);
            this.wrangledEntity.motionZ += distZ * distOffset;
            this.angler.triggerAchievement(AchievementList.wrangle);
            reelType = 3;
        } else if (this.ticksCatchable > 0) {
            int scoreVal = 10;
            int id = this.rand.nextInt(10);
            if (this.angler.getLuckModifier()) {
                id = this.rand.nextInt(3);
            }

            int fishedItem = ItemList.fishRaw.id;
            if (id == 0) {
                Bobber.LootHolder randomItem = WeightedRandom.getRandomItem(this.rand, trash);
                if (this.rand.nextBoolean() && !this.angler.hasEffect(EffectList.badLuck)) {
                    randomItem = WeightedRandom.getRandomItem(this.rand, treasure);
                    scoreVal = 50;
                }

                fishedItem = randomItem.itemId;
            }

            Item item = ItemList.items[fishedItem];
            ItemStack stack = new ItemStack(item);
            if (item.isDamageable()) {
                stack = new ItemStack(fishedItem, 1, this.rand.nextInt(item.getMaxDamage()));
            }

            DroppedItem drop = new DroppedItem(this.world, this.posX, this.posY, this.posZ, stack);
            distY = this.angler.posY - this.posY - this.angler.yOffset;
            distTotal = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);
            drop.motionX = distX * distOffset;
            drop.motionY = distY * distOffset + MathHelper.sqrt_double(distTotal) * 0.08F;
            drop.motionZ = distZ * distOffset;
            this.world.spawnEntity(drop);
            this.angler.score += scoreVal;
            this.angler.addStat(StatList.fishCaughtStat, 1);
            reelType = 1;
        }

        if (this.inGround) {
            reelType = 2;
        }

        this.setDead();
        this.angler.bobber = null;
        return reelType;
    }

    @Override
    public boolean inRange(double range) {
        return true;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }

    private static class LootHolder extends WeightedRandom.Choice {
        public final int itemId;

        private LootHolder(int weight, Item item) {
            super(weight);
            this.itemId = item.id;
        }

        public LootHolder(int weight, Block cock) {
            super(weight);
            this.itemId = cock.id;
        }
    }
}
