package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.LiquidMaterial;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Fireball extends Projectile {
    public Fireball(World world) {
        super(world);
        this.itemAssigned = ItemList.fireball;
        this.gravityForce = 0.0;
    }

    public Fireball(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.fireball;
        if (dmg == 10) {
            this.fire = 500;
        }

        this.gravityForce = 0.0;
    }

    public Fireball(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.fireball;
        this.gravityForce = 0.0;
    }

    public Fireball(World world, Mob thrower, double x, double y, double z, int dmg) {
        super(world, thrower, x, y, z, dmg);
        this.itemAssigned = ItemList.fireball;
        if (dmg == 10) {
            this.fire = 500;
        }

        this.gravityForce = 0.0;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float offset = 0.25F;
            this.world
                .spawnParticle(
                    ParticleType.FIRE,
                    this.posX - this.motionX * offset,
                    this.posY - this.motionY * offset,
                    this.posZ - this.motionZ * offset,
                    this.motionX,
                    this.motionY,
                    this.motionZ
                );
            this.world
                .spawnParticle(
                    ParticleType.SMOKE,
                    this.posX - this.motionX * offset,
                    this.posY - this.motionY * offset,
                    this.posZ - this.motionZ * offset,
                    this.motionX,
                    this.motionY,
                    this.motionZ
                );
        }

        this.fire = 200;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.fire = 100;
            if (this.damage == 10) {
                this.world.createFireExplosion(this, this.posX, this.posY, this.posZ, 2.0F);
                mop.entityHit.damageEntity(DamageSource.fireballDamage(this, this.thrower), 4);
            } else {
                mop.entityHit.damageEntity(DamageSource.fireballDamage(this, this.thrower), this.damage);
            }
        } else {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;
            if (this.world.getBlock(x, y, z) == BlockList.mobSpawner && this.world.getTileEntity(x, y, z) != null) {
                SpawnerEntity spawner = (SpawnerEntity)this.world.getTileEntity(x, y, z);
                spawner.spawns = 0;
                this.setDead();
                return;
            }

            x = MathHelper.floor_double(this.posX);
            y = MathHelper.floor_double(this.posY);
            z = MathHelper.floor_double(this.posZ);
            if (this.world.getBlockId(x, y, z) == 0 && this.world.isSolid(x, y - 1, z) && !(this.world.getBlockMaterial(x, y, z) instanceof LiquidMaterial)) {
                int belowBlock = this.world.getBlockId(x, y - 1, z);
                this.world
                    .setBlockWithNotify(
                        x, y, z, belowBlock != BlockList.slowSand.id && belowBlock != BlockList.soulSoil.id ? BlockList.fire.id : BlockList.hellfire.id
                    );
            }

            if (this.damage == 10) {
                this.world.createFireExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
            }
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.SMOKE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
