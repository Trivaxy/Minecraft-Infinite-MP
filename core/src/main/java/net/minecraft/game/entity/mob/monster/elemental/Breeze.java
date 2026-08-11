package net.minecraft.game.entity.mob.monster.elemental;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Breeze extends Blaze {
    public Breeze(World world) {
        super(world);
        this.texture = "/mob/elemental/wind_elemental.png";
        this.moveSpeed = 0.27F;
        this.burns = this.canGlow = false;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.MONSTER;
    }

    @Override
    public int getSpawnID() {
        return 62;
    }

    @Override
    public String getSound() {
        return "mob.breeze";
    }

    @Override
    protected String getHurtSound() {
        return "mob.breezehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.breezedeath";
    }

    @Override
    public void spawnParticles() {
        this.world
            .spawnParticle(
                ParticleType.EXPLODE,
                this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                this.posY + this.rand.nextDouble() * this.height,
                this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                0.0,
                0.0,
                0.0
            );
    }

    @Override
    public void onDeath() {
        this.deathTimer++;
        if (this.deathTimer > 20) {
            this.setDead();

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

            this.world.createWindExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
        }
    }

    @Override
    public int getDrop() {
        return ItemList.windCharge.id;
    }
}
