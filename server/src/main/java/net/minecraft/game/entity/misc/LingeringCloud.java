package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class LingeringCloud extends Entity {
    private EffectData effect;
    private int time;
    private int maxTime;
    private int dmg;

    public LingeringCloud(World world) {
        super(world);
    }

    public LingeringCloud(World level, double x, double y, double z, EffectData effect, int damage) {
        super(level);
        this.setSize(0.0F, 0.0F);
        this.setPosition(x + 0.5, y + 0.1, z + 0.5);
        this.yOffset = 1.5F;
        this.produceFootsteps = false;
        this.maxTime = (int)(40.0 / (Math.random() * 0.8 + 0.2));
        this.effect = effect;
        this.dmg = damage;
    }

    @Override
    public void onTick() {
        float xOffset = (float)(Math.random() - 0.5);
        float zOffset = (float)(Math.random() - 0.5);
        float aliveTime = (float)this.time / this.maxTime;
        int potion = this.dmg;
        if (potion > 19) {
            potion %= 19;
        }

        for (int n = 0; n < 4; n++) {
            if (Math.random() > aliveTime) {
                if (this.effect != null) {
                    double red = (EffectList.effects[potion].getLiquidColor() >> 16 & 0xFF) / 255.0;
                    double green = (EffectList.effects[potion].getLiquidColor() >> 8 & 0xFF) / 255.0;
                    double blue = (EffectList.effects[potion].getLiquidColor() >> 0 & 0xFF) / 255.0;
                    this.world.spawnParticle(ParticleType.SPELL, this.posX + xOffset, this.posY, this.posZ + zOffset, red, green, blue);
                } else {
                    this.world
                        .spawnParticle(
                            ParticleType.SMOKE, this.posX + xOffset, this.posY, this.posZ + zOffset, this.posX + xOffset, this.posY, this.posZ + zOffset
                        );
                }
            }
        }

        if (this.time++ > this.maxTime) {
            this.setDead();
        }
    }

    @Override
    public void onCollideWith(Entity entity) {
        if (!this.world.isOnline && entity instanceof Mob) {
            if (this.effect != null) {
                ((Mob)entity).addEffect(this.effect);
                if (this.effect.getPotionID() == EffectList.heal.id) {
                    ((Mob)entity).queueHealing(this.effect.getPotionAmplifier() + 1);
                } else if (this.effect.getPotionID() == EffectList.harm.id) {
                    entity.damageEntity(DamageSource.magic, this.effect.getPotionAmplifier() + 1);
                } else if (this.effect.getPotionID() == EffectList.water.id) {
                    entity.fire = 0;
                }
            } else {
                entity.damageEntity(DamageSource.magic, 1);
            }
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        if (tag.hasKey("Effect")) {
            CompoundTag effectTag = tag.getCompoundTag("Effect");
            byte id = effectTag.getByte("Id");
            byte amp = effectTag.getByte("Amplifier");
            int dur = effectTag.getInteger("Duration");
            this.effect = new EffectData(id, dur, amp);
        }

        this.time = tag.getInteger("AliveTime");
        this.maxTime = tag.getInteger("MaxTime");
        this.dmg = tag.getInteger("Damage");
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        if (this.effect != null) {
            EffectData effect = this.effect;
            CompoundTag effectTag = new CompoundTag();
            effectTag.setByte("Id", (byte)effect.getPotionID());
            effectTag.setByte("Amplifier", (byte)effect.getPotionAmplifier());
            effectTag.setInteger("Duration", effect.getPotionDuration());
            tag.setTag("Effect", effectTag);
        }

        tag.setInteger("AliveTime", this.time);
        tag.setInteger("MaxTime", this.maxTime);
        tag.setInteger("Damage", this.dmg);
    }
}
