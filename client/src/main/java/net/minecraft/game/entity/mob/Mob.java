package net.minecraft.game.entity.mob;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Profiler;
import net.minecraft.game.ParticleContext;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.CloudBlock;
import net.minecraft.game.block.building.Leaves;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.SlimeBlock;
import net.minecraft.game.block.misc.StoneSpike;
import net.minecraft.game.block.plants.Clover;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.ai.TaskList;
import net.minecraft.game.entity.mob.ai.util.BodyControl;
import net.minecraft.game.entity.mob.ai.util.JumpControl;
import net.minecraft.game.entity.mob.ai.util.LookControl;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.mob.ai.util.Senses;
import net.minecraft.game.entity.mob.animal.Monkey;
import net.minecraft.game.entity.mob.animal.mounts.RideableMob;
import net.minecraft.game.entity.mob.monster.Ghast;
import net.minecraft.game.entity.mob.monster.arachnid.Termite;
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.BrewingSystem;
import net.minecraft.game.item.potion.Effect;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.tools.GrapplingHook;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.SkyDimension;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.game.world.pathfinder.PathNavigator;
import net.minecraft.game.world.util.Position;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public abstract class Mob extends Entity {
    public HashMap<Integer, EffectData> activeEffects = new HashMap<>();
    public String customName = "";
    public String texture = "/mob/player/char.png";
    public PathNavigator navigator;
    public MoveControl moveControl;
    public LookControl lookControl;
    public JumpControl jumpControl;
    public BodyControl bodyControl;
    public Senses senses;
    public TaskList behaviors = new TaskList();
    public TaskList detections = new TaskList();
    public Mob attackTarget;
    public Mob lastTarget;
    private Position homePos = new Position(0, 0, 0);
    public int invulnerabilityFrames = 20;
    public int attackStrength = 2;
    public int scoreValue = 0;
    public int health = 10;
    public int maxHealth = 10;
    public int prevHealth;
    public int hurtTime;
    public int hurtDuration;
    private int soundTimer;
    public int deathTimer = 0;
    protected int damageTaken = 0;
    public int age = 0;
    private int effectID;
    public int healingAmount;
    protected int carryoverDamage;
    protected int rotIncrements;
    public int calmTime;
    public int tier = -1;
    public int attackFrames;
    private int arrowHitTimer;
    private int arrowCount;
    public float yawOffset = 0.0F;
    public float prevYawOffset = 0.0F;
    public float headFacing = 0.0F;
    public float prevFacing = 0.0F;
    public float swing;
    public float prevSwing;
    public float swingOffhand;
    public float prevSwingOffhand;
    public float attackAngle = 0.0F;
    public float limbYaw;
    public float prevLimbYaw;
    public float limbSwing;
    public float moveForward;
    public float moveStrafing;
    public float moveSpeed = 0.7F;
    public float landSpeed = 0.1F;
    public float jumpSpeed = 0.02F;
    private float particleCounter;
    private float maxHomeDist = -1.0F;
    protected double newX;
    protected double newY;
    protected double newZ;
    protected double newYaw;
    protected double newPitch;
    protected boolean dead = false;
    private boolean updateEffects = true;
    public boolean isMultiplayer = false;
    public byte type = 0;

    public Mob(World world) {
        super(world);
        this.preventSpawning = true;
        this.navigator = new PathNavigator(this, world, 16.0F);
        this.moveControl = new MoveControl(this);
        this.lookControl = new LookControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyControl = new BodyControl(this);
        this.senses = new Senses(this);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yaw = (float)(Math.random() * (float) Math.PI * 2.0);
        this.stepHeight = 0.5F;
        this.headFacing = this.yaw;
    }

    @Override
    protected void init() {
        this.dataSync.addObject(8, this.effectID);
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public boolean isPushable() {
        return !this.isDead;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    public void playSound() {
        String snd = this.getSound();
        if (snd != null) {
            this.world.playEntitySound(this, snd, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    public String getSound() {
        return null;
    }

    protected String getHurtSound() {
        return "random.hurt";
    }

    protected String getDeathSound() {
        return "random.hurt";
    }

    public float getSoundVolume() {
        return 1.0F;
    }

    public int getSoundInterval() {
        return 80;
    }

    public void setType() {
    }

    public void setHealth(int health) {
        this.health = this.maxHealth = health;
    }

    @Override
    public Vector getLookVector(float angle) {
        if (angle == 1.0F) {
            float yaw1 = MathHelper.cos(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float yaw2 = MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float pitch1 = -MathHelper.cos(-this.pitch * (float) (Math.PI / 180.0));
            float pitch2 = MathHelper.sin(-this.pitch * (float) (Math.PI / 180.0));
            return Vector.createVector(yaw2 * pitch1, pitch2, yaw1 * pitch1);
        } else {
            float yaw1 = this.prevPitch + (this.pitch - this.prevPitch) * angle;
            float yaw2 = this.prevYaw + (this.yaw - this.prevYaw) * angle;
            float pitch1 = MathHelper.cos(-yaw2 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float pitch2 = MathHelper.sin(-yaw2 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float mult1 = -MathHelper.cos(-yaw1 * (float) (Math.PI / 180.0));
            float mult2 = MathHelper.sin(-yaw1 * (float) (Math.PI / 180.0));
            return Vector.createVector(pitch2 * mult1, mult2, pitch1 * mult1);
        }
    }

    @Override
    public void tick() {
        this.prevSwing = this.swing;
        this.prevSwingOffhand = this.swingOffhand;
        super.tick();
        Profiler.startSection("mobBaseTick");
        if (this.isAlive() && this.rand.nextInt(1000) < this.soundTimer++) {
            this.soundTimer = this.soundTimer - this.getSoundInterval();
            this.playSound();
        }

        if (this.isAlive() && this.isInsideBlock()) {
            this.damageEntity(DamageSource.inWall, 1);
        }

        if (this.world.isOnline || !this.canBurn()) {
            this.fire = 0;
        }

        if (!this.isInMaterial(MaterialList.water) || !this.isAlive() || this.canBreatheUnderwater()) {
            this.particleCounter -= 7.5F;
            if (this.particleCounter < 0.0F) {
                this.particleCounter = 0.0F;
            }

            this.air += 7;
            if (this.air > this.maxAir) {
                this.air = this.maxAir;
            }
        } else if (this.isInMagmaAirPocket()) {
            this.particleCounter -= 5.0F;
            if (this.particleCounter < 0.0F) {
                this.particleCounter = 0.0F;
            }

            this.air += 5;
            if (this.air > this.maxAir) {
                this.air = this.maxAir;
            }
        } else {
            this.particleCounter++;
            if (this.air > this.maxAir) {
                this.air = this.maxAir;
            }

            this.air--;
            if (this.air == -20) {
                this.air = 0;

                for (int i = 0; i < 8; i++) {
                    float x = this.rand.nextFloat() - this.rand.nextFloat();
                    float y = this.rand.nextFloat() - this.rand.nextFloat();
                    float z = this.rand.nextFloat() - this.rand.nextFloat();
                    this.world.spawnParticle(ParticleType.BUBBLE, this.posX + x, this.posY + y, this.posZ + z, this.motionX, this.motionY, this.motionZ);
                }

                this.damageEntity(DamageSource.drown, 2);
            }

            this.fire = 0;
        }

        if (this.isInWater() && this.world.currDim instanceof VoidDimension) {
            this.addEffect(new EffectData(EffectList.weakness.id, 200, 0));
            this.addEffect(new EffectData(EffectList.poison.id, 80, 3));
            this.addEffect(new EffectData(EffectList.fragility.id, 150, 0));
            this.addEffect(new EffectData(EffectList.slowness.id, 120, 1));
        }

        if (this.health < this.maxHealth && this.isInWater() && this.ticksExisted % 100 * 4 == 0 && this.world.currDim instanceof SkyDimension) {
            this.heal(1);
        }

        if (this.hurtTime > 0) {
            this.hurtTime--;
        }

        if (this.damage > 0) {
            this.damage--;
        }

        if (this.attackFrames > 0) {
            this.attackFrames--;
        }

        if (this.health <= 0) {
            this.onDeath();
        }

        if (this.freeze == 1) {
            for (int i = 0; i < 20; i++) {
                this.world
                    .spawnParticle(
                        ParticleType.EXPLODE,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02
                    );
            }
        }

        if (this.lastTarget != null && !this.lastTarget.isAlive()) {
            this.lastTarget = null;
        }

        this.tickEffects();
        this.prevYawOffset = this.yawOffset;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevFacing = this.headFacing;

        for (int x = -5; x < 6; x++) {
            for (int z = -5; z < 6; z++) {
                int id = this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z);
                int id2 = this.world.getBlockId(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
                int movement = 1;
                if (id == BlockList.fan.id) {
                    switch (this.world
                        .getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z)) {
                        case 6:
                            if (z > 0) {
                                this.motionZ -= movement * 0.001;
                            }
                            break;
                        case 7:
                            if (z < 0) {
                                this.motionZ += movement * 0.001;
                            }
                    }
                }

                if (id2 == BlockList.fan.id) {
                    switch (this.world
                        .getBlockMetadata(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
                        case 8:
                            if (x > 0) {
                                this.motionX -= movement * 0.001;
                            }
                            break;
                        case 9:
                            if (x < 0) {
                                this.motionX += movement * 0.001;
                            }
                    }
                }
            }
        }

        Profiler.endSection();
    }

    @Override
    public void despawnTick() {
        Profiler.startSection("checkDespawn");
        this.age++;
        this.despawn();
        Profiler.endSection();
    }

    public boolean canBreatheUnderwater() {
        return this.hasEffect(EffectList.waterBreathing);
    }

    @Override
    public boolean canBurn() {
        return !this.hasEffect(EffectList.fireResistance);
    }

    public void spawnPuffParticles() {
        for (int i = 0; i < 20; i++) {
            this.world
                .spawnParticle(
                    ParticleType.EXPLODE,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - this.rand.nextGaussian() * 0.02 * 10.0,
                    this.posY + this.rand.nextFloat() * this.height - this.rand.nextGaussian() * 0.02 * 10.0,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - this.rand.nextGaussian() * 0.02 * 10.0,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02
                );
        }
    }

    @Override
    public void teleportInServer(double x, double y, double z, float yaw, float pitch, int mode) {
        this.yOffset = 0.0F;
        this.newX = x;
        this.newY = y;
        this.newZ = z;
        this.newYaw = yaw;
        this.newPitch = pitch;
        this.rotIncrements = mode;
        this.headFacing = yaw;
    }

    public boolean isInMagmaAirPocket() {
        return this.world.handleWaterMovement(this.boundingBox.expand(0.0, 10.0, 0.0), MaterialList.magma, this);
    }

    @Override
    public void onTick() {
        super.onTick();
        this.mobTick();
        this.rotateBody();

        while (this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
        }

        while (this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
        }

        while (this.yawOffset - this.prevYawOffset < -180.0F) {
            this.prevYawOffset -= 360.0F;
        }

        while (this.yawOffset - this.prevYawOffset >= 180.0F) {
            this.prevYawOffset += 360.0F;
        }

        while (this.pitch - this.prevPitch < -180.0F) {
            this.prevPitch -= 360.0F;
        }

        while (this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
        }

        while (this.headFacing - this.prevFacing < -180.0F) {
            this.prevFacing -= 360.0F;
        }

        while (this.headFacing - this.prevFacing >= 180.0F) {
            this.prevFacing += 360.0F;
        }
    }

    public void rotateBody() {
        this.bodyControl.tick();
    }

    public void heal(int amount) {
        if (this.health > 0 && !this.hasEffect(EffectList.wither)) {
            this.health += amount;
            if (this.health > this.maxHealth) {
                this.health = this.maxHealth;
            }

            this.damage = this.invulnerabilityFrames / 2;
        }
    }

    public void addHealth(int amount) {
        if (this.health >= this.maxHealth) {
            this.health += amount;
            if (this.health > this.maxHealth + 20) {
                this.health = this.maxHealth + 20;
            }

            this.damage = this.invulnerabilityFrames / 2;
        }
    }

    public void queueHealing(int amount) {
        if (this.health > 0 && this.health < this.maxHealth) {
            this.healingAmount += amount;
        }
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        if (this.world.isOnline) {
            return false;
        }

        this.age = 0;
        if (this.health <= 0) {
            return false;
        }

        if (source.isFireDamage() && !this.canBurn()) {
            return false;
        }

        this.limbYaw = 1.5F;
        boolean canDamage = true;
        if (this.damage > this.invulnerabilityFrames / 2.0F) {
            if (amount <= this.damageTaken) {
                return false;
            }

            this.applyDamageMultiplier(source, amount - this.damageTaken);
            this.damageTaken = amount;
            canDamage = false;
        } else {
            this.damageTaken = amount;
            this.prevHealth = this.health;
            this.damage = this.invulnerabilityFrames;
            this.applyDamageMultiplier(source, amount);
            this.hurtTime = this.hurtDuration = 10;
        }

        this.attackAngle = 0.0F;
        if (canDamage) {
            this.world.updateServerEntity(this, (byte)2);
            this.setAttacked();
            Entity entity = source.getEntity();
            if (entity != null) {
                double distX = entity.posX - this.posX;

                double distZ;
                for (distZ = entity.posZ - this.posZ; distX * distX + distZ * distZ < 1.0E-4; distZ = (Math.random() - Math.random()) * 0.01) {
                    distX = (Math.random() - Math.random()) * 0.01;
                }

                this.attackAngle = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - this.yaw;
                this.knockBack(entity, distX, distZ);
                if (entity instanceof Mob) {
                    this.lastTarget = (Mob)entity;
                }
            } else {
                this.attackAngle = (int)(Math.random() * 2.0) * 180;
            }
        }

        if (source.damageType == "fall") {
            if (amount > 4) {
                this.world.playEntitySound(this, "random.fallbig", 1.0F, 1.0F);
            } else {
                this.world.playEntitySound(this, "random.fallsmall", 1.0F, 1.0F);
            }
        }

        if (this.health <= 0) {
            if (canDamage) {
                this.world.playEntitySound(this, this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.onDeath(source);
        } else if (canDamage) {
            this.world.playEntitySound(this, this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }

        return true;
    }

    protected void applyDamageMultiplier(DamageSource src, int amount) {
        amount = this.applyDamageModifier(src, amount);
        this.health -= amount;
    }

    public void knockBack(Entity entity, double forceX, double forceZ) {
        float dist = MathHelper.sqrt_double(forceX * forceX + forceZ * forceZ);
        float force = entity.knockback;
        this.motionX /= 2.0;
        this.motionY /= 2.0;
        this.motionZ /= 2.0;
        this.motionX -= forceX / dist * force;
        this.motionY += 0.4F;
        this.motionZ -= forceZ / dist * force;
        if (this.motionY > 0.4F) {
            this.motionY = 0.4F;
        }
    }

    public void onDeath(DamageSource source) {
        Entity entity = source.getEntity();
        boolean luck = entity != null ? entity.getLuckModifier() : false;
        int lootingModifier = 0;
        if (entity != null) {
            if (!this.world.bloodMoon && !luck) {
                entity.addScore(this, this.scoreValue);
            } else {
                entity.addScore(this, this.scoreValue * 2);
            }

            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (luck) {
                    if (player.hasEffect(EffectList.luck)) {
                        lootingModifier = ((Player)entity).getEffect(EffectList.luck).getPotionAmplifier() + 1;
                    } else {
                        lootingModifier = 1;
                    }
                }

                player.addStat(StatList.killStats[this.getSpawnID()], 1);
                if (this.boss) {
                    player.addStat(StatList.bossKillsStat, 1);
                }

                player.onKillEntity(this);
            }
        }

        this.dead = true;
        if (!this.world.isOnline
            && this.world.settings.mobDrops
            && (!(entity instanceof Player) || this.boss || !((Player)entity).hasEffect(EffectList.badLuck) || !this.rand.nextBoolean())) {
            this.dropFewItems(lootingModifier);
            if (this.specialDrop() > 0) {
                this.dropSpecialItem(source, luck);
            }
        }

        if (this.boss && this.tier >= 0 && this.world.settings.mobDrops) {
            this.dropItem(ItemList.key.id, 1, this.tier, 1.0F);
        }

        this.world.updateServerEntity(this, (byte)3);
    }

    public void dropSpecialItem(DamageSource ds, boolean luck) {
        Entity entity = ds.getEntity();
        int lootModifier = 5;
        if (entity instanceof Player) {
            if (luck) {
                lootModifier = 2;
            } else if (((Player)entity).hasEffect(EffectList.badLuck)) {
                lootModifier = 10;
            }
        }

        if (!this.world.isOnline && this.rand.nextInt(lootModifier) == 0) {
            this.dropItem(this.specialDrop(), 1);
        }
    }

    protected void dropFewItems(int lootingModifier) {
        int id = this.getDrop();
        if (id > 0) {
            int amount = this.getDropCount() + lootingModifier;

            for (int i = 0; i < amount; i++) {
                this.dropItem(id, 1);
            }
        }
    }

    public int specialDrop() {
        return 0;
    }

    public int getDrop() {
        return 0;
    }

    public int getDropCount() {
        return this.rand.nextInt(3);
    }

    @Override
    protected void fall(float dist) {
        int dmg = (int)Math.ceil(dist - 3.0F);
        if (this.hasEffect(EffectList.jump)) {
            dmg /= 2 + this.getEffect(EffectList.jump).getPotionAmplifier();
        }

        Block block = this.world
            .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2F - this.yOffset), MathHelper.floor_double(this.posZ));
        if (block instanceof Bed || block == BlockList.hayBlock || block instanceof Leaves || block instanceof Clover || this instanceof RideableMob) {
            dmg /= 2;
        } else if (block instanceof StoneSpike && dist >= 5.0F) {
            dmg *= 2;
        } else if (block instanceof SlimeBlock || block instanceof CloudBlock) {
            return;
        }

        if (dmg > 0 && !this.hasEffect(EffectList.slowFall)) {
            this.damageEntity(DamageSource.fall, dmg);
            if (block != null) {
                int metadata = this.world
                    .getBlockMetadata(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2F - this.yOffset), MathHelper.floor_double(this.posZ)
                    );
                this.world
                    .playEffect(
                        2001,
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY - 1.0),
                        MathHelper.floor_double(this.posZ),
                        metadata << 12 | block.id
                    );
            }
        }
    }

    public void moveEntityWithHeading(float motionX, float motionY) {
        float movementSpeed = 0.91F;
        if ((!this.isInWater() || this.flying) && (!this.isInLava() || this.canBurn())) {
            if (this.isInLava() && !this.flying && this.canBurn()) {
                double y = this.posY;
                this.moveRelative(motionX, motionY, 0.02F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5;
                this.motionY *= 0.5;
                this.motionZ *= 0.5;
                this.motionY -= 0.02;
                this.fallDistance = 0.0F;
                if (this.collidedHorizontally && this.offsetPosInLiquid(this.motionX, this.motionY + 0.6F - this.posY + y, this.motionZ)) {
                    this.motionY = 0.3F;
                }
            } else if (this.flying) {
                double y = this.posY;
                this.moveFlying(motionX, motionY, 0.0F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.8F;
                this.motionY *= 0.8F;
                this.motionZ *= 0.8F;
                this.motionY = 0.0;
                this.fallDistance = 0.0F;
            } else {
                float slipperiness = 0.6F;
                if (this.onGround) {
                    int id = this.world
                        .getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
                    if (id > 0) {
                        slipperiness = BlockList.blocks[id].slipperiness;
                    }
                }

                float slipperySpeed = movementSpeed * slipperiness;
                float movementFactor = 0.16277136F / (slipperySpeed * slipperySpeed * slipperySpeed);
                float moveSpeed;
                if (this.onGround) {
                    moveSpeed = this.landSpeed;
                    moveSpeed *= movementFactor;
                } else {
                    moveSpeed = this.jumpSpeed;
                }

                this.moveRelative(motionX, motionY, moveSpeed);
                if (this.canClimb(BlockList.ladder) || this.canClimb(BlockList.vine) || this.canClimb(MaterialList.rope)) {
                    this.fallDistance = 0.0F;
                    if (this.motionY < -0.15) {
                        this.motionY = -0.15;
                    }

                    if (this.sneaking && this.motionY < 0.0) {
                        this.motionY = 0.0;
                    }
                }

                if (this.hasEffect(EffectList.slowFall)) {
                    this.fallDistance = 0.0F;
                    if (this.motionY < -0.15) {
                        this.motionY = -0.15;
                    }
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                if (this.collidedHorizontally && (this.canClimb(BlockList.ladder) || this.canClimb(BlockList.vine))
                    || this.jumping && this.canClimb(MaterialList.rope)) {
                    this.motionY = 0.2;
                }

                if (this.hasEffect(EffectList.levitation)) {
                    this.motionY = 0.15;
                }

                if (this.sneaking || this.crawling && this.onGround) {
                    movementSpeed = 0.1F;
                }

                this.motionX *= movementSpeed;
                this.motionY *= 0.98F;
                this.motionZ *= movementSpeed;
                this.motionY -= 0.08;
                if (this.onGround) {
                    this.motionX *= slipperiness;
                    this.motionZ *= slipperiness;
                }
            }
        } else {
            double y = this.posY;
            this.moveRelative(motionX, motionY, this.jumpSpeed);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            if (this.isInMaterial(MaterialList.water) && this.crawling) {
                this.motionX *= 0.91F;
                this.motionY *= 0.91F;
                this.motionZ *= 0.91F;
            } else {
                this.motionX *= 0.8F;
                this.motionY *= 0.8F;
                this.motionZ *= 0.8F;
            }

            this.motionY -= 0.02;
            this.fallDistance = 0.0F;
            if (this.collidedHorizontally && this.offsetPosInLiquid(this.motionX, this.motionY + 0.6F - this.posY + y, this.motionZ)) {
                this.motionY = 0.3F;
            }
        }

        this.prevLimbYaw = this.limbYaw;
        double distX = this.posX - this.prevX;
        double distZ = this.posZ - this.prevZ;
        float dist = MathHelper.sqrt_double(distX * distX + distZ * distZ) * 4.0F;
        if (dist > 1.0F) {
            dist = 1.0F;
        }

        this.limbYaw = this.limbYaw + (dist - this.limbYaw) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbYaw;
    }

    public boolean canClimb(Block id) {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlock(x, y, z) == id;
    }

    public boolean canClimb(Material id) {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockMaterial(x, y, z) == id || this.world.getBlockMaterial(x, y + 1, z) == id;
    }

    public boolean isInsideBlock() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY + this.getEyeHeight());
        int z = MathHelper.floor_double(this.posZ);
        return this.world.isBlockFull(x, y, z);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setShort("Health", (short)this.health);
        tag.setShort("MaxHealth", (short)this.maxHealth);
        tag.setShort("HurtTime", (short)this.hurtTime);
        tag.setShort("HealAmount", (short)this.healingAmount);
        tag.setShort("DeathTime", (short)this.deathTimer);
        tag.setByte("Skin", this.type);
        tag.setInteger("Tier", this.tier);
        tag.setString("CustomName", this.customName);
        if (!this.activeEffects.isEmpty()) {
            ListTag<CompoundTag> tagList = new ListTag<>();

            for (EffectData effect : this.activeEffects.values()) {
                CompoundTag effectTag = new CompoundTag();
                effectTag.setByte("Id", (byte)effect.getPotionID());
                effectTag.setByte("Amplifier", (byte)effect.getPotionAmplifier());
                effectTag.setInteger("Duration", effect.getPotionDuration());
                effectTag.setBoolean("Special", effect.isSpecial);
                tagList.add(effectTag);
            }

            tag.setTag("ActiveEffects", tagList);
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.health = tag.getShort("Health");
        if (!tag.hasKey("Health")) {
            this.health = 10;
        }

        this.maxHealth = tag.getShort("MaxHealth");
        if (!tag.hasKey("MaxHealth")) {
            this.maxHealth = 20;
        }

        this.healingAmount = tag.getShort("HealAmount");
        this.hurtTime = tag.getShort("HurtTime");
        this.deathTimer = tag.getShort("DeathTime");
        this.customName = tag.getString("CustomName");
        this.tier = tag.getInteger("Tier");
        this.type = tag.getByte("Skin");
        this.setType();
        if (tag.hasKey("ActiveEffects")) {
            ListTag<CompoundTag> tagList = tag.getTagList("ActiveEffects");

            for (int index = 0; index < tagList.size(); index++) {
                CompoundTag effectTag = tagList.get(index);
                byte id = effectTag.getByte("Id");
                byte amp = effectTag.getByte("Amplifier");
                int dur = effectTag.getInteger("Duration");
                boolean bool = effectTag.getBoolean("Special");
                this.activeEffects.put(Integer.valueOf(id), new EffectData(id, dur, amp, bool));
            }
        }
    }

    @Override
    public boolean isAlive() {
        return !this.isDead && this.health > 0;
    }

    public void mobTick() {
        if (this.hasEffect(EffectList.fireResistance)) {
            this.fire = 0;
        }

        if (this.world.isDaytime() && this.world.badSun) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F
                && this.world.isBlockExposed(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                this.fire = 300;
            }
        }

        int arrows = this.getArrows();
        if (arrows > 0) {
            if (this.arrowHitTimer <= 0) {
                this.arrowHitTimer = 20 * (30 - arrows);
            }

            this.arrowHitTimer--;
            if (this.arrowHitTimer <= 0) {
                this.setArrows(arrows - 1);
            }
        }

        if (this.rotIncrements > 0) {
            double x = this.posX + (this.newX - this.posX) / this.rotIncrements;
            double y = this.posY + (this.newY - this.posY) / this.rotIncrements;
            double z = this.posZ + (this.newZ - this.posZ) / this.rotIncrements;
            double yaw = this.newYaw - this.yaw;

            while (yaw < -180.0) {
                yaw += 360.0;
            }

            while (yaw >= 180.0) {
                yaw -= 360.0;
            }

            this.yaw = (float)(this.yaw + yaw / this.rotIncrements);
            this.pitch = (float)(this.pitch + (this.newPitch - this.pitch) / this.rotIncrements);
            this.rotIncrements--;
            this.setPosition(x, y, z);
            this.setRotation(this.yaw, this.pitch);
            List<BoundingBox> bbList = this.world.getBoundingBoxes(this, this.boundingBox.shrink(0.03125, 0.0, 0.03125));
            if (bbList.size() > 0) {
                double yOffset = 0.0;

                for (int index = 0; index < bbList.size(); index++) {
                    BoundingBox bb = bbList.get(index);
                    if (bb.maxY > yOffset) {
                        yOffset = bb.maxY;
                    }
                }

                y += yOffset - this.boundingBox.minY;
                this.setPosition(x, y, z);
            }
        }

        Profiler.startSection("ai");
        if (this.health <= 0 || this.world.aiDisabled() && !(this instanceof Player) || this.freeze > 0) {
            this.jumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
        } else if (!this.isMultiplayer && !this.behaviors.tasks.isEmpty()) {
            Profiler.startSection("aiTasks");
            this.updateAI();
            Profiler.endSection();
        }

        Profiler.endSection();
        if (this.jumping) {
            if (this.isInWater() || this.isInLava() || this.isInMaterial(MaterialList.rope)) {
                this.motionY += 0.04F;
            } else if (this.onGround) {
                this.jump();
            }
        }

        float oldSpeed = this.landSpeed;
        this.landSpeed = this.landSpeed * this.applySpeedModifier();
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.landSpeed = oldSpeed;
        Profiler.startSection("push");
        List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
        if (entities != null && entities.size() > 0) {
            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                if (entity.isPushable() && entity.rider != this) {
                    entity.applyCollision(this);
                }
            }
        }

        if (this.health > 0) {
            entities = this.world.getEntities(this, this.boundingBox.expand(1.0, 0.0, 1.0));
            if (entities != null) {
                for (int entity = 0; entity < entities.size(); entity++) {
                    entities.get(entity).onCollideWith(this);
                }
            }
        }

        Profiler.endSection();
    }

    protected void updateAI() {
        Profiler.startSection("sensing");
        this.senses.clearSensingCache();
        Profiler.endSection();
        Profiler.startSection("targetSelector");
        this.detections.onUpdateTasks();
        Profiler.endSection();
        if (!this.boss || this.attackTarget != null) {
            Profiler.startSection("goalSelector");
            this.behaviors.onUpdateTasks();
            Profiler.endSection();
            Profiler.startSection("navigation");
            this.navigator.tick();
            Profiler.endSection();
            Profiler.startSection("controls");
            this.moveControl.tick();
            this.lookControl.tick();
            this.jumpControl.jump();
            Profiler.endSection();
        }
    }

    @Override
    public int maxPathFallDist() {
        if (this.attackTarget == null) {
            return 3;
        }

        int var1 = (int)(this.health - this.maxHealth * 0.33F);
        var1 -= (3 - this.world.settings.getDifficulty()) * 4;
        if (var1 < 0) {
            var1 = 0;
        }

        return var1 + 3;
    }

    public int getFaceSpeed() {
        return 40;
    }

    public boolean canDespawn() {
        return this.customName.isEmpty() && !this.boss;
    }

    public void despawn() {
        if (this.world.isOnline) {
            return;
        }

        Entity entity = this.world.getClosestPlayer(this, -1.0);
        if (entity != null && this.canDespawn()) {
            double distX = entity.posX - this.posX;
            double distY = entity.posY - this.posY;
            double distZ = entity.posZ - this.posZ;
            double distTotal = distX * distX + distY * distY + distZ * distZ;
            if (distTotal > 16384.0) {
                this.setDead();
            }

            if (this.age > 600 && this.rand.nextInt(800) == 0) {
                if (distTotal < 1024.0) {
                    this.age = 0;
                } else {
                    this.setDead();
                }
            }
        }
    }

    public int getMaxSpawns() {
        return 4;
    }

    public void jump() {
        this.motionY = 0.42F;
        if (this.hasEffect(EffectList.jump)) {
            this.motionY = 0.42F * (1.0F + (this.getEffect(EffectList.jump).getPotionAmplifier() + 1) * 0.82F);
        }

        if (this.isSprinting()) {
            float f = this.yaw * 0.01745329F;
            this.motionX = this.motionX - MathHelper.sin(f) * 0.2F / 2.0F;
            this.motionZ = this.motionZ + MathHelper.cos(f) * 0.2F / 2.0F;
        }
    }

    public void breakItem(ItemStack stack) {
        for (int pcs = 0; pcs < 16; pcs++) {
            Vector vector1 = new Vector((this.rand.nextFloat() - 0.5F) * 0.1F, (float)(Math.random() * 0.1F + 0.1F), 0.0);
            vector1.rotateAroundX(-this.pitch * (float) Math.PI / 180.0F);
            vector1.rotateAroundY(-this.yaw * (float) Math.PI / 180.0F);
            Vector vector2 = new Vector((this.rand.nextFloat() - 1.5F) * 0.3F, -this.rand.nextFloat() * 0.6F - 0.3F, 0.6F);
            vector2.rotateAroundX(-this.pitch * (float) Math.PI / 180.0F);
            vector2.rotateAroundY(-this.yaw * (float) Math.PI / 180.0F);
            vector2 = vector2.addVector(this.posX, this.posY, this.posZ);
            this.world
                .spawnParticle(
                    ParticleContext.setIconContext(ParticleType.ICONCRACK, stack.getItem()),
                    vector2.xCoord,
                    vector2.yCoord,
                    vector2.zCoord,
                    vector1.xCoord,
                    vector1.yCoord + 0.05F,
                    vector1.zCoord
                );
        }

        this.world.playEntitySound(this, "random.break", 1.0F, 1.0F);
    }

    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox)
            && this.getPathWeight(x, y, z) >= 0.0F;
    }

    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return 0.0F;
    }

    @Override
    protected void kill() {
        this.damageEntity(DamageSource.outOfWorld, 4);
    }

    public float getSwing(float val) {
        float swing = this.swing - this.prevSwing;
        if (swing < 0.0F) {
            swing++;
        }

        return this.prevSwing + swing * val;
    }

    public float getSwingOffhand(float val) {
        float swing = this.swingOffhand - this.prevSwingOffhand;
        if (swing < 0.0F) {
            swing++;
        }

        return this.prevSwingOffhand + swing * val;
    }

    @Override
    public void hurtAnim() {
        this.hurtTime = this.hurtDuration = 10;
        this.attackAngle = 0.0F;
    }

    @Override
    public void updateState(byte state) {
        if (state == 2) {
            this.limbYaw = 1.5F;
            this.damage = this.invulnerabilityFrames;
            this.hurtTime = this.hurtDuration = 10;
            this.attackAngle = 0.0F;
            this.world.playEntitySound(this, this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.damageEntity(DamageSource.generic, 0);
        } else if (state == 3) {
            this.world.playEntitySound(this, this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.health = 0;
            this.onDeath(DamageSource.generic);
        } else {
            super.updateState(state);
        }
    }

    public boolean isVisible(Entity entity) {
        return this.world
                .rayTraceBlocks(
                    Vector.createVector(this.posX, this.posY + this.getEyeHeight(), this.posZ),
                    Vector.createVector(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
                )
            == null;
    }

    public Icon getItemIcon(ItemStack item, int dmg) {
        return item.getIconIndex() == null ? GrapplingHook.hook : item.getIconIndex();
    }

    @Override
    public int getSpawnID() {
        return 8;
    }

    public boolean canSpawnNextToBed() {
        return this.canSpawnHere();
    }

    protected void tickEffects() {
        Iterator<Integer> effectsList = this.activeEffects.keySet().iterator();

        try {
            while (effectsList.hasNext()) {
                Integer index = effectsList.next();
                EffectData effect = this.activeEffects.get(index);
                if (!effect.onUpdate(this) && !this.world.isOnline) {
                    effectsList.remove();
                    this.onEffectRemoved(effect);
                }
            }
        } catch (Exception var9) {
        }

        if (this.updateEffects) {
            if (!this.world.isOnline) {
                if (!this.activeEffects.isEmpty()) {
                    int id = BrewingSystem.updatePotions(this.activeEffects.values());
                    this.dataSync.updateObject(8, id);
                } else {
                    this.dataSync.updateObject(8, 0);
                }
            }

            this.updateEffects = false;
        }

        if (this.rand.nextBoolean()) {
            int id = this.dataSync.getObjectInt(8);
            if (id > 0) {
                double red = (id >> 16 & 0xFF) / 255.0;
                double green = (id >> 8 & 0xFF) / 255.0;
                double blue = (id >> 0 & 0xFF) / 255.0;
                this.world
                    .spawnParticle(
                        ParticleType.SPELL,
                        this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                        this.posY + this.rand.nextDouble() * this.height - this.yOffset,
                        this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                        red,
                        green,
                        blue
                    );
            }
        }
    }

    public void clearEffects() {
        Iterator<Integer> effects = this.activeEffects.keySet().iterator();

        while (effects.hasNext()) {
            Integer index = effects.next();
            EffectData effect = this.activeEffects.get(index);
            if (!this.world.isOnline) {
                effects.remove();
                this.onEffectRemoved(effect);
            }
        }
    }

    public Collection<EffectData> getEffects() {
        return this.activeEffects.values();
    }

    public boolean hasEffect(Effect potion) {
        return this.activeEffects.containsKey(potion.id);
    }

    public EffectData getEffect(Effect potion) {
        return this.activeEffects.get(potion.id);
    }

    public void addEffect(EffectData effect) {
        if (this.activeEffects.containsKey(effect.getPotionID())) {
            this.activeEffects.get(effect.getPotionID()).combine(effect);
            this.onEffectChanged(this.activeEffects.get(effect.getPotionID()));
        } else {
            this.activeEffects.put(effect.getPotionID(), effect);
            this.onEffectAdded(effect);
        }
    }

    public void removeEffect(int index) {
        this.activeEffects.remove(index);
    }

    protected void onEffectAdded(EffectData potionEffect1) {
        this.updateEffects = true;
    }

    protected void onEffectChanged(EffectData potionEffect1) {
        this.updateEffects = true;
    }

    protected void onEffectRemoved(EffectData potionEffect1) {
        this.updateEffects = true;
    }

    protected float applySpeedModifier() {
        float spd = 1.0F;
        if (this.hasEffect(EffectList.swiftness)) {
            spd *= 1.0F + 0.2F * (this.getEffect(EffectList.swiftness).getPotionAmplifier() + 1);
        }

        if (this.hasEffect(EffectList.slowness)) {
            spd *= 1.0F - 0.15F * (this.getEffect(EffectList.slowness).getPotionAmplifier() + 1);
        }

        return spd;
    }

    protected int applyDamageModifier(DamageSource source, int amount) {
        if (this.hasEffect(EffectList.resistance)) {
            int amplifier = (this.getEffect(EffectList.resistance).getPotionAmplifier() + 1) * 10;
            int val = 25 - amplifier;
            int total = amount * val + this.carryoverDamage;
            amount = total / 25;
            this.carryoverDamage = total % 25;
        }

        if (this.hasEffect(EffectList.fragility)) {
            int amplifier = (this.getEffect(EffectList.fragility).getPotionAmplifier() + 1) / 10;
            int val = 25 + amplifier;
            int total = amount * val + this.carryoverDamage;
            amount = total / 15;
            this.carryoverDamage = total % 15;
        }

        return amount;
    }

    @Override
    public void setDead() {
        super.setDead();
        if (this.wasLit) {
            this.world.restoreBlockEmittance(this.prevLitX, this.prevLitY, this.prevLitZ);
        }
    }

    public void onDeath() {
        this.deathTimer++;
        if (this.deathTimer > 20) {
            this.setDead();
            this.spawnPuffParticles();
        }
    }

    public boolean withinTrackingDist() {
        return this.withinTrackingDist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    public boolean withinTrackingDist(int x, int y, int z) {
        return this.maxHomeDist == -1.0F ? true : this.homePos.getDistanceSq(x, y, z) < this.maxHomeDist * this.maxHomeDist;
    }

    public void setHomePos(int x, int y, int z, int dist) {
        this.homePos.set(x, y, z);
        this.maxHomeDist = dist;
    }

    public Position getHomePosition() {
        return this.homePos;
    }

    public float getMaxTrackDist() {
        return this.maxHomeDist;
    }

    public void stopTracking() {
        this.maxHomeDist = -1.0F;
    }

    public boolean hasTarget() {
        return this.maxHomeDist != -1.0F;
    }

    public boolean canBeAggroed(Class<?> c) {
        return Creeper.class != c && Ghast.class != c && Monkey.class != c && Seraphim.class != c && Termite.class != c;
    }

    public Mob getLastTarget() {
        return this.lastTarget;
    }

    public boolean isRideable() {
        return false;
    }

    public Vector getPosition(float tick) {
        if (tick == 1.0F) {
            return Vector.createVector(this.posX, this.posY, this.posZ);
        }

        double x = this.prevX + (this.posX - this.prevX) * tick;
        double y = this.prevY + (this.posY - this.prevY) * tick;
        double z = this.prevZ + (this.posZ - this.prevZ) * tick;
        return Vector.createVector(x, y, z);
    }

    public TargetingSystem getSelectedBlock(double dist, float tick) {
        Vector position = this.getPosition(tick);
        Vector rotation = this.getLookVector(tick);
        Vector finalVec = position.addVector(rotation.xCoord * dist, rotation.yCoord * dist, rotation.zCoord * dist);
        return this.world.rayTraceBlocks(position, finalVec);
    }

    @Override
    public float getFacingAngle() {
        return this.headFacing;
    }

    @Override
    public boolean alwaysTick() {
        return this.boss;
    }

    public final int getArrows() {
        return this.arrowCount;
    }

    public final void setArrows(int count) {
        this.arrowCount = count;
    }
}
