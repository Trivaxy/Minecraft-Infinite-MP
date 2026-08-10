package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.monster.elemental.Blaze;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Inferno extends Blaze {
    private String[] names = new String[]{"Yallau", "Uwriaa", "Ishwua", "Shaire", "Neffzei", "Yimidul", "Ghaflei", "Tithlaaeh", "Modhoyil", "Phibtiai"};

    public Inferno(World world) {
        super(world);
        this.setSize(1.6F, 2.0F);
        this.setHealth(250);
        this.texture = "/mob/elemental/inferno.png";
        this.knockback = 1.2F;
        this.scoreValue = 1000;
        this.boss = true;
        this.tier = 3;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.behaviors.addTask(2, new Inferno.ShockwaveAttack(this));
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.rand.nextInt(50) == 0) {
            this.addEffect(new EffectData(EffectList.resistance.id, 100, 0));
        }
    }

    @Override
    public int getSpawnID() {
        return 61;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        return src.getEntity() != null && src.damageType == "arrow" ? false : super.damageEntity(src, amount);
    }

    private class ShockwaveAttack extends Action {
        private Inferno mob;
        private int shockwaveTick = 0;
        private final int shockwaveDuration = 5;

        private ShockwaveAttack(Inferno mob) {
            this.mob = mob;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            return this.mob.attackTarget != null && this.mob.getDistanceTo(this.mob.attackTarget) < 4.0;
        }

        @Override
        public void tick() {
            double centerX = this.mob.posX;
            double centerY = this.mob.posY + 1.0;
            double centerZ = this.mob.posZ;
            int particles = 40;
            double speed = 0.25;

            for (int i = 0; i < particles; i++) {
                double angle = (Math.PI * 2) * i / particles;
                double motionX = Math.cos(angle) * speed;
                double motionZ = Math.sin(angle) * speed;
                this.mob.world.spawnParticle(ParticleType.FLAME, centerX, centerY, centerZ, motionX, 0.02, motionZ);
            }

            this.shockwaveTick++;
            if (this.shockwaveTick > 5) {
                this.mob.attackTarget.damageEntity(DamageSource.mobDamage(this.mob), this.mob.attackStrength);
                this.shockwaveTick = 0;
            }
        }
    }
}
