package net.minecraft.game.entity.projectile;

import java.util.List;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.boss.Witch;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class ThrownPotion extends Projectile {
    public ThrownPotion(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.itemAssigned = ItemList.splashPotion;
        this.gravityForce = 0.05;
    }

    public ThrownPotion(World world, Mob mob, int dmg) {
        super(world, mob, dmg);
        this.setHeading(this.motionX, this.motionY, this.motionZ, 0.75F, 1.0F);
        this.itemAssigned = ItemList.splashPotion;
        this.gravityForce = 0.05;
    }

    public ThrownPotion(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setHeading(this.motionX, this.motionY, this.motionZ, 0.75F, 1.0F);
        this.itemAssigned = ItemList.splashPotion;
        this.gravityForce = 0.05;
    }

    @Override
    protected void onImpact(TargetingSystem hit) {
        if (!this.world.isOnline) {
            List<EffectData> effects = ItemList.potion.getEffects(this.damage);
            int level = 0;
            if (effects != null && !effects.isEmpty() || this.damage <= 19) {
                BoundingBox boundingBox = this.boundingBox.expand(4.0, 2.0, 4.0);
                List<Entity> entities = this.world.getEntitiesOfType(Mob.class, boundingBox);
                if (entities != null && !entities.isEmpty()) {
                    for (Entity candidate : entities) {
                        Mob entity = (Mob)candidate;
                        double dist = this.getDistanceSqTo(entity);
                        if (!(dist >= 16.0)) {
                            double maxDist = 1.0 - Math.sqrt(dist) / 4.0;
                            if (entity == hit.entityHit) {
                                maxDist = 1.0;
                            }

                            if (this.damage <= 19) {
                                EffectData effect = new EffectData(this.damage, EffectList.effects[this.damage].defaultDuration / 2, level);
                                if (EffectList.effects[this.damage].isInstant()) {
                                    EffectList.effects[this.damage].affectEntity(this.thrower, entity, effect.getPotionAmplifier(), maxDist);
                                } else {
                                    int durationTicks = (int)(maxDist * effect.getPotionDuration() + 0.5);
                                    if (durationTicks > 20) {
                                        entity.addEffect(effect);
                                        if (effect.getPotionID() == EffectList.waterBreathing.id) {
                                            entity.air = entity.maxAir;
                                        }
                                    } else {
                                        entity.fire = 0;
                                    }
                                }
                            } else {
                                for (EffectData pe : effects) {
                                    int pid = pe.getPotionID();
                                    if (EffectList.effects[pid].isInstant()) {
                                        EffectList.effects[pid].affectEntity(this.thrower, entity, pe.getPotionAmplifier(), maxDist);
                                    } else {
                                        int duration = (int)(maxDist * pe.getPotionDuration() + 0.5);
                                        if (duration > 20) {
                                            entity.addEffect(new EffectData(pid, duration, pe.getPotionAmplifier()));
                                        }
                                    }
                                }
                            }

                            if (this.thrower instanceof Player && entity instanceof Witch) {
                                ((Player)this.thrower).triggerAchievement(AchievementList.witch);
                            }
                        }
                    }
                }
            }

            this.world.playEffect(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), this.damage);
            if (this.damage > 0) {
                if (this.damage <= 19) {
                    this.world
                        .createMagicExplosion(
                            this,
                            this.posX,
                            this.posY,
                            this.posZ,
                            3.0F,
                            new EffectData(this.damage, EffectList.effects[this.damage].defaultDuration / 2, level),
                            this.damage
                        );
                } else if (effects != null && !effects.isEmpty()) {
                    for (EffectData pe : effects) {
                        this.world
                            .createMagicExplosion(
                                this,
                                this.posX,
                                this.posY,
                                this.posZ,
                                3.0F,
                                new EffectData(pe.getPotionID(), pe.getPotionDuration(), pe.getPotionAmplifier()),
                                this.damage
                            );
                    }
                }
            }

            this.setDead();
        }
    }
}
