package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Stalker extends Monster {
    public Stalker(World world) {
        super(world);
        this.setSize(0.8F, 2.9F);
        this.setHealth(40);
        this.texture = "/mob/ghost/enderman.png";
        this.scoreValue = 150;
        this.moveSpeed = 0.3F;
        this.attackStrength = 7;
        this.stepHeight = 1.0F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(3, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, EffectList.blindness.id, 100, 0, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 128.0F));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 4.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 4.0F, 0, true));
    }

    @Override
    public boolean canSpawnHere() {
        return MathHelper.floor_double(this.boundingBox.minY) <= this.world.getSeaLevel() - 16
            && this.world.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                != BlockList.voidGrass
            && super.canSpawnHere();
    }

    @Override
    public String getSound() {
        return this.attackTarget != null ? "mob.voidmen.angry" : "mob.voidmen.idle";
    }

    @Override
    public int getBrightnessInt(float brightness) {
        return this.attackTarget == null ? 15728880 : super.getBrightnessInt(brightness);
    }

    @Override
    public float getBrightness(float brightness) {
        return this.attackTarget == null ? 1.0F : super.getBrightness(brightness);
    }

    @Override
    protected String getHurtSound() {
        return "mob.voidmen.angry";
    }

    @Override
    protected String getDeathSound() {
        return "mob.voidmen.death";
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.world
            .spawnParticle(
                ParticleType.SMOKE,
                this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                this.posY + this.rand.nextDouble() * this.height,
                this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                0.0,
                0.0,
                0.0
            );
        if (this.attackTarget != null) {
            this.texture = "/mob/ghost/enderman.png";
            if (this.rand.nextInt(25) == 0) {
                this.teleportToEntity(this.attackTarget);
            }
        } else {
            this.texture = "/mob/ghost/void.png";
            if (this.rand.nextInt(125) == 0) {
                this.teleportRandomly(true);
            }
        }

        if (this.world.getBlockBrightness(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) > 7) {
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

            this.world.playEntitySound(this, "mob.voidmen.death", 1.0F, 0.1F);
            this.damageEntity(DamageSource.generic, 2);
        }
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        if (super.damageEntity(source, amount) && this.health > 0) {
            this.teleportRandomly(false);
        }

        return false;
    }

    @Override
    public int getMaxSpawns() {
        return 1;
    }

    @Override
    public void jump() {
    }

    @Override
    public void addEffect(EffectData potionEffect) {
    }

    protected boolean teleportRandomly(boolean canTP) {
        double x = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
        double y = this.posY + (this.rand.nextInt(16) - 4);
        double z = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
        return this.teleportTo(x, y, z, canTP);
    }

    protected boolean teleportToEntity(Entity target) {
        Vector distVector = Vector.createVector(
            this.posX - target.posX, this.boundingBox.minY + this.height / 2.0F - target.posY + target.getEyeHeight(), this.posZ - target.posZ
        );
        distVector = distVector.normalize();
        double dist = 16.0;
        double x = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - distVector.xCoord * dist;
        double y = this.posY + (this.rand.nextInt(16) - 8) - distVector.yCoord * dist;
        double z = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - distVector.zCoord * dist;
        return this.teleportTo(x, y, z, true);
    }

    protected boolean teleportTo(double x, double y, double z, boolean canTP) {
        double posX = this.posX;
        double posY = this.posY;
        double posZ = this.posZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        boolean validPosition = false;
        int entityX = MathHelper.floor_double(this.posX);
        int entityY = MathHelper.floor_double(this.posY);
        int entityZ = MathHelper.floor_double(this.posZ);
        if (canTP && this.world.getBlockBrightness(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z)) > 7) {
            return false;
        }

        if (this.world.blockExists(entityX, entityY, entityZ)) {
            boolean pass = false;

            while (!pass && entityY > 0) {
                int blockID = this.world.getBlockId(entityX, entityY - 1, entityZ);
                if (blockID != 0 && BlockList.blocks[blockID].material.isSolid()) {
                    pass = true;
                } else {
                    this.posY--;
                    entityY--;
                }
            }

            if (pass) {
                this.setPosition(this.posX, this.posY, this.posZ);
                if (this.world.getBoundingBoxes(this, this.boundingBox).size() == 0 && !this.world.containsLiquid(this.boundingBox)) {
                    validPosition = true;
                }
            }
        }

        if (!validPosition) {
            this.setPosition(posX, posY, posZ);
            return false;
        }

        short height = (short)this.world.getWorldHeight();

        for (int blockID = 0; blockID < height; blockID++) {
            double offset = blockID / (height - 1.0);
            float offsetX = (this.rand.nextFloat() - 0.5F) * 0.2F;
            float offsetY = (this.rand.nextFloat() - 0.5F) * 0.2F;
            float offsetZ = (this.rand.nextFloat() - 0.5F) * 0.2F;
            double distX = posX + (this.posX - posX) * offset + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            double distY = posY + (this.posY - posY) * offset + this.rand.nextDouble() * this.height;
            double distZ = posZ + (this.posZ - posZ) * offset + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            this.world.spawnParticle(ParticleType.SMOKE, distX, distY, distZ, offsetX, offsetY, offsetZ);
        }

        return true;
    }

    @Override
    public int getSpawnID() {
        return 69;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getDrop() {
        return ItemList.pearl.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(2);
    }
}
