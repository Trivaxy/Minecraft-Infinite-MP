package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.ParticleType;
import net.minecraft.game.world.World;

public class Sentry extends Seraphim {
    public Sentry(World world) {
        super(world);
        this.setSize(2.0F, 2.0F);
        this.setHealth(100);
        this.scoreValue = 250;
        this.boss = false;
        this.spawnLittleGuy = false;
    }

    @Override
    protected void searchForCrystals() {
    }

    @Override
    public void onDeath() {
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
    }
}
