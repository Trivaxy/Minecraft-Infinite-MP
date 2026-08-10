package net.minecraft.game.entity.mob.monster.boss;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.misc.Portal;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Seraphim extends Monster {
    public int deathTimer;
    public VoidCrystal healingCrystal = null;
    private String[] names = new String[]{"Asteraoth", "Usiel", "Erathaol", "Nathanael", "Gadriel", "Adoel", "Hadriel", "Sariel", "Ithuriel", "Lahabiel"};
    protected boolean spawnLittleGuy = true;
    private static Map<Integer, Integer> potionsMap = new HashMap<>();

    public Seraphim(World world) {
        super(world);
        potionsMap.put(0, EffectList.poison.id);
        potionsMap.put(1, EffectList.weakness.id);
        potionsMap.put(2, EffectList.slowness.id);
        potionsMap.put(3, EffectList.fragility.id);
        potionsMap.put(4, EffectList.fatigue.id);
        potionsMap.put(5, EffectList.wither.id);
        this.setSize(4.0F, 4.0F);
        this.setHealth(500);
        this.texture = "/mob/seraphim/seraphim.png";
        this.attackStrength = 8;
        this.scoreValue = 5000;
        this.boss = true;
        this.noClip = true;
        this.moveSpeed = 0.15F;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.moveControl = new FlightControl(this, false);
        this.behaviors.addTask(4, new Seraphim.SeraphAttack(this, this.moveSpeed));
        this.behaviors.addTask(5, new LookAroundFlying(this, false));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Player.class, 100.0F, 0, true));
    }

    @Override
    public boolean isInsideBlock() {
        return false;
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canClimb(Block block) {
        return false;
    }

    @Override
    public boolean canSpawnHere() {
        return false;
    }

    @Override
    public int getMaxSpawns() {
        return 1;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }

    @Override
    public void updateAI() {
        this.tickCrystals();
        if (this.healingCrystal == null) {
            super.updateAI();
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("HasSpawnedMinions", this.spawnLittleGuy);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.spawnLittleGuy = tag.getBoolean("HasSpawnedMinions");
    }

    @Override
    public void mobTick() {
        if (this.health <= 0) {
            float offsetX = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float offsetY = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float offsetZ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.spawnParticle(ParticleType.HUGEEXPLODE, this.posX + offsetX, this.posY + 2.0 + offsetY, this.posZ + offsetZ, 0.0, 0.0, 0.0);
            this.yaw = 0.0F;
        } else {
            super.mobTick();
        }

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY + this.getEyeHeight());
        int z = MathHelper.floor_double(this.posZ);
        if (this.world.isBlockFull(x, y, z) || this.onGround) {
            this.knockBack(null, 0.0, 0.0);
        }
    }

    private void tickCrystals() {
        if (this.healingCrystal != null) {
            if (this.healingCrystal.isDead) {
                this.healingCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.health < this.maxHealth) {
                this.health++;
            }
        }

        this.searchForCrystals();
        if (this.healingCrystal != null) {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            Player closestPlayer = this.world.getClosestPlayer(this, 100.0);
            if (closestPlayer != null && closestPlayer.activeEffects.isEmpty()) {
                closestPlayer.addEffect(new EffectData(potionsMap.get(this.rand.nextInt(6)), 600, 0));
            }

            this.texture = "/mob/seraphim/seraphim_sleep.png";
        } else {
            if (this.boss) {
                this.world
                    .playRecord("Endgame", MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), false);
            }

            if (this.health <= this.maxHealth / 4 && this.boss) {
                this.texture = "/mob/seraphim/seraphim_angry.png";
            } else {
                this.texture = "/mob/seraphim/seraphim.png";
            }

            if (this.health <= 0) {
                float x = (this.rand.nextFloat() - 0.5F) * 8.0F;
                float y = (this.rand.nextFloat() - 0.5F) * 4.0F;
                float z = (this.rand.nextFloat() - 0.5F) * 8.0F;
                this.world.spawnParticle(ParticleType.HUGEEXPLODE, this.posX + x, this.posY + 2.0 + y, this.posZ + z, 0.0, 0.0, 0.0);
            }
        }
    }

    protected void searchForCrystals() {
        if (this.healingCrystal == null) {
            float range = 1024.0F;
            List<Entity> entities = this.world.getEntitiesOfType(VoidCrystal.class, this.boundingBox.expand(range, range, range));
            VoidCrystal crystal = null;
            double max = Double.MAX_VALUE;

            for (Entity entity : entities) {
                double dist = entity.getDistanceSqTo(this);
                if (dist < max && entity instanceof VoidCrystal) {
                    max = dist;
                    crystal = (VoidCrystal)entity;
                }
            }

            this.healingCrystal = crystal;
        }
    }

    private void createEnderPortal() {
        Portal.breakPortal = false;
        if (this.world.getBlock(0, this.world.getTopTileSafe(-12, 0) - 1, 0) != BlockList.endPortal) {
            this.world.setBlockWithNotify(0, this.world.getTopTileSafe(0, 0), 0, BlockList.endPortal.id);
        }

        if (this.world.getBlock(0, this.world.getTopTileSafe(-12, -1) - 1, -1) != BlockList.endPortal) {
            this.world.setBlockWithNotify(0, this.world.getTopTileSafe(0, -1), -1, BlockList.endPortal.id);
        }

        if (this.world.getBlock(-1, this.world.getTopTileSafe(-11, 0) - 1, 0) != BlockList.endPortal) {
            this.world.setBlockWithNotify(-1, this.world.getTopTileSafe(-1, 0), 0, BlockList.endPortal.id);
        }

        if (this.world.getBlock(-1, this.world.getTopTileSafe(-11, -1) - 1, -1) != BlockList.endPortal) {
            this.world.setBlockWithNotify(-1, this.world.getTopTileSafe(-1, -1), -1, BlockList.endPortal.id);
        }

        Portal.breakPortal = true;
    }

    @Override
    public void knockBack(Entity entity, double force, double dist) {
        int xOffset = this.rand.nextInt(16) + 8;
        int zOffset = this.rand.nextInt(16) + 8;
        if (this.rand.nextBoolean()) {
            xOffset = -this.rand.nextInt(16) - 8;
            zOffset = -this.rand.nextInt(16) - 8;
        }

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        if (this.world.getBlock(x + xOffset, y - 5, z + zOffset) == null && this.world.getBlock(x + xOffset, y + 1, z + zOffset) == null) {
            this.setPosition(x + xOffset, y + 1, z + zOffset);

            for (int i = 0; i < 16; i++) {
                this.world.spawnParticle(ParticleType.SMOKE, this.posX, this.posY + 1.0, this.posZ, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        if (this.healingCrystal != null) {
            return false;
        }

        if (source.getEntity() != null && (source.damageType == "arrow" || source.damageType == "thrown") && this.health <= this.maxHealth / 4 && this.boss) {
            return false;
        }

        if (this.health <= this.maxHealth / 2 && this.spawnLittleGuy) {
            Sentry sentry = new Sentry(this.world);
            sentry.setPosition(this.posX + 20.0, this.posY, this.posZ);
            this.world.spawnEntity(sentry);
            Sentry sentry2 = new Sentry(this.world);
            sentry2.setPosition(this.posX - 20.0, this.posY, this.posZ - 10.0);
            this.world.spawnEntity(sentry2);
            this.spawnLittleGuy = false;
        }

        return super.damageEntity(source, amount);
    }

    @Override
    public String getSound() {
        return this.healingCrystal != null ? "mob.seraphim_idle" : "mob.seraphim";
    }

    @Override
    public String getHurtSound() {
        return null;
    }

    @Override
    public String getDeathSound() {
        return "mob.seraphim";
    }

    @Override
    public float getSoundVolume() {
        return 10.0F;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (this.isDead || this.health <= 0) {
            super.moveEntityWithHeading(x, z);
        } else if (this.isInWater()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
        } else if (this.isInLava()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            float force = 0.91F;
            if (this.onGround) {
                force = 0.54600006F;
            }

            float offset = 0.16277136F / (force * force * force);
            this.moveFlying(x, z, this.onGround ? 0.1F * offset : 0.02F);
            force = 0.91F;
            if (this.onGround) {
                force = 0.54600006F;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= force;
            this.motionY *= force;
            this.motionZ *= force;
        }
    }

    @Override
    public void onDeath() {
        this.deathTimer++;
        if (this.deathTimer >= 180 && this.deathTimer <= 200) {
            float x = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float y = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float z = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.world.spawnParticle(ParticleType.HUGEEXPLODE, this.posX + x, this.posY + 2.0 + y, this.posZ + z, 0.0, 0.0, 0.0);
        }

        this.moveEntity(0.0, 0.1, 0.0);
        if (this.deathTimer == 200) {
            if (this.world.currDim instanceof VoidDimension) {
                this.createEnderPortal();
            }

            Player target = this.world.getClosestPlayer(this, 1024.0);
            if (target != null) {
                target.triggerAchievement(AchievementList.finalBoss);
                if (!target.inventory.addItemToInventory(new ItemStack(ItemList.endCrystal.id, 3))) {
                    this.dropItem(ItemList.endCrystal.id, 3, 0, 1.0F);
                }
            }

            this.setDead();
        }
    }

    private class SeraphAttack extends FlyToTarget {
        private int shootTimer = 0;

        public SeraphAttack(Mob parent, float dist) {
            super(parent, dist);
        }

        @Override
        public void tick() {
            if (this.mob.health <= this.mob.maxHealth / 4 && this.spd < 0.3F && this.mob.boss) {
                this.spd *= 2.0F;
            }

            this.mob.moveControl.moveTo(this.mob.attackTarget.posX, this.mob.attackTarget.posY, this.mob.attackTarget.posZ, this.spd);
            this.attackTimer = Math.max(this.attackTimer - 1, 0);
            double dist = this.mob.width * 2.0F * this.mob.width;
            if (this.mob.getDistanceSq(this.mob.attackTarget.posX, this.mob.attackTarget.posY, this.mob.attackTarget.posZ) <= dist) {
                this.attack();
            } else {
                this.shootRandomProjectile();
            }
        }

        private void shootRandomProjectile() {
            double minDist = 64.0;
            if (this.mob.attackTarget.getDistanceSqTo(this.mob) < minDist * minDist && this.mob.boss && this.mob.health > this.mob.maxHealth / 4) {
                double distX = this.mob.attackTarget.posX - this.mob.posX;
                double distY = this.mob.attackTarget.boundingBox.minY + this.mob.attackTarget.height - (this.mob.posY + this.mob.height);
                double distZ = this.mob.attackTarget.posZ - this.mob.posZ;
                if (this.mob.isVisible(this.mob.attackTarget)) {
                    this.shootTimer++;
                    if (this.shootTimer == 20) {
                        this.mob.world.playEntitySound(this.mob, "mob.ghast.shoot", 10.0F, 1.0F / (this.mob.rand.nextFloat() * 0.4F + 0.8F));
                        Projectile proj = Seraphim.this.rand.nextBoolean()
                            ? new Windball(this.mob.world, this.mob, distX, distY, distZ, 10)
                            : new Fireball(this.mob.world, this.mob, distX, distY, distZ, 10);
                        double offset = 4.0;
                        Vector lookVec = this.mob.getLookVector(1.0F);
                        proj.posX = this.mob.posX + lookVec.xCoord * offset;
                        proj.posY = this.mob.posY + this.mob.height / 2.0F - 1.5;
                        proj.posZ = this.mob.posZ + lookVec.zCoord * offset;
                        this.mob.world.spawnEntity(proj);
                        if (this.mob.health <= this.mob.maxHealth / 4) {
                            this.shootTimer = -50;
                        } else {
                            this.shootTimer = -100;
                        }
                    }
                } else if (this.shootTimer > 0) {
                    this.shootTimer--;
                }
            } else if (this.shootTimer > 0) {
                this.shootTimer--;
            }
        }
    }
}
