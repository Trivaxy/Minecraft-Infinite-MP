package net.minecraft.game.entity.player;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.game.ParticleContext;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.block.tileentity.container.GraveEntity;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.block.tileentity.smelters.HellforgeEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.GlobalInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.boat.LavaBoat;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.mounts.Camel;
import net.minecraft.game.entity.mob.animal.mounts.Pig;
import net.minecraft.game.entity.mob.animal.mounts.horse.Horse;
import net.minecraft.game.entity.mob.animal.mounts.horse.Pegasus;
import net.minecraft.game.entity.mob.animal.mounts.horse.SkeletonHorse;
import net.minecraft.game.entity.mob.animal.mounts.horse.ZombieHorse;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.monster.Ghast;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.mob.monster.Phantom;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.entity.projectile.Hook;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.staff.GabrielStaff;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import net.minecraft.game.item.tools.GrapplingHook;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.generation.features.general.StarterHouse;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.game.world.util.Position;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Player extends Mob {
    public PlayerInventory inventory = new PlayerInventory(this);
    public GlobalInventory inventoryChest = new GlobalInventory(this);
    public Container container;
    public Container openContainer;
    public ItemStack itemInUse;
    public FoodData foodStats = new FoodData();
    public PlayerProperties properties = new PlayerProperties();
    public String username;
    private Position bedPosition;
    private Position playerSpawn;
    private Position hellSpawn;
    private Position skySpawn;
    private Position lastDeathPos;
    private Position minecartStart;
    public Map<Integer, Position[]> teleporterPositionsIn = new HashMap<>();
    public Map<Integer, Position[]> teleporterPositionsOut = new HashMap<>();
    public Bobber bobber = null;
    public Hook hook = null;
    public int score = 0;
    private int sleepTimer;
    private int timeWithoutRest;
    private int damageRemainder = 0;
    public int dimension;
    public int itemAnimFrames;
    public int stamina;
    public int maxStamina;
    public int itemUseCooldown = 0;
    public int maxDelay = 0;
    private int ticksInAir;
    private int hasJumped;
    public int swingProgress = 0;
    public int swingProgressOffhand = 0;
    public float prevCamYaw;
    public float camYaw;
    public float prevCamPitch;
    public float camPitch;
    public float modelRotX;
    public float modelRotY;
    public float modelRotZ;
    public float prevLandSpeed;
    public float prevJumpSpeed;
    public float nauseaTime;
    public float prevNauseaTime;
    public float rotateElytraX;
    public float rotateElytraY;
    public float rotateElytraZ;
    public double capeX;
    public double capeY;
    public double capeZ;
    public double shiftX;
    public double shiftY;
    public double shiftZ;
    public boolean swinging = false;
    public boolean swingingOffhand = false;
    public boolean op = false;
    public boolean isSlim = false;
    public boolean foundSkin = false;
    public boolean dropItems = true;
    public boolean forceSpawn = false;

    public Player(World world) {
        super(world);
        this.container = new PlayerContainer(this.inventory, this);
        this.openContainer = this.container;
        Position spawn = world.getSpawnPoint();
        this.teleportTo(spawn.x + 0.5, spawn.y, spawn.z + 0.5, 0.0F, 0.0F);
        this.yOffset = 1.62F;
        this.setHealth(20);
        this.maxStamina = 200;
        this.stamina = this.maxStamina;
        this.texture = "/mob/player/char.png";
        this.prevLandSpeed = this.landSpeed;
        this.prevJumpSpeed = this.jumpSpeed;

        for (int i = 0; i < 4; i++) {
            this.teleporterPositionsIn.put(i, new Position[]{null, null, null, null, null, null});
            this.teleporterPositionsOut.put(i, new Position[]{null, null, null, null, null, null});
        }
    }

    @Override
    public double getYOffset() {
        return this.yOffset - 0.5;
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)0);
    }

    public void spawn(boolean triggerOnce) {
        this.yOffset = 1.62F;
        this.setSize(0.6F, 1.8F);
        if (this.world != null) {
            while (true) {
                if (this.posY > 0.0) {
                    this.setPosition(this.posX, this.posY, this.posZ);
                    if (this.world.getBoundingBoxes(this, this.boundingBox).size() != 0) {
                        this.posY++;
                        continue;
                    }
                }

                this.motionX = this.motionY = this.motionZ = 0.0;
                this.pitch = 0.0F;
                break;
            }
        }

        this.health = this.maxHealth = 20;
        this.deathTimer = 0;
        if (triggerOnce) {
            if (this.properties.gamemode == 2) {
                this.inventory.setSlotContents(0, new ItemStack(ItemList.swordSteel, 1));
                this.inventory.setSlotContents(1, new ItemStack(ItemList.bow, 1));
                this.inventory.setSlotContents(2, new ItemStack(ItemList.arrowFlint, 64));
                this.inventory.setSlotContents(3, new ItemStack(ItemList.striker, 1));
                this.inventory.setSlotContents(4, new ItemStack(ItemList.appleRed, 64));
                this.inventory.setSlotContents(7, new ItemStack(BlockList.torch, 64));
                this.inventory.setSlotContents(8, new ItemStack(BlockList.tnt, 10));
                this.inventory.setSlotContents(9, new ItemStack(ItemList.helmetLeather, 1));
                this.inventory.setSlotContents(10, new ItemStack(ItemList.plateLeather, 1));
                this.inventory.setSlotContents(11, new ItemStack(ItemList.legsLeather, 1));
                this.inventory.setSlotContents(12, new ItemStack(ItemList.bootsLeather, 1));
            }

            if (this.world.settings.bonusHouse) {
                StarterHouse.INSTANCE
                    .place(
                        this.world,
                        this.world.rand,
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY - 1.0),
                        MathHelper.floor_double(this.posZ)
                    );
            }
        }
    }

    public void spawnInDimension(int dim) {
        this.dimension = dim;
        this.fallDistance = 0.0F;
    }

    @Override
    public void onTick() {
        if (this.itemInUse != null) {
            ItemStack heldItem = this.inventory.getCurrentItem();
            if (this.inventory.getOffHandItem() != null && this.inventory.getOffHandItem() == this.itemInUse) {
                heldItem = this.inventory.getOffHandItem();
            }

            if (heldItem != this.itemInUse) {
                this.clearItemInUse();
            } else {
                if (this.itemAnimFrames <= 25 && this.itemAnimFrames % 4 == 0) {
                    this.consumeFood(heldItem, 5);
                }

                if (--this.itemAnimFrames == 0 && !this.world.isOnline) {
                    this.useItem();
                }
            }
        }

        if (this.isPlayerSleeping()) {
            this.sleepTimer++;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.isInBed()) {
                this.wakeUpPlayer(true, true, false);
            } else if (!this.world.isOnline && this.awaken()) {
                this.wakeUpPlayer(false, true, true);
            }
        } else if (this.sleepTimer > 0) {
            this.sleepTimer++;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        if (this.timeWithoutRest > 0) {
            this.timeWithoutRest--;
        }

        if (this.itemInUse != null && this.itemInUse.getItemUseAction() == Item.Action.joust && this.hurtTime == 0) {
            this.joust();
        }

        super.onTick();
        if (!this.world.isOnline && this.openContainer != null && !this.openContainer.canUse(this)) {
            this.closeScreen();
            this.openContainer = this.container;
        }

        this.capeX = this.shiftX;
        this.capeY = this.shiftY;
        this.capeZ = this.shiftZ;
        double animX = this.posX - this.shiftX;
        double animY = this.posY - this.shiftY;
        double animZ = this.posZ - this.shiftZ;
        double max = 10.0;
        if (animX > max) {
            this.capeX = this.shiftX = this.posX;
        }

        if (animZ > max) {
            this.capeZ = this.shiftZ = this.posZ;
        }

        if (animY > max) {
            this.capeY = this.shiftY = this.posY;
        }

        if (animX < -max) {
            this.capeX = this.shiftX = this.posX;
        }

        if (animZ < -max) {
            this.capeZ = this.shiftZ = this.posZ;
        }

        if (animY < -max) {
            this.capeY = this.shiftY = this.posY;
        }

        this.shiftX += animX * 0.25;
        this.shiftZ += animZ * 0.25;
        this.shiftY += animY * 0.25;
        if (this.itemUseCooldown > 0) {
            this.itemUseCooldown--;
        }

        if (!this.world.isOnline && this.properties.getHunger()) {
            this.foodStats.onUpdate(this);
        }

        if (this.jumping
            && this.itemUseCooldown == 0
            && !this.onGround
            && !this.flying
            && this.inventory.accessorySlots[3] != null
            && this.inventory.accessorySlots[3].getItem() == ItemList.windAmulet
            && this.hasJumped < 3) {
            this.jump();
            this.itemUseCooldown = 20;
            this.fallDistance = 0.0F;
            this.hasJumped++;

            for (int i = 0; i < 20; i++) {
                this.world
                    .spawnParticle(
                        ParticleType.EXPLODE,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY - 2.0,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.rand.nextFloat() * 0.02F,
                        this.rand.nextFloat() * 0.02F,
                        this.rand.nextFloat() * 0.02F
                    );
            }
        }

        if (this.isGliding()) {
            this.ticksInAir++;
        } else {
            this.ticksInAir = 0;
        }

        this.addStat(StatList.minutesPlayedStat, 1);
    }

    @Override
    public void moveEntityWithHeading(float movX, float movZ) {
        double x = this.posX;
        double y = this.posY;
        double z = this.posZ;
        if (!this.isInWater() && !this.isInLava() && this.isGliding()) {
            this.moveGliding();
        } else {
            super.moveEntityWithHeading(movX, movZ);
        }

        this.addMovementStat(this.posX - x, this.posY - y, this.posZ - z);
    }

    private void moveGliding() {
        if (this.motionY > -0.5) {
            this.fallDistance = 1.0F;
        }

        Vector lookVector = this.getLookVector(1.0F);
        float facing = this.pitch * (float) (Math.PI / 180.0);
        double dist = Math.sqrt(lookVector.xCoord * lookVector.xCoord + lookVector.zCoord * lookVector.zCoord);
        double velocity = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double totalDist = lookVector.lengthVector();
        float angle = MathHelper.cos(facing);
        angle = (float)((double)angle * angle * Math.min(1.0, totalDist / 0.4));
        this.motionY += -0.08 + angle * 0.06;
        if (this.motionY < 0.0 && dist > 0.0) {
            double d2 = this.motionY * -0.1 * angle;
            this.motionY += d2;
            this.motionX = this.motionX + lookVector.xCoord * d2 / dist;
            this.motionZ = this.motionZ + lookVector.zCoord * d2 / dist;
        }

        if (facing < 0.0F) {
            double d10 = velocity * -MathHelper.sin(facing) * 0.04;
            this.motionY += d10 * 3.2;
            this.motionX = this.motionX - lookVector.xCoord * d10 / dist;
            this.motionZ = this.motionZ - lookVector.zCoord * d10 / dist;
        }

        if (dist > 0.0) {
            this.motionX = this.motionX + (lookVector.xCoord / dist * velocity - this.motionX) * 0.1;
            this.motionZ = this.motionZ + (lookVector.zCoord / dist * velocity - this.motionZ) * 0.1;
        }

        this.motionX *= 0.99F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.99F;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.collidedHorizontally && !this.world.isOnline) {
            double distTravelled = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            double totalSpeedTravelled = velocity - distTravelled;
            float force = (float)(totalSpeedTravelled * 10.0 - 3.0);
            if (force > 0.0F) {
                this.damageEntity(DamageSource.fall, MathHelper.floor_double(force));
            }
        }

        if (this.onGround && !this.world.isOnline) {
            this.setFlag(7, false);
        }

        this.prevLimbYaw = this.limbYaw;
        double x = this.posX - this.prevX;
        double z = this.posZ - this.prevZ;
        float animSpeed = MathHelper.sqrt_double(x * x + z * z) * 4.0F;
        if (animSpeed > 1.0F) {
            animSpeed = 1.0F;
        }

        this.limbYaw = this.limbYaw + (animSpeed - this.limbYaw) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbYaw;
    }

    @Override
    public boolean isGliding() {
        return this.getFlag(7);
    }

    public void setGliding() {
        this.setFlag(7, true);
    }

    public void clearGliding() {
        this.setFlag(7, false);
    }

    public int getTicksElytraFlying() {
        return this.ticksInAir;
    }

    private void updateElytra() {
        boolean flag = this.getFlag(7);
        if (flag && !this.onGround && !this.isRiding()) {
            ItemStack itemstack = this.inventory.accessorySlots[2];
            if (itemstack == null) {
                flag = false;
            } else if (itemstack.getItem() == ItemList.elytra) {
                flag = true;
                if (!this.world.isOnline && (this.ticksInAir + 1) % 20 == 0) {
                    itemstack.damageItem(1, this);
                }

                if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
                    this.clearGliding();
                    flag = false;
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.world.isOnline) {
            this.setFlag(7, flag);
        }
    }

    private void joust() {
        double speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (this.isRiding() && this.mount != null) {
            speed = MathHelper.sqrt_double(this.mount.motionX * this.mount.motionX + this.mount.motionZ * this.mount.motionZ);
        }

        double blocksPerSecond = speed * 20.0;
        BoundingBox attackBox = this.boundingBox.expand(1.5, 0.0, 1.5);
        List<Entity> hitEntities = this.world.getEntities(this, attackBox);

        for (int i = 0; i < hitEntities.size(); i++) {
            Entity e = hitEntities.get(i);
            if (e != this && e != this.mount && e.visible && e.isSelectable() && speed > 0.15) {
                float baseDamage = this.getAtkDamage(e);
                double speedMultiplier = Math.min(5.0, 1.0 + blocksPerSecond / 4.0);
                int damageMultiplier = 0;
                boolean isLiving = e instanceof Mob;
                if (isLiving) {
                    if (!((Mob)e).isVisible(this)) {
                        break;
                    }

                    damageMultiplier = EnchantmentHelper.getAtkModifier(this.inventory, (Mob)e);
                    baseDamage += damageMultiplier;
                }

                if (this.hasEffect(EffectList.strength)) {
                    baseDamage += 3 << this.getEffect(EffectList.strength).getPotionAmplifier();
                }

                if (this.hasEffect(EffectList.weakness)) {
                    baseDamage -= 2 << this.getEffect(EffectList.weakness).getPotionAmplifier();
                }

                float totalDamage = (float)(baseDamage * speedMultiplier);
                if (e.damageEntity(DamageSource.mobDamage(this), (int)totalDamage)) {
                    this.itemInUse.damageItem(2, this);
                    if (this.itemInUse.stackSize <= 0) {
                        this.itemInUse = null;
                    }

                    this.spawnCritParticles(e, ParticleType.DAMAGE, (int)totalDamage / 2);
                    if (damageMultiplier > 0) {
                        this.spawnCritParticles(e, ParticleType.MAGIC, 16);
                    }
                }
            }
        }
    }

    @Override
    public float getStepHeight() {
        return this.inventory.accessorySlots[2] != null && this.inventory.accessorySlots[2].getItem() == ItemList.capeAgility ? 1.0F : 0.5F;
    }

    @Override
    public boolean canBeSlowed() {
        return this.inventory.accessorySlots[2] == null || this.inventory.accessorySlots[2].getItem() != ItemList.capeAgility;
    }

    @Override
    public int isLit() {
        ItemStack heldItem = this.inventory.getCurrentItem();
        if (this.inventory.getOffHandItem() != null
            && (heldItem == null || heldItem != null && (heldItem.getItem().id >= 4096 || Block.lightValue[heldItem.getItem().id] == 0))) {
            heldItem = this.inventory.getOffHandItem();
        }

        if (heldItem != null && this.health > 0) {
            if (heldItem.getItem() instanceof BlockItem) {
                BlockItem block = (BlockItem)heldItem.getItem();
                return Block.lightValue[block.id];
            }

            if (heldItem.getItem() == ItemList.glowStick) {
                return 15;
            }
        }

        return 0;
    }

    public void addStat(Stat stat, int value) {
    }

    public void removeStat(Stat stat) {
    }

    public void swingItem() {
        this.swingProgress = -1;
        this.swinging = true;
    }

    public void swingOffhandItem() {
        this.swingProgressOffhand = -1;
        this.swingingOffhand = true;
    }

    public void resetKeys() {
    }

    protected void handleHealthUpdates() {
        if (this.world.settings.getDifficulty() == 0 && this.health < this.maxHealth && this.ticksExisted % 20 * 4 == 0) {
            this.heal(1);
        }

        if (this.health < this.maxHealth
            && this.inventory.accessorySlots[0] != null
            && this.inventory.accessorySlots[0].getItem() == ItemList.ringRegen
            && this.inventory.accessorySlots[0].isNotBroken()
            && this.ticksExisted % 100 * 4 == 0) {
            this.heal(1);
            this.inventory.accessorySlots[0].damageItem(1, this);
        }

        if (this.health < this.maxHealth && this.dimension == 2 && this.isInWater() && this.ticksExisted % 100 * 4 == 0) {
            this.triggerAchievement(AchievementList.water);
        }

        if (this.healingAmount > 0 && this.health < this.maxHealth && this.ticksExisted % 5 * this.healingAmount == 0) {
            this.healingAmount--;
            this.heal(1);
        }

        if (this.health > this.maxHealth && !this.hasEffect(EffectList.absorption)) {
            this.health = this.maxHealth;
        }

        if (this.health >= this.maxHealth) {
            this.healingAmount = 0;
        }
    }

    @Override
    public void mobTick() {
        this.handleHealthUpdates();
        this.updateElytra();
        this.inventory.tick();
        this.prevCamYaw = this.camYaw;
        this.prevCamPitch = this.camPitch;
        this.headFacing = this.yaw;
        super.mobTick();
        this.landSpeed = this.prevLandSpeed;
        this.jumpSpeed = this.prevJumpSpeed;
        if (this.isSprinting()) {
            this.landSpeed = this.landSpeed + this.prevLandSpeed * 0.3F;
            this.jumpSpeed = this.jumpSpeed + this.prevJumpSpeed * 0.3F;
        }

        if (this.properties.gamemode == 2) {
            this.landSpeed = this.landSpeed + this.prevLandSpeed * this.properties.SPD / 30.0F;
            this.jumpSpeed = this.jumpSpeed + this.prevJumpSpeed * this.properties.SPD / 30.0F;
        }

        float velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float fallDist = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
        if (velocity > 0.1F) {
            velocity = 0.1F;
        }

        if (!this.onGround || this.health <= 0) {
            velocity = 0.0F;
        }

        if (this.onGround || this.health <= 0) {
            fallDist = 0.0F;
        }

        this.camYaw = this.camYaw + (velocity - this.camYaw) * 0.4F;
        this.camPitch = this.camPitch + (fallDist - this.camPitch) * 0.8F;
        if (this.canBreatheUnderwater() && this.isInWater()) {
            this.jumpSpeed = this.jumpSpeed + this.prevJumpSpeed * 0.5F;
        }

        if (this.inventory.armorSlots[0] != null
            && this.inventory.armorSlots[1] != null
            && this.inventory.armorSlots[2] != null
            && this.inventory.armorSlots[3] != null) {
            this.triggerAchievement(AchievementList.suitUp);
            if (this.inventory.hasSetEquipped(ArmorMaterial.SAPPHIRE)) {
                this.freeze = 0;
            }

            if (this.inventory.hasSetEquipped(ArmorMaterial.TOPAZ) && !this.flying && !this.isInWater() && !this.isInLava() && !this.isGliding()) {
                if (!this.onGround && this.jumping) {
                    this.motionY = (float)(this.motionY + 0.07);
                    this.gliding = true;

                    for (int i = 0; i < 20; i++) {
                        this.world
                            .spawnParticle(
                                ParticleType.EXPLODE,
                                this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                                this.posY - 2.0,
                                this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                                this.rand.nextFloat() * 0.02F,
                                this.rand.nextFloat() * 0.02F,
                                this.rand.nextFloat() * 0.02F
                            );
                    }
                } else {
                    this.motionY = (float)(this.motionY + 0.02);
                    this.gliding = false;
                }

                if (this.jumping) {
                    this.motionY *= 0.9F;
                }

                this.fallDistance /= 2.0F;
            }
        }

        if (this.isInWater()
            && this.inventory.accessorySlots[3] != null
            && this.inventory.accessorySlots[3].getItem() == ItemList.necklace
            && this.inventory.accessorySlots[3].isNotBroken()) {
            this.addEffect(new EffectData(EffectList.resistance.id, 100, 1));
            this.addEffect(new EffectData(EffectList.strength.id, 100, 1));
        }

        if (this.ticksInAir > 600 && this.isGliding()) {
            this.triggerAchievement(AchievementList.airTime);
        }

        if ((int)((float)this.world.getWorldTime() / this.world.settings.dayLength) >= 100) {
            this.triggerAchievement(AchievementList.days);
        }

        if (this.fallDistance > 50.0F && this.isInCloud()) {
            this.triggerAchievement(AchievementList.cloud);
        }

        this.swingArms();
        this.switchDimension(0, false);
    }

    @Override
    public void rotateBody() {
        double x = this.posX - this.prevX;
        double z = this.posZ - this.prevZ;
        float dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = this.yawOffset;
        if (dist > 0.05F) {
            yaw = (float)Math.atan2(z, x) * 180.0F / (float) Math.PI - 90.0F;
        }

        if (this.swing > 0.0F) {
            yaw = this.yaw;
        }

        float newYaw = yaw - this.yawOffset;

        while (newYaw < -180.0F) {
            newYaw += 360.0F;
        }

        while (newYaw >= 180.0F) {
            newYaw -= 360.0F;
        }

        this.yawOffset += newYaw * 0.1F;
        float yaw2 = this.yaw - this.yawOffset;

        while (yaw2 < -180.0F) {
            yaw2 += 360.0F;
        }

        while (yaw2 >= 180.0F) {
            yaw2 -= 360.0F;
        }

        if (yaw2 < -75.0F) {
            yaw2 = -75.0F;
        }

        if (yaw2 >= 75.0F) {
            yaw2 = 75.0F;
        }

        this.yawOffset = this.yaw - yaw2;
        this.yawOffset += yaw2 * 0.1F;
    }

    private void swingArms() {
        if (this.swinging) {
            this.swingProgress++;
            if (this.swingProgress >= 8) {
                this.swingProgress = 0;
                this.swinging = false;
            }
        } else {
            this.swingProgress = 0;
        }

        if (this.swingingOffhand) {
            this.swingProgressOffhand++;
            if (this.swingProgressOffhand >= 8) {
                this.swingProgressOffhand = 0;
                this.swingingOffhand = false;
            }
        } else {
            this.swingProgressOffhand = 0;
        }

        this.swing = this.swingProgress / 8.0F;
        this.swingOffhand = this.swingProgressOffhand / 8.0F;
    }

    @Override
    public void walkOnBlock() {
        int xCoord = MathHelper.floor_double(this.posX);
        int yCoord = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
        int zCoord = MathHelper.floor_double(this.posZ);
        Material blockMaterial = this.world.getBlockMaterial(xCoord, yCoord, zCoord);
        if ((blockMaterial == MaterialList.water || blockMaterial == MaterialList.lava)
            && this.world.getBlockId(xCoord, yCoord + 1, zCoord) == 0
            && this.inventory.accessorySlots[0] != null
            && this.inventory.accessorySlots[0].getItem() == ItemList.ring) {
            for (int x = xCoord - 2; x < xCoord + 2; x++) {
                for (int z = zCoord - 2; z < zCoord + 2; z++) {
                    if (this.world.getBlockMaterial(x, yCoord, z) == MaterialList.water) {
                        this.world.setBlock(x, yCoord, z, BlockList.ice.id);
                    } else if (this.world.getBlockMaterial(x, yCoord, z) == MaterialList.lava) {
                        this.world.setBlock(x, yCoord, z, BlockList.obsidian.id);
                    }
                }
            }
        }

        if (blockMaterial == MaterialList.quicksoil) {
            this.triggerAchievement(AchievementList.sand);
        }
    }

    public final boolean isInCloud() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY - 1.0);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockId(x, y, z) == BlockList.cloud.id;
    }

    @Override
    public boolean isInWater() {
        return this.crawling ? this.world.handleWaterMovement(this.boundingBox.expand(0.0, 0.0, 0.0), MaterialList.water, this) : super.isInWater();
    }

    @Override
    public boolean isInLava() {
        return this.crawling ? this.world.handleWaterMovement(this.boundingBox.expand(0.0, 0.0, 0.0), MaterialList.lava, this) : super.isInLava();
    }

    @Override
    protected float applySpeedModifier() {
        float factor = 1.0F;
        if (this.hasEffect(EffectList.swiftness)) {
            factor *= 1.0F + 0.2F * (this.getEffect(EffectList.swiftness).getPotionAmplifier() + 1);
        }

        if (this.inventory.accessorySlots[2] != null && this.inventory.accessorySlots[2].getItem() == ItemList.capeAgility) {
            factor *= 1.2F;
        }

        if (this.hasEffect(EffectList.slowness)) {
            factor *= 1.0F - 0.15F * (this.getEffect(EffectList.slowness).getPotionAmplifier() + 1);
        }

        return factor;
    }

    @Override
    public void addEffect(EffectData potionEffect) {
        if (EffectList.effects[potionEffect.getPotionID()].isNegative()
            && this.inventory.accessorySlots[3] != null
            && this.inventory.accessorySlots[3].itemID == ItemList.crown.id
            && this.inventory.accessorySlots[3].isNotBroken()) {
            this.inventory.accessorySlots[3].damageItem(1, this);
        } else {
            super.addEffect(potionEffect);
            int total = 0;

            for (int i = 0; i < EffectList.effects.length; i++) {
                if (EffectList.effects[i] != null && EffectList.effects[i].isMainEffect && this.hasEffect(EffectList.effects[i])) {
                    total++;
                }
            }

            if (total >= 17) {
                this.triggerAchievement(AchievementList.cocktail);
            }
        }
    }

    public void onFoodEaten() {
    }

    public void switchDimension(int i, boolean forced) {
    }

    public void onKillEntity(Mob victim) {
        if (victim instanceof Monster) {
            this.triggerAchievement(AchievementList.killEnemy);
        }

        if (victim.boss) {
            this.triggerAchievement(AchievementList.dungeon);
        }
    }

    @Override
    public void onDeath(DamageSource ds) {
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.lastDeathPos = new Position(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        this.motionY = 0.1F;
        this.openContainer.onClose(this);
        if (this.username.equals("Notch")) {
            this.dropPlayerItem(new ItemStack(ItemList.appleRed, 1), true);
        }

        if (this.username.equals("Yoniko98")) {
            this.dropPlayerItem(new ItemStack(ItemList.appleGold, 1), true);
        }

        if (this.properties.getGrave() && !this.properties.getBabyMode() && this.posY >= 1.0) {
            int facing = MathHelper.floor_double(this.yaw * 4.0F / 360.0F + 0.5) & 3;
            int metadata = 0;
            if (facing == 0) {
                metadata = 2;
            }

            if (facing == 1) {
                metadata = 5;
            }

            if (facing == 2) {
                metadata = 3;
            }

            if (facing == 3) {
                metadata = 4;
            }

            int heightOffset = 1;
            if (this.crawling) {
                heightOffset = 0;
            }

            this.world
                .setBlockAndMetadataWithNotify(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY - heightOffset),
                    MathHelper.floor_double(this.posZ),
                    BlockList.playerGrave.id,
                    metadata
                );

            for (int i = 0; i < this.inventory.getSize() + 1; i++) {
                ItemStack stack = this.inventory.getStackInSlot(i);
                if (stack != null) {
                    stack = this.inventory.getStackInSlot(i).copy();
                }

                GraveEntity grave = (GraveEntity)this.world
                    .getTileEntity(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - heightOffset), MathHelper.floor_double(this.posZ));
                if (grave != null) {
                    grave.setSlotContents(i, stack);
                }
            }
        } else if (!this.properties.getBabyMode()) {
            this.inventory.clearContents(true);
        }

        Entity entity = ds.getEntity();
        if (entity != null) {
            this.motionX = -MathHelper.cos((this.attackAngle + this.yaw) * (float) Math.PI / 180.0F) * 0.1F;
            this.motionZ = -MathHelper.sin((this.attackAngle + this.yaw) * (float) Math.PI / 180.0F) * 0.1F;
        } else {
            this.motionX = this.motionZ = 0.0;
        }

        this.yOffset = 0.1F;
        this.addStat(StatList.deathsStat, 1);
        if (entity != null) {
            if (entity instanceof Mob) {
                if (((Mob)entity).getSpawnID() >= 0) {
                    this.addStat(StatList.deathStats[((Mob)entity).getSpawnID()], 1);
                }
            } else if (entity instanceof Projectile) {
                if (((Projectile)entity).thrower != null && ((Projectile)entity).thrower.getSpawnID() >= 0) {
                    this.addStat(StatList.deathStats[((Projectile)entity).thrower.getSpawnID()], 1);
                }
            } else if (entity instanceof Arrow && ((Arrow)entity).shooter != null && ((Arrow)entity).shooter.getSpawnID() >= 0) {
                this.addStat(StatList.deathStats[((Arrow)entity).shooter.getSpawnID()], 1);
            }
        }
    }

    @Override
    public void addScore(Entity entity, int score) {
        this.score += score;
        this.addStat(StatList.mobKillsStat, 1);
        this.addStat(StatList.mobScore, score);
    }

    public void dropCurrentItem(int size) {
        if (this.inventory.getCurrentItem() != null && this.inventory.getCurrentItem().getItem() == ItemList.grapplingHook && this.hook != null) {
            this.hook.setDead();
            this.hook = null;
        }

        this.dropPlayerItem(this.inventory.decrStackSize(this.inventory.currentSlot, size), false);
    }

    public void dropPlayerItem(ItemStack stack) {
        this.dropPlayerItem(stack, false);
    }

    public void dropPlayerItem(ItemStack stack, boolean moving) {
        if (stack != null) {
            DroppedItem item = new DroppedItem(this.world, this.posX, this.posY - 0.3F, this.posZ, stack);
            item.pickupDelay = 40;
            float offset = 0.1F;
            if (moving) {
                float angle = this.rand.nextFloat() * 0.5F;
                float f6 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                item.motionX = -MathHelper.sin(f6) * angle;
                item.motionZ = MathHelper.cos(f6) * angle;
                item.motionY = 0.2F;
            } else {
                offset = 0.3F;
                item.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
                item.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * offset;
                item.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * offset + 0.1F;
                offset = 0.02F;
                float angle = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                offset *= this.rand.nextFloat();
                item.motionX = item.motionX + Math.cos(angle) * offset;
                item.motionY = item.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                item.motionZ = item.motionZ + Math.sin(angle) * offset;
            }

            this.spawnItem(item);
            this.addStat(StatList.dropStat, stack.stackSize);
            this.addStat(StatList.dropStats[stack.itemID], stack.stackSize);
        }
    }

    protected void spawnItem(DroppedItem entityItem1) {
        this.world.spawnEntity(entityItem1);
    }

    @Override
    public boolean canBurn() {
        if (this.inventory.hasSetEquipped(ArmorMaterial.RUBY) || this.properties.gamemode == 1) {
            return false;
        } else {
            return this.mount != null && this.mount instanceof LavaBoat ? false : super.canBurn();
        }
    }

    public void clone(Player player, boolean keepStats) {
        if (keepStats) {
            this.inventory = player.inventory;
            this.container = player.container;
            this.activeEffects = player.activeEffects;
            this.health = player.health;
            this.foodStats = player.foodStats;
        }

        if (!keepStats && player.inventory != null && player.properties.getBabyMode()) {
            this.inventory = player.inventory;
            this.inventory.player = this;
            this.container = new PlayerContainer(this.inventory, this);
            this.openContainer = this.container;
            // backpack and quiver contents ride in the accessory item's nbt, so they come back with it
            if (player.inventory.accessorySlots[2] != null && player.inventory.accessorySlots[2].hasTag()) {
                ((PlayerContainer)this.container).loadContents(player.inventory.accessorySlots[2]);
            }
        }

        this.inventoryChest = player.inventoryChest;
        this.inventoryChest.player = this;
        this.inventory.player = this;
        this.timeWithoutRest = player.timeWithoutRest;
        this.score = player.score;
        this.dimension = player.dimension;
        this.username = player.username;
        this.maxHealth = player.maxHealth;
        if (player.getSpawn(0) != null) {
            this.setSpawn(player.getSpawn(0), 0, this.forceSpawn);
        }

        if (player.getSpawn(1) != null) {
            this.setSpawn(player.getSpawn(1), 1, this.forceSpawn);
        }

        if (player.getSpawn(2) != null) {
            this.setSpawn(player.getSpawn(2), 2, this.forceSpawn);
        }

        if (player.getDeathCoordinate() != null) {
            this.lastDeathPos = player.lastDeathPos;
        }

        this.setSpawn(player.getSpawn(player.dimension), player.dimension, player.forceSpawn);
        this.teleporterPositionsIn = player.teleporterPositionsIn;
        this.teleporterPositionsOut = player.teleporterPositionsOut;
        this.properties = player.properties;
    }

    public void setProperties(World world) {
        this.properties.setSprint(WorldSettings.sprint);
        this.properties.setHunger(WorldSettings.hunger);
        this.properties.setAttack(WorldSettings.atkCooldown);
        this.properties.setArmor(WorldSettings.armor);
        this.properties.setBabyMode(WorldSettings.keepInventory);
        this.properties.setPermadeath(WorldSettings.permadeath);
        this.properties.setCanSleep(WorldSettings.sleep);
        this.properties.setGrave(WorldSettings.grave);
        this.properties.setDurability(WorldSettings.durability);
    }

    public float getStrVsBlock(Block block) {
        float speed = this.breakSpd(block);
        int modifier = EnchantmentHelper.getEfficiencyModifier(this.inventory);
        if (modifier > 0 && this.applySpeedBoost(block)) {
            speed += modifier * modifier + 1;
        }

        if (this.hasEffect(EffectList.haste)) {
            speed *= 1.0F + (this.getEffect(EffectList.haste).getPotionAmplifier() + 1);
        }

        if (this.hasEffect(EffectList.miningFatigue)) {
            speed *= 1.0F - (this.getEffect(EffectList.miningFatigue).getPotionAmplifier() + 1);
        }

        if (this.isInMaterial(MaterialList.water) && !this.canBreatheUnderwater() || !this.onGround) {
            speed /= 5.0F;
        }

        return speed;
    }

    public float breakSpd(Block block) {
        float spd = 1.0F;
        if (this.getUsing()) {
            if (this.inventory.offhandSlot[0] != null) {
                spd *= this.inventory.offhandSlot[0].getDigSpeed(block);
            }
        } else if (this.inventory.mainInventory[this.inventory.currentSlot] != null) {
            spd *= this.inventory.mainInventory[this.inventory.currentSlot].getDigSpeed(block);
        }

        return spd;
    }

    public boolean applySpeedBoost(Block block) {
        ItemStack stack = this.inventory.getStackInSlot(this.inventory.currentSlot);
        if (this.inventory.player.getUsing()) {
            stack = this.inventory.getStackInSlot(44);
        }

        return stack != null ? stack.canHarvestBlock(block) : false;
    }

    public boolean canHarvestBlock(Block block) {
        if (block.material != MaterialList.rock && block.material != MaterialList.metal) {
            return true;
        }

        ItemStack stack = this.inventory.getStackInSlot(this.inventory.currentSlot);
        if (this.getUsing()) {
            stack = this.inventory.getStackInSlot(44);
        }

        return stack != null ? stack.canHarvestBlock(block) : false;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.forceSpawn = tag.getBoolean("SpawnForced");
        this.sleeping = tag.getBoolean("Sleeping");
        this.sleepTimer = tag.getShort("SleepTimer");
        this.timeWithoutRest = tag.getInteger("TimeWithoutRest");
        this.dimension = tag.getInteger("Dimension");
        if (this.sleeping) {
            this.bedPosition = new Position(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.wakeUpPlayer(true, true, false);
        }

        if (tag.hasKey("SpawnX") && tag.hasKey("SpawnY") && tag.hasKey("SpawnZ")) {
            this.playerSpawn = new Position(tag.getInteger("SpawnX"), tag.getInteger("SpawnY"), tag.getInteger("SpawnZ"));
        }

        if (tag.hasKey("SpawnHellX") && tag.hasKey("SpawnHellY") && tag.hasKey("SpawnHellZ")) {
            this.hellSpawn = new Position(tag.getInteger("SpawnHellX"), tag.getInteger("SpawnHellY"), tag.getInteger("SpawnHellZ"));
        }

        if (tag.hasKey("SpawnSkyX") && tag.hasKey("SpawnSkyY") && tag.hasKey("SpawnSkyZ")) {
            this.skySpawn = new Position(tag.getInteger("SpawnSkyX"), tag.getInteger("SpawnSkyY"), tag.getInteger("SpawnSkyZ"));
        }

        if (tag.hasKey("DeathX") && tag.hasKey("DeathY") && tag.hasKey("DeathZ")) {
            this.lastDeathPos = new Position(tag.getInteger("DeathX"), tag.getInteger("DeathY"), tag.getInteger("DeathZ"));
        }

        if (tag.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

        this.loadPositions(tag);
        this.foodStats.load(tag);
        this.properties.readNBT(tag);
    }

    private void loadPositions(CompoundTag tag) {
        Position[] inputPositions = new Position[]{null, null, null, null, null, null};
        Position[] outputPositions = new Position[]{null, null, null, null, null, null};
        Position[] inputPositionsHell = new Position[]{null, null, null, null, null, null};
        Position[] outputPositionsHell = new Position[]{null, null, null, null, null, null};
        Position[] inputPositionsSky = new Position[]{null, null, null, null, null, null};
        Position[] outputPositionsSky = new Position[]{null, null, null, null, null, null};
        Position[] inputPositionsVoid = new Position[]{null, null, null, null, null, null};
        Position[] outputPositionsVoid = new Position[]{null, null, null, null, null, null};

        for (int i = 0; i < 6; i++) {
            if (tag.hasKey("inputX_" + i) && tag.hasKey("inputY_" + i) && tag.hasKey("inputZ_" + i)) {
                inputPositions[i] = new Position(tag.getInteger("inputX_" + i), tag.getInteger("inputY_" + i), tag.getInteger("inputZ_" + i));
            }

            if (tag.hasKey("outputX_" + i) && tag.hasKey("outputY_" + i) && tag.hasKey("outputZ_" + i)) {
                outputPositions[i] = new Position(tag.getInteger("outputX_" + i), tag.getInteger("outputY_" + i), tag.getInteger("outputZ_" + i));
            }

            if (tag.hasKey("inputHellX_" + i) && tag.hasKey("inputHellY_" + i) && tag.hasKey("inputHellZ_" + i)) {
                inputPositionsHell[i] = new Position(tag.getInteger("inputHellX_" + i), tag.getInteger("inputHellY_" + i), tag.getInteger("inputHellZ_" + i));
            }

            if (tag.hasKey("outputHellX_" + i) && tag.hasKey("outputHellY_" + i) && tag.hasKey("outputHellZ_" + i)) {
                outputPositionsHell[i] = new Position(
                    tag.getInteger("outputHellX_" + i), tag.getInteger("outputHellY_" + i), tag.getInteger("outputHellZ_" + i)
                );
            }

            if (tag.hasKey("inputSkyX_" + i) && tag.hasKey("inputSkyY_" + i) && tag.hasKey("inputSkyZ_" + i)) {
                inputPositionsSky[i] = new Position(tag.getInteger("inputSkyX_" + i), tag.getInteger("inputSkyY_" + i), tag.getInteger("inputSkyZ_" + i));
            }

            if (tag.hasKey("outputSkyX_" + i) && tag.hasKey("outputSkyY_" + i) && tag.hasKey("outputSkyZ_" + i)) {
                outputPositionsSky[i] = new Position(tag.getInteger("outputSkyX_" + i), tag.getInteger("outputSkyY_" + i), tag.getInteger("outputSkyZ_" + i));
            }

            if (tag.hasKey("inputVoidX_" + i) && tag.hasKey("inputVoidY_" + i) && tag.hasKey("inputVoidZ_" + i)) {
                inputPositionsVoid[i] = new Position(tag.getInteger("inputVoidX_" + i), tag.getInteger("inputVoidY_" + i), tag.getInteger("inputVoidZ_" + i));
            }

            if (tag.hasKey("outputVoidX_" + i) && tag.hasKey("outputVoidY_" + i) && tag.hasKey("outputVoidZ_" + i)) {
                outputPositionsVoid[i] = new Position(
                    tag.getInteger("outputVoidX_" + i), tag.getInteger("outputVoidY_" + i), tag.getInteger("outputVoidZ_" + i)
                );
            }
        }

        this.teleporterPositionsIn.put(0, inputPositions);
        this.teleporterPositionsIn.put(1, inputPositionsHell);
        this.teleporterPositionsIn.put(2, inputPositionsSky);
        this.teleporterPositionsIn.put(3, inputPositionsVoid);
        this.teleporterPositionsOut.put(0, outputPositions);
        this.teleporterPositionsOut.put(1, outputPositionsHell);
        this.teleporterPositionsOut.put(2, outputPositionsSky);
        this.teleporterPositionsOut.put(3, outputPositionsVoid);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("SpawnForced", this.forceSpawn);
        tag.setBoolean("Sleeping", this.sleeping);
        tag.setShort("SleepTimer", (short)this.sleepTimer);
        tag.setInteger("TimeWithoutRest", this.timeWithoutRest);
        tag.setInteger("Dimension", this.dimension);
        tag.setBoolean("FallFlying", this.isGliding());
        if (this.playerSpawn != null) {
            tag.setInteger("SpawnX", this.playerSpawn.x);
            tag.setInteger("SpawnY", this.playerSpawn.y);
            tag.setInteger("SpawnZ", this.playerSpawn.z);
        }

        if (this.hellSpawn != null) {
            tag.setInteger("SpawnHellX", this.hellSpawn.x);
            tag.setInteger("SpawnHellY", this.hellSpawn.y);
            tag.setInteger("SpawnHellZ", this.hellSpawn.z);
        }

        if (this.skySpawn != null) {
            tag.setInteger("SpawnSkyX", this.skySpawn.x);
            tag.setInteger("SpawnSkyY", this.skySpawn.y);
            tag.setInteger("SpawnSkyZ", this.skySpawn.z);
        }

        if (this.lastDeathPos != null) {
            tag.setInteger("DeathX", this.lastDeathPos.x);
            tag.setInteger("DeathY", this.lastDeathPos.y);
            tag.setInteger("DeathZ", this.lastDeathPos.z);
        }

        this.savePositions(tag);
        this.foodStats.save(tag);
        this.properties.writeNBT(tag);
    }

    private void savePositions(CompoundTag tag) {
        for (int i = 0; i < 6; i++) {
            if (this.teleporterPositionsIn.get(0)[i] != null) {
                tag.setInteger("inputX_" + i, this.teleporterPositionsIn.get(0)[i].x);
                tag.setInteger("inputY_" + i, this.teleporterPositionsIn.get(0)[i].y);
                tag.setInteger("inputZ_" + i, this.teleporterPositionsIn.get(0)[i].z);
            }

            if (this.teleporterPositionsOut.get(0)[i] != null) {
                tag.setInteger("outputX_" + i, this.teleporterPositionsOut.get(0)[i].x);
                tag.setInteger("outputY_" + i, this.teleporterPositionsOut.get(0)[i].y);
                tag.setInteger("outputZ_" + i, this.teleporterPositionsOut.get(0)[i].z);
            }

            if (this.teleporterPositionsIn.get(1)[i] != null) {
                tag.setInteger("inputHellX_" + i, this.teleporterPositionsIn.get(1)[i].x);
                tag.setInteger("inputHellY_" + i, this.teleporterPositionsIn.get(1)[i].y);
                tag.setInteger("inputHellZ_" + i, this.teleporterPositionsIn.get(1)[i].z);
            }

            if (this.teleporterPositionsOut.get(1)[i] != null) {
                tag.setInteger("outputHellX_" + i, this.teleporterPositionsOut.get(1)[i].x);
                tag.setInteger("outputHellY_" + i, this.teleporterPositionsOut.get(1)[i].y);
                tag.setInteger("outputHellZ_" + i, this.teleporterPositionsOut.get(1)[i].z);
            }

            if (this.teleporterPositionsIn.get(2)[i] != null) {
                tag.setInteger("inputSkyX_" + i, this.teleporterPositionsIn.get(2)[i].x);
                tag.setInteger("inputSkyY_" + i, this.teleporterPositionsIn.get(2)[i].y);
                tag.setInteger("inputSkyZ_" + i, this.teleporterPositionsIn.get(2)[i].z);
            }

            if (this.teleporterPositionsOut.get(2)[i] != null) {
                tag.setInteger("outputSkyX_" + i, this.teleporterPositionsOut.get(2)[i].x);
                tag.setInteger("outputSkyY_" + i, this.teleporterPositionsOut.get(2)[i].y);
                tag.setInteger("outputSkyZ_" + i, this.teleporterPositionsOut.get(2)[i].z);
            }

            if (this.teleporterPositionsIn.get(3)[i] != null) {
                tag.setInteger("inputVoidX_" + i, this.teleporterPositionsIn.get(3)[i].x);
                tag.setInteger("inputVoidY_" + i, this.teleporterPositionsIn.get(3)[i].y);
                tag.setInteger("inputVoidZ_" + i, this.teleporterPositionsIn.get(3)[i].z);
            }

            if (this.teleporterPositionsOut.get(3)[i] != null) {
                tag.setInteger("outputVoidX_" + i, this.teleporterPositionsOut.get(3)[i].x);
                tag.setInteger("outputVoidY_" + i, this.teleporterPositionsOut.get(3)[i].y);
                tag.setInteger("outputVoidZ_" + i, this.teleporterPositionsOut.get(3)[i].z);
            }
        }
    }

    public void onItemPickup(Entity entity) {
    }

    @Override
    public float getEyeHeight() {
        return 0.12F;
    }

    protected void resetHeight() {
        this.yOffset = 1.62F;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        this.age = 0;
        if (this.health <= 0 || this.properties.gamemode == 1 && !src.hurtCreative() || this.properties.gamemode == 3) {
            return false;
        }

        if (this.inventory.accessorySlots[1] != null
            && this.inventory.accessorySlots[1].itemID == ItemList.shield.id
            && this.inventory.accessorySlots[1].isNotBroken()
            && src == DamageSource.explosion) {
            if (amount > 0) {
                this.inventory.accessorySlots[1].damageItem(amount, this);
            }

            return false;
        } else {
            if (src.getEntity() != null
                && (src.getEntity() instanceof Arrow || src.getEntity() instanceof Skeleton)
                && this.inventory.accessorySlots[1] != null
                && this.inventory.accessorySlots[1].itemID == ItemList.shield.id
                && this.inventory.accessorySlots[1].isNotBroken()) {
                this.inventory.accessorySlots[1].damageItem(1, this);
                this.world.playEntitySound(this, "random.block", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 1.2F));
                this.triggerAchievement(AchievementList.deflect);
                this.addStat(StatList.arrows, 1);
                this.addStat(StatList.useStats[ItemList.shield.id], 1);
                return false;
            }

            if (src.getEntity() instanceof Mob
                && !((Mob)src.getEntity()).boss
                && this.inventory.accessorySlots[1] != null
                && this.inventory.accessorySlots[1].getItem() == ItemList.shieldDamascus
                && this.inventory.accessorySlots[1].isNotBroken()
                && this.sneaking) {
                this.inventory.accessorySlots[1].damageItem(amount, this);
                this.world.playEntitySound(this, "random.block", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 1.2F));
                this.addStat(StatList.useStats[ItemList.shieldDamascus.id], 1);
                return false;
            }

            Entity entity = src.getEntity();
            if (entity != null && (entity instanceof Monster || entity instanceof Arrow)) {
                if (this.world.settings.getDifficulty() == 0) {
                    amount = 0;
                }

                if (this.world.settings.getDifficulty() == 1) {
                    amount = amount / 3 + 1;
                }

                if (this.world.settings.getDifficulty() == 3) {
                    amount = amount * 3 / 2;
                }

                this.setCanSprint(false);
                if (this.inventory.accessorySlots[2] != null && this.inventory.accessorySlots[2].getItem() == ItemList.capeInvisibility) {
                    this.addEffect(new EffectData(EffectList.invisibility.id, 100, 0));
                    ((Monster)entity).attackTarget = null;
                }
            }

            if (this.inventory.accessorySlots[1] != null
                && this.inventory.accessorySlots[1].itemID == ItemList.shieldRepulsion.id
                && this.inventory.accessorySlots[1].isNotBroken()
                && entity != null
                && entity instanceof Mob) {
                double x = this.posX - entity.posX;
                double z = this.posZ - entity.posZ;
                Mob mob = (Mob)entity;

                while (x * x + z * z < 1.0E-4) {
                    x = (Math.random() - Math.random()) * 0.01;
                    z = (Math.random() - Math.random()) * 0.01;
                }

                this.attackAngle = (float)(Math.atan2(z, x) * 180.0 / (float) Math.PI) - this.yaw;
                mob.knockBack(this, x, z);
                mob.addEffect(new EffectData(EffectList.weakness.id, 150, 0));
                mob.addEffect(new EffectData(EffectList.slowness.id, 150, 1));
                mob.addEffect(new EffectData(EffectList.fragility.id, 100, 0));
                if (amount > 0) {
                    this.inventory.accessorySlots[1].damageItem(amount, this);
                }

                this.addStat(StatList.useStats[ItemList.shieldRepulsion.id], 1);
                return super.damageEntity(src, amount);
            } else {
                if (this.inventory.hasSetEquipped(ArmorMaterial.SAPPHIRE) && entity != null) {
                    entity.freeze = 100;
                }

                if (this.inventory.hasSetEquipped(ArmorMaterial.RUBY) && entity != null) {
                    entity.fire = 100;
                    if (src.isFireDamage()) {
                        return false;
                    }
                }

                if (this.inventory.hasSetEquipped(ArmorMaterial.ADAMANTINE) && this.rand.nextInt(100) < 10) {
                    return false;
                }

                if (amount == 0) {
                    return false;
                }

                Entity attacker = entity;
                if (entity instanceof Arrow && ((Arrow)entity).shooter != null) {
                    attacker = ((Arrow)entity).shooter;
                }

                if (attacker instanceof Mob) {
                    this.alertWolves((Mob)attacker, false);
                }

                this.addStat(StatList.damageTakenStat, amount);
                return super.damageEntity(src, amount);
            }
        }
    }

    @Override
    public void onStruckByLightning(LightningBolt bolt) {
        if (this.inventory.accessorySlots[3] != null
            && this.inventory.accessorySlots[3].getItem() == ItemList.necklace
            && this.inventory.accessorySlots[3].isNotBroken()) {
            this.addEffect(new EffectData(EffectList.regeneration.id, 500, 1));
            this.addEffect(new EffectData(EffectList.resistance.id, 500, 1));
            this.addEffect(new EffectData(EffectList.fireResistance.id, 500, 0));
            this.addEffect(new EffectData(EffectList.strength.id, 500, 1));
            this.inventory.accessorySlots[3].damageItem(5, this);
        } else {
            super.onStruckByLightning(bolt);
        }

        this.addStat(StatList.lightningStrike, 1);
    }

    public boolean isBlocking() {
        return this.isUsingItem() && ItemList.items[this.itemInUse.itemID].getItemUseAction(this.itemInUse) == Item.Action.block;
    }

    public int getTotalArmorValue(DamageSource src) {
        if (this.properties.getArmor()) {
            int dmg1 = 0;
            int dmg2 = 0;
            int dmg3 = 0;

            for (int slot = 0; slot < this.inventory.armorSlots.length; slot++) {
                if (this.inventory.armorSlots[slot] != null && this.inventory.armorSlots[slot].getItem() instanceof Armor) {
                    int maxDamage = this.inventory.armorSlots[slot].getMaxDamage();
                    int currDamage = this.inventory.armorSlots[slot].getItemDamage();
                    int damageDifference = maxDamage - currDamage;
                    dmg2 += damageDifference;
                    dmg3 += maxDamage;
                    int val = ((Armor)this.inventory.armorSlots[slot].getItem()).defenseOLD;
                    if (((Armor)this.inventory.armorSlots[slot].getItem()).tier == ArmorMaterial.DAMASCUS) {
                        val = val / 2 + this.inventory.armorSlots[slot].getItemDamage() / 128;
                    }

                    dmg1 += val;
                }
            }

            return dmg3 == 0 ? 0 : (dmg1 - 1) * dmg2 / dmg3 + 1;
        } else {
            int def = 0;
            ItemStack[] stacks = this.inventory.armorSlots;

            for (int index = 0; index < stacks.length; index++) {
                ItemStack stack = stacks[index];
                if (stack != null && stack.getItem() instanceof Armor) {
                    Armor armor = (Armor)stack.getItem();
                    int val = armor.defense;
                    if (armor.tier == ArmorMaterial.DAMASCUS) {
                        val = val / 2 + stack.getItemDamage() / 128;
                    }

                    def += val;
                }
            }

            return def;
        }
    }

    public void damageArmor(int amount) {
        for (int slot = 0; slot < this.inventory.armorSlots.length; slot++) {
            if (this.inventory.armorSlots[slot] != null && this.inventory.armorSlots[slot].getItem() instanceof Armor) {
                this.inventory.armorSlots[slot].damageItem(amount, this);
                if (this.inventory.armorSlots[slot].stackSize == 0) {
                    this.inventory.armorSlots[slot] = null;
                }
            }
        }
    }

    @Override
    protected void applyDamageMultiplier(DamageSource src, int amount) {
        if (this.isBlocking()) {
            amount = 1 + amount >> 1;
        }

        int armorReduceAmount = 0;
        if (!src.unblockable()) {
            armorReduceAmount = this.getTotalArmorValue(src);
            this.damageArmor(amount);
        }

        if (this.properties.gamemode == 2) {
            amount -= this.properties.DEF / 2;
        }

        int remainder = 25 - armorReduceAmount;
        int total = amount * remainder + this.damageRemainder;
        int dmg = 0;
        if (src.getEntity() != null) {
            if (this.inventory.armorSlots[0] != null && this.inventory.armorSlots[0].getItem() == ItemList.bootsThorns) {
                dmg += 2;
            } else if (this.inventory.armorSlots[1] != null && this.inventory.armorSlots[1].getItem() == ItemList.legsThorns) {
                dmg += 2;
            } else if (this.inventory.armorSlots[2] != null && this.inventory.armorSlots[2].getItem() == ItemList.plateThorns) {
                dmg += 2;
            } else if (this.inventory.armorSlots[3] != null && this.inventory.armorSlots[3].getItem() == ItemList.helmetThorns) {
                dmg += 2;
            }
        }

        if (src.getEntity() != null && dmg > 0) {
            src.getEntity().damageEntity(DamageSource.magic, dmg);
        }

        amount = total / 25;
        this.damageRemainder = total % 25;
        this.addExhaustion(src.getHungerDamage());
        if (this.health - amount <= 0 && this.inventory.accessorySlots[3] != null && this.inventory.accessorySlots[3].getItem() == ItemList.rosary) {
            this.breakItem(this.inventory.accessorySlots[3]);
            this.inventory.accessorySlots[3] = null;
            this.heal(this.maxHealth);
            this.addHealth(4);
            this.addEffect(new EffectData(EffectList.absorption.id, 600, 0));
            this.addEffect(new EffectData(EffectList.regeneration.id, 200, 2));
            this.addEffect(new EffectData(EffectList.resistance.id, 900, 0));
            this.triggerAchievement(AchievementList.revive);

            for (int i = 0; i < 20; i++) {
                ParticleType fairy = ParticleType.FIREFLY;
                switch (this.rand.nextInt(3)) {
                    case 0:
                        fairy = ParticleType.FIREFLY;
                        break;
                    case 1:
                        fairy = ParticleType.FIREFLYBLUE;
                        break;
                    case 2:
                        fairy = ParticleType.FIREFLYPINK;
                }

                this.world
                    .spawnParticle(
                        fairy,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02,
                        this.rand.nextGaussian() * 0.02
                    );
            }
        } else {
            super.applyDamageMultiplier(src, amount);
        }
    }

    @Override
    public void jump() {
        super.jump();
        this.addStat(StatList.jumpStat, 1);
        if (this.isSprinting()) {
            this.addExhaustion(0.2F);
        } else {
            this.addExhaustion(0.05F);
        }

        if (this.inventory.accessorySlots[3] != null && this.inventory.accessorySlots[3].getItem() == ItemList.windAmulet && this.hasJumped < 3) {
            this.itemUseCooldown = 5;
            this.maxDelay = 5;
        }
    }

    @Override
    protected int applyDamageModifier(DamageSource src, int amount) {
        int value = super.applyDamageModifier(src, amount);
        if (value <= 0) {
            return 0;
        }

        int modifier = EnchantmentHelper.getDmgModifier(this.inventory, src);
        if (modifier > 20) {
            modifier = 20;
        }

        if (modifier > 0 && modifier <= 20) {
            int difference = 25 - modifier;
            int dmg = value * difference + this.carryoverDamage;
            value = dmg / 25;
            this.carryoverDamage = dmg % 25;
        }

        return value;
    }

    @Override
    public boolean canSprint() {
        if (!this.hasEffect(EffectList.fatigue) && !this.crawling && !this.sneaking) {
            if (this.properties.getHunger() && this.getFoodStats().getHunger() >= 6.0F) {
                return true;
            } else {
                return this.properties.gamemode == 1 ? true : this.getFlag(4);
            }
        } else {
            return false;
        }
    }

    public void crawl() {
        if (this.canCrawlHere() && !this.flying && !this.sitting && !this.sleeping) {
            this.crawling = !this.crawling;
            this.setCrawling(this.crawling);
        }
    }

    public final boolean canCrawlHere() {
        if (!this.sneaking && this.mount == null && (this.onGround || this.isInWater() || this.flying) && !this.sleeping) {
            if (!this.crawling) {
                return true;
            }

            boolean bool = true;

            for (float f = -0.3F; f <= 0.3F; f += 0.3F) {
                for (float q = -0.3F; q <= 0.3F; q += 0.3F) {
                    if (this.world
                        .isBlockFull(MathHelper.floor_double(this.posX + f), MathHelper.floor_double(this.posY + 1.0), MathHelper.floor_double(this.posZ + q))) {
                        bool = false;
                    }
                }
            }

            return bool;
        } else {
            return false;
        }
    }

    public void setCrawling(boolean state) {
        if (state) {
            this.yOffset = 0.56F;
            this.setSize(0.6F, 0.9F);
            this.setPosition(this.posX, this.posY, this.posZ);
        } else {
            this.yOffset = 1.62F;
            this.setSize(0.6F, 1.8F);
            this.setPosition(this.posX, this.posY + 1.3F, this.posZ);
        }
    }

    public void setSneaking() {
        this.sneaking = !this.sneaking;
        if (this.sneaking) {
            this.yOffset = 1.42F;
        } else {
            this.yOffset = 1.62F;
        }
    }

    protected void closeScreen() {
        this.openContainer = this.container;
    }

    public void updateSkin() {
    }

    @Override
    public final boolean canClimb(Block block) {
        return block == BlockList.ladder && this.inventory.accessorySlots[0] != null && this.inventory.accessorySlots[0].getItem() == ItemList.gloves
            ? this.collidedHorizontally
            : super.canClimb(block);
    }

    public void useItemOnEntity(Entity entity) {
        if (!entity.interact(this) || this.sneaking) {
            ItemStack stack = this.getCurrentEquippedItem();
            if (this.getUsing()) {
                stack = this.inventory.getOffHandItem();
            }

            if (stack != null && entity instanceof Mob) {
                stack.useItemOnEntity((Mob)entity, this);
                if (stack.stackSize <= 0) {
                    this.destroyCurrentEquippedItem();
                }
            }
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.inventory.hasSetEquipped(ArmorMaterial.TIDE) || this.hasEffect(EffectList.waterBreathing) || this.properties.gamemode == 1;
    }

    public ItemStack getCurrentEquippedItem() {
        return this.inventory.getCurrentItem();
    }

    public void destroyCurrentEquippedItem() {
        this.inventory.setSlotContents(this.inventory.currentSlot, (ItemStack)null);
    }

    public void destroyOffhandItem() {
        this.inventory.setSlotContents(44, (ItemStack)null);
    }

    public int getAtkDamage(Entity target) {
        ItemStack stack = this.inventory.getStackInSlot(this.inventory.currentSlot);
        if (this.getUsing()) {
            stack = this.inventory.getStackInSlot(44);
        }

        int dmg = 1;
        int dmgMultiplier = 0;
        if (this.properties.gamemode == 2) {
            dmgMultiplier = this.properties.ATK / 2;
        }

        if (this.inventory.accessorySlots[3] != null && this.inventory.accessorySlots[3].getItem() == ItemList.necklace && this.isInWater()) {
            dmgMultiplier = 4;
        }

        if (stack == null
            && this.inventory.accessorySlots[0] != null
            && this.inventory.accessorySlots[0].getItem() == ItemList.gloves
            && this.inventory.accessorySlots[0].isNotBroken()) {
            dmg = 4;
            this.inventory.accessorySlots[0].damageItem(1, this);
        }

        if (stack != null) {
            dmg = stack.getDamageVsEntity(target);
            if (stack.itemID == ItemList.swordDamascus.id || stack.itemID == ItemList.spearDamascus.id) {
                dmg = stack.getDamageVsEntity(target) / 2 + stack.getItemDamage() / 128;
            }

            if ((stack.itemID == ItemList.swordAdamantine.id || stack.itemID == ItemList.spearAdamantine.id) && target.rand.nextInt(10) == 0) {
                dmg *= 2;
            }

            if (stack.itemID == ItemList.mace.id) {
                dmg = (int)(dmg + this.fallDistance * 2.0F);
                if (dmg >= 40) {
                    this.triggerAchievement(AchievementList.mace);
                }
            }
        }

        return dmg + dmgMultiplier;
    }

    public void attackEntityWithItem(Entity entity) {
        int dmg = this.getAtkDamage(entity);
        if (this.properties.getAttack()) {
            int multiplier = this.itemUseCooldown / 5;
            if (multiplier <= 0) {
                multiplier = 1;
            }

            dmg = this.getAtkDamage(entity) / multiplier;
        }

        if (dmg < 0) {
            dmg = 1;
        }

        if (dmg >= 0) {
            int damageMultiplier = 0;
            boolean isLiving = entity instanceof Mob;
            if (isLiving) {
                damageMultiplier = EnchantmentHelper.getAtkModifier(this.inventory, (Mob)entity);
                dmg += damageMultiplier;
            }

            if (this.hasEffect(EffectList.strength)) {
                dmg += 3 << this.getEffect(EffectList.strength).getPotionAmplifier();
            }

            if (this.hasEffect(EffectList.weakness)) {
                dmg -= 2 << this.getEffect(EffectList.weakness).getPotionAmplifier();
            }

            boolean crit = this.motionY < 0.0 && !this.onGround && !this.isInWater();
            if (crit && isLiving) {
                dmg += this.rand.nextInt(dmg / 2 + 2);
                this.spawnCritParticles(entity, ParticleType.CRIT, 16);
            }

            if (damageMultiplier > 0 && isLiving) {
                this.spawnCritParticles(entity, ParticleType.MAGIC, 16);
            }

            if (isLiving) {
                this.spawnCritParticles(entity, ParticleType.DAMAGE, dmg / 2);
            }

            ItemStack heldItem = this.getCurrentEquippedItem();
            if (this.getUsing()) {
                heldItem = this.inventory.getOffHandItem();
            }

            if (heldItem != null && heldItem.getItem() == ItemList.swordMichael) {
                this.spawnSoulParticles(entity);
            }

            if (heldItem != null && heldItem.getItem() != null && heldItem.getItem() == ItemList.battleAxe) {
                this.knockback = 0.8F;
            } else {
                this.knockback = 0.4F;
            }

            boolean attacked = entity.damageEntity(DamageSource.mobDamage(this), dmg);
            if (attacked && this.isSprinting()) {
                entity.addVelocity(-MathHelper.sin((float)(this.yaw * Math.PI / 180.0)) * 0.4, 0.1, MathHelper.cos((float)(this.yaw * Math.PI / 180.0)) * 0.4);
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
                this.setSprinting(false);
            }

            if (heldItem != null && entity instanceof Mob && attacked) {
                heldItem.hitEntity((Mob)entity, this, this.world);
                if (heldItem.stackSize <= 0) {
                    if (this.getUsing()) {
                        this.destroyOffhandItem();
                    } else {
                        this.destroyCurrentEquippedItem();
                    }
                }
            }

            if (entity instanceof Mob) {
                if (entity.isAlive()) {
                    this.alertWolves((Mob)entity, true);
                }

                this.attackTarget = (Mob)entity;
                this.addStat(StatList.damageDealtStat, dmg);
            }

            if (dmg >= 20) {
                this.triggerAchievement(AchievementList.slayer);
            }

            this.addExhaustion(0.1F);
        }
    }

    public void spawnSoulParticles(Entity entity) {
    }

    public void spawnCritParticles(Entity entity1, ParticleType type, int i) {
    }

    protected void alertWolves(Mob mob, boolean tamed) {
        if (!(mob instanceof Creeper) && !(mob instanceof Ghast)) {
            if (mob instanceof Wolf) {
                Wolf wolf = (Wolf)mob;
                if (wolf.isTamed() && this.username.equals(wolf.getOwnerName())) {
                    return;
                }
            }

            if (!(mob instanceof Player) || this.isPVPEnabled()) {
                for (Entity entity5 : this.world
                    .getEntitiesOfType(
                        Wolf.class,
                        BoundingBox.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).expand(16.0, 4.0, 16.0)
                    )) {
                    Wolf wolf = (Wolf)entity5;
                    if (wolf.isTamed() && wolf.attackTarget == null && this.username.equals(wolf.getOwnerName()) && (!tamed || !wolf.isSitting())) {
                        wolf.setSitting(false);
                        wolf.attackTarget = mob;
                    }
                }

                return;
            }
        }
    }

    protected boolean isPVPEnabled() {
        return false;
    }

    public boolean getUsing() {
        return (this.dataSync.getObjectByte(16) & 16) != 0;
    }

    public void setUsing(boolean state) {
        byte set = this.dataSync.getObjectByte(16);
        if (state) {
            this.dataSync.updateObject(16, (byte)(set | 16));
        } else {
            this.dataSync.updateObject(16, (byte)(set & -17));
        }
    }

    @Override
    public Icon getItemIcon(ItemStack stack, int dmg) {
        Icon icon = super.getItemIcon(stack, dmg);
        if (stack.itemID == ItemList.fishingRod.id && this.isHeldItem(stack) && this.bobber != null) {
            icon = ItemList.fishingRod.getIconFromMeta(-1);
        } else if (stack.itemID == ItemList.grapplingHook.id && this.hook != null) {
            icon = GrapplingHook.string;
        } else if (stack.itemID == ItemList.whip.id && this.isHeldItem(stack) && this.swinging) {
            icon = ItemList.whip.getIconFromMeta(-1);
        } else if (stack.itemID == ItemList.gabrielStaff.id && !GabrielStaff.canUseStaff) {
            icon = ItemList.gabrielStaff.getIconFromMeta(-1);
        } else if (stack.itemID == ItemList.lightGem.id) {
            int x = MathHelper.floor(this.posX);
            int y = MathHelper.floor(this.posY);
            int z = MathHelper.floor(this.posZ);
            int lightLvl = this.world.getBlockBrightness(x, y, z);
            icon = ItemList.lightGem.getIconFromMeta(lightLvl);
        } else if (this.itemInUse != null && this.itemInUse == stack && ItemList.items[stack.itemID] instanceof Crossbow && this.isHeldItem(stack)) {
            int drawback = stack.getMaxItemUseDuration() - this.itemAnimFrames;
            if (drawback >= 18) {
                return stack.getItem().getIconFrame(3);
            }

            if (drawback > 13) {
                return stack.getItem().getIconFrame(2);
            }

            if (drawback > 8) {
                return stack.getItem().getIconFrame(1);
            }

            if (drawback > 0) {
                return stack.getItem().getIconFrame(0);
            }
        }

        return icon;
    }

    private boolean isHeldItem(ItemStack item) {
        return this.getCurrentEquippedItem() == item || this.inventory.getOffHandItem() == item;
    }

    public Bed.Status goToSleep(int x, int y, int z) {
        if (this.isPlayerSleeping() || !this.isAlive()) {
            return Bed.Status.OTHER_PROBLEM;
        }

        if (this.properties.getCanSleep()) {
            for (int x1 = -1; x1 < 1; x1++) {
                for (int z1 = -1; z1 < 1; z1++) {
                    for (int y1 = 1; y1 < 3; y1++) {
                        if (this.world.getBlock(x + x1, y + y1, z + z1) == null
                            && !this.world.isDaytime()
                            && (this.world.getBrightness(LightLayer.Block, x + x1, y + y1, z + z1) <= 7 || this.world.isBlockExposed(x + x1, y + y1, z + z1))) {
                            return Bed.Status.NOT_POSSIBLE_HERE;
                        }
                    }
                }
            }

            if ((this.timeWithoutRest <= 0 || !this.preventSleeping()) && this.world.settings.getEvent() <= 0) {
                if (Math.abs(this.posX - x) <= 3.0 && Math.abs(this.posY - y) <= 2.0 && Math.abs(this.posZ - z) <= 3.0) {
                    this.setSize(0.2F, 0.2F);
                    this.yOffset = 0.2F;
                    if (this.world.blockExists(x, y, z)) {
                        int meta = this.world.getBlockMetadata(x, y, z);
                        int bedDirection = Bed.getDirection(meta);
                        double xOffset = 0.5;
                        double zOffset = 0.5;
                        switch (bedDirection) {
                            case 0:
                                zOffset = 0.9;
                                break;
                            case 1:
                                xOffset = 0.1;
                                break;
                            case 2:
                                zOffset = 0.1;
                                break;
                            case 3:
                                xOffset = 0.9;
                        }

                        this.rotatePlayerModel(bedDirection);
                        this.setPosition(x + xOffset, y + 0.9375, z + zOffset);
                    } else {
                        this.setPosition(x + 0.5, y + 0.9375, z + 0.5);
                    }

                    this.sleeping = true;
                    this.sleepTimer = 0;
                    this.bedPosition = new Position(x, y, z);
                    this.motionX = this.motionZ = this.motionY = 0.0;
                    if (!this.world.isOnline) {
                        this.world.wakePlayers();
                    }

                    return Bed.Status.OK;
                } else {
                    return Bed.Status.TOO_FAR_AWAY;
                }
            } else {
                if (this.dimension == 0) {
                    this.setSpawn(new Position(x, y, z), 0, false);
                }

                return Bed.Status.NOT_POSSIBLE_NOW;
            }
        } else {
            this.setSpawn(new Position(x, y, z), 0, false);
            this.addStat(StatList.bedUse, 1);
            this.triggerAchievement(AchievementList.sleep);
            if (this.world.settings.getEvent() > 0) {
                return Bed.Status.NOT_POSSIBLE_NOW;
            }

            if (this.getCurrentEquippedItem() != null && this.getCurrentEquippedItem().getItem() == ItemList.dreamCatcher
                || this.inventory.getOffHandItem() != null && this.inventory.getOffHandItem().getItem() == ItemList.dreamCatcher) {
                this.switchDimension(2, false);
            }

            return Bed.Status.SET_POSITION;
        }
    }

    public void wakeUpPlayer(boolean resetTimer, boolean updateAllP, boolean setPos) {
        this.setSize(0.6F, 1.8F);
        this.resetHeight();
        boolean forceWakeup = false;
        if (this.dimension != 0) {
            Phantom enemy = new Phantom(this.world);
            enemy.setPosition(this.posX + 0.5, this.posY + 1.0, this.posZ + 0.5);
            this.world.spawnEntity(enemy);
            forceWakeup = true;
        }

        Position bedPos = this.bedPosition;
        Position respawnPos = this.bedPosition;
        if (bedPos != null && this.world.getBlock(bedPos.x, bedPos.y, bedPos.z) instanceof Bed) {
            Bed.setOccupied(this.world, bedPos.x, bedPos.y, bedPos.z, false);
            respawnPos = Bed.nearestFreePos(this.world, bedPos.x, bedPos.y, bedPos.z, 0);
            if (respawnPos == null) {
                respawnPos = new Position(bedPos.x, bedPos.y + 1, bedPos.z);
            }

            this.setPosition(respawnPos.x + 0.5, (double)respawnPos.y + this.yOffset + 0.1, respawnPos.z + 0.5);
        } else {
            this.setPosition(this.posX + 0.5, this.posY + this.yOffset + 0.1, this.posZ + 0.5);
        }

        this.sleeping = false;
        if (!this.world.isOnline && updateAllP) {
            this.world.wakePlayers();
        }

        if (setPos && !forceWakeup) {
            this.setSpawn(this.bedPosition, 0, false);
            this.addStat(StatList.bedUse, 1);
            this.triggerAchievement(AchievementList.sleep);
        }

        if (!resetTimer && !forceWakeup) {
            this.sleepTimer = 100;
            if (!this.preventSleeping()) {
                this.switchDimension(2, false);
                return;
            }

            this.timeWithoutRest = (int)this.world.settings.dayLength / 2;
            long time = this.world.settings.getWorldTime() + (int)this.world.settings.dayLength / 2;
            this.world.setTime(time);
        } else {
            this.sleepTimer = 0;
        }
    }

    private void rotatePlayerModel(int direction) {
        this.modelRotX = 0.0F;
        this.modelRotZ = 0.0F;
        switch (direction) {
            case 0:
                this.modelRotZ = -1.8F;
                break;
            case 1:
                this.modelRotX = 1.8F;
                break;
            case 2:
                this.modelRotZ = 1.8F;
                break;
            case 3:
                this.modelRotX = -1.8F;
        }
    }

    protected boolean awaken() {
        return this.sleepTimer > 110;
    }

    public boolean preventSleeping() {
        return this.getCurrentEquippedItem() != null && this.getCurrentEquippedItem().getItem() == ItemList.dreamCatcher
            ? false
            : this.inventory.getOffHandItem() == null || this.inventory.getOffHandItem().getItem() != ItemList.dreamCatcher;
    }

    private boolean isInBed() {
        return this.world.getBlock(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z) instanceof Bed;
    }

    public static Position wakeUpPlayer(World var0, Position var1, int dim) {
        boolean respawnNotFound;
        if (dim == 1) {
            respawnNotFound = var0.getBlock(var1.x, var1.y, var1.z) != BlockList.obsidianCrying;
        } else if (dim == 2) {
            respawnNotFound = var0.getBlock(var1.x, var1.y, var1.z) != BlockList.altar;
        } else {
            respawnNotFound = !(var0.getBlock(var1.x, var1.y, var1.z) instanceof Bed);
        }

        return respawnNotFound ? null : Bed.nearestFreePos(var0, var1.x, var1.y, var1.z, 0);
    }

    public float getBedAngle() {
        if (this.bedPosition != null) {
            int meta = this.world.getBlockMetadata(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z);
            int facing = Bed.getDirection(meta);
            switch (facing) {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    public boolean isPlayerSleeping() {
        return this.sleeping;
    }

    public boolean isPlayerFullyAsleep() {
        return this.sleeping && this.sleepTimer >= 100;
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void setRestTime(int i) {
        this.timeWithoutRest = i;
    }

    public void addExhaustion(float value) {
        if (!this.world.isOnline && this.properties.getHunger()) {
            this.foodStats.addExhaustion(value);
        }
    }

    public FoodData getFoodStats() {
        return this.foodStats;
    }

    public void sendChatMessage(String msg) {
    }

    public void addChatMessage(String msg) {
    }

    public Position getSpawn(int dim) {
        switch (dim) {
            case 1:
                return this.hellSpawn;
            case 2:
                return this.skySpawn;
            default:
                return this.playerSpawn;
        }
    }

    public void setSpawn(Position pos, int dim, boolean forced) {
        if (pos != null) {
            if (dim == 1) {
                this.hellSpawn = new Position(pos);
            } else if (dim == 2) {
                this.skySpawn = new Position(pos);
            } else {
                this.playerSpawn = new Position(pos);
            }

            this.forceSpawn = forced;
        } else {
            if (dim == 1) {
                this.hellSpawn = null;
            } else if (dim == 2) {
                this.skySpawn = null;
            } else {
                this.playerSpawn = null;
            }

            this.forceSpawn = false;
        }
    }

    public boolean canEat(boolean isItEat) {
        if (isItEat) {
            return true;
        } else {
            return this.properties.gamemode == 1
                ? true
                : (this.health < this.maxHealth || this.foodStats.getHunger() < 20) && !this.hasEffect(EffectList.nausea);
        }
    }

    @Override
    protected void fall(float dist) {
        if (!this.flying) {
            if (dist >= 2.0F) {
                this.addStat(StatList.distanceFallenStat, (int)Math.round(dist * 100.0));
            }

            this.hasJumped = 0;
            super.fall(dist);
        }
    }

    @Override
    public void updateRidden() {
        double prevX = this.posX;
        double prevY = this.posY;
        double prevZ = this.posZ;
        super.updateRidden();
        this.prevCamYaw = this.camYaw;
        this.camYaw = 0.0F;
        this.addMountedMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);
    }

    public void triggerAchievement(Stat stat) {
        this.addStat(stat, 1);
    }

    @Override
    public boolean getLuckModifier() {
        boolean coin = this.inventory.accessorySlots[0] != null
            && this.inventory.accessorySlots[0].getItem() == ItemList.coin
            && this.inventory.accessorySlots[0].itemDmg < this.inventory.accessorySlots[0].getItem().getMaxDamage();
        if (coin) {
            this.inventory.accessorySlots[0].damageItem(1, this);
            return true;
        } else {
            return this.hasEffect(EffectList.luck);
        }
    }

    private void addMountedMovementStat(double x, double y, double z) {
        if (this.mount != null) {
            int distTraveled = Math.round(MathHelper.sqrt_double(x * x + y * y + z * z) * 100.0F);
            if (distTraveled > 0) {
                if (this.mount instanceof Minecart) {
                    this.addStat(StatList.distanceByMinecartStat, distTraveled);
                    if (this.minecartStart == null) {
                        this.minecartStart = new Position(
                            MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)
                        );
                    } else if (this.minecartStart
                            .getDistanceSq(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                        >= 1000.0) {
                        this.triggerAchievement(AchievementList.onARail);
                    }
                } else if (this.mount instanceof Horse
                    || this.mount instanceof ZombieHorse
                    || this.mount instanceof SkeletonHorse
                    || this.rider instanceof Pegasus
                    || this.rider instanceof Camel) {
                    this.addStat(StatList.distanceByHorseStat, distTraveled);
                } else if (this.mount instanceof Pig) {
                    this.addStat(StatList.distanceByPigStat, distTraveled);
                } else if (this.mount instanceof Boat || this.mount instanceof Raft) {
                    this.addStat(StatList.distanceByBoatStat, distTraveled);
                }
            }
        }
    }

    public void addMovementStat(double x, double y, double z) {
        if (this.mount == null) {
            if (this.isInMaterial(MaterialList.water)) {
                int distTravelled = Math.round(MathHelper.sqrt_double(x * x + y * y + z * z) * 100.0F);
                if (distTravelled > 0) {
                    this.addExhaustion(0.01F * distTravelled * 0.01F);
                }
            } else if (this.isInWater()) {
                int distTravelled = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (distTravelled > 0) {
                    this.addStat(StatList.distanceSwumStat, distTravelled);
                    this.addExhaustion(0.01F * distTravelled * 0.01F);
                }
            } else if (this.canClimb(BlockList.ladder)) {
                if (y > 0.0) {
                    this.addStat(StatList.distanceClimbedStat, (int)Math.round(y * 100.0));
                }
            } else if (this.onGround) {
                int distTravelled = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (distTravelled > 0) {
                    if (this.isSprinting()) {
                        this.addExhaustion(0.1F * distTravelled * 0.01F);
                        this.addStat(StatList.distanceRanStat, distTravelled);
                    } else {
                        this.addExhaustion(0.0F * distTravelled * 0.01F);
                        if (this.sneaking) {
                            this.addStat(StatList.distanceSneakedStat, distTravelled);
                        } else if (this.crawling) {
                            this.addStat(StatList.distanceCrawledStat, distTravelled);
                        } else {
                            this.addStat(StatList.distanceWalkedStat, distTravelled);
                        }
                    }
                }
            } else {
                int distTravelled = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (distTravelled > 25) {
                    if (this.isGliding()) {
                        distTravelled /= 100;
                    }

                    this.addStat(StatList.distanceFlownStat, distTravelled);
                }
            }
        }
    }

    public int getItemAnimFrames() {
        return this.itemAnimFrames;
    }

    public int getItemUseDur() {
        return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemAnimFrames : 0;
    }

    public void setItemInUse(ItemStack itemstack, int i) {
        if (itemstack != this.itemInUse) {
            this.itemInUse = itemstack;
            this.itemAnimFrames = i;
            if (!this.world.isOnline) {
                this.setEating(true);
            }
        }
    }

    public ItemStack getItemInUse() {
        return this.itemInUse;
    }

    public void stopUsingItem() {
        if (this.itemInUse != null) {
            this.itemInUse.onPlayerStoppedUsing(this.world, this, this.itemAnimFrames);
        }

        this.clearItemInUse();
    }

    public void clearItemInUse() {
        this.itemInUse = null;
        this.itemAnimFrames = 0;
        if (!this.world.isOnline) {
            this.setEating(false);
        }
    }

    public void useItem() {
        if (this.itemInUse != null) {
            this.consumeFood(this.itemInUse, 16);
            int i = this.itemInUse.stackSize;
            ItemStack itemstack = this.itemInUse.onFinishedUsing(this.world, this);
            if (this.inventory.getOffHandItem() != null && this.inventory.getOffHandItem() == this.itemInUse) {
                if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i) {
                    this.inventory.offhandSlot[0] = itemstack;
                    if (itemstack.stackSize == 0) {
                        this.inventory.offhandSlot[0] = null;
                    }
                }
            } else if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i) {
                this.inventory.mainInventory[this.inventory.currentSlot] = itemstack;
                if (itemstack.stackSize == 0) {
                    this.inventory.mainInventory[this.inventory.currentSlot] = null;
                }
            }

            this.stopUsingItem();
            this.clearItemInUse();
        }
    }

    public void consumeFood(ItemStack itemstack, int i) {
        if (itemstack.getItemUseAction() == Item.Action.drink) {
            this.world.playEntitySound(this, "random.drink", 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (itemstack.getItemUseAction() == Item.Action.eat) {
            for (int j = 0; j < i; j++) {
                Vector vector1 = Vector.createVector((this.world.rand.nextDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                vector1.rotateAroundX(-this.pitch * 3.141593F / 180.0F);
                vector1.rotateAroundY(-this.yaw * 3.141593F / 180.0F);
                Vector vector2 = Vector.createVector((this.world.rand.nextFloat() - 0.5) * 0.3, -this.world.rand.nextFloat() * 0.6 - 0.3, 0.6);
                vector2.rotateAroundX(-this.pitch * 3.141593F / 180.0F);
                vector2.rotateAroundY(-this.yaw * 3.141593F / 180.0F);
                vector2 = vector2.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
                this.world
                    .spawnParticle(
                        ParticleContext.setIconContext(ParticleType.ICONCRACK, itemstack.getItem()),
                        vector2.xCoord,
                        vector2.yCoord,
                        vector2.zCoord,
                        vector1.xCoord,
                        vector1.yCoord + 0.05,
                        vector1.zCoord
                    );
            }

            this.world
                .playEntitySound(
                    this, "random.eat", 0.5F + 0.5F * this.world.rand.nextInt(2), (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F + 1.0F
                );
        }
    }

    public boolean isUsingItem() {
        return this.itemInUse != null;
    }

    @Override
    public boolean alwaysTick() {
        return true;
    }

    public Position getDeathCoordinate() {
        return this.lastDeathPos;
    }

    public void setDeathCoordinate(Position deathCoordinate) {
        this.lastDeathPos = deathCoordinate;
    }

    public void displayGUIChest(Inventory inventory, boolean b) {
    }

    public void displayWorkbenchGUI(int x, int y, int z, boolean portable) {
    }

    public void displayGUIFurnace(FurnaceEntity furnace) {
    }

    public void displayGUIEditSign(SignEntity sign) {
    }

    public void displayGUIPainting(int xCoord, int yCoord, int zCoord, byte b8) {
    }

    public void displayGUIBarrel(Inventory inventory) {
    }

    public void displayGUIBookshelf(Inventory inventory) {
    }

    public void displayGUIGenerator(GeneratorEntity generator) {
    }

    public void displayGUIBook(ItemStack stack) {
    }

    public void displayGUIRecipe() {
    }

    public void displayGUIHellforge(HellforgeEntity furnace) {
    }

    public void displayGUIAnvil(int x, int y, int z) {
    }

    public void displayGUISmoker(SmokerEntity smoker) {
    }

    public void displayStonecutterGUI(int xCoord, int yCoord, int zCoord) {
    }

    public void displayGUICrafter(CrafterEntity crafter) {
    }

    public void displayGUIEnderChest(Inventory inv1, Inventory inv2) {
    }

    public void displayGUIEditCommand(CommandBlockEntity cmd) {
    }

    public void displayGUIBeacon(BeaconEntity beacon) {
    }

    public void displayGUIGrindstone(int x, int y, int z) {
    }

    public void displayGUIVacuum(Inventory inv) {
    }

    public void displayGUIDispenser(Inventory inv) {
    }
}
