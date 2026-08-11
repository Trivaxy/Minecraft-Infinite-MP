package net.minecraft.game.item.potion;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;

public class Effect {
    private String name = "";
    public final int id;
    private int iconIndex = -1;
    public int defaultDuration = 3600;
    private int liquidColor;
    private double effectiveness;
    private boolean isNegative;
    public boolean isMainEffect = false;

    protected Effect(int id, boolean instant, int col) {
        this.id = id;
        EffectList.effects[id] = this;
        if (instant) {
            this.effectiveness = 0.5;
        } else {
            this.effectiveness = 1.0;
        }

        this.liquidColor = col;
    }

    protected Effect setIconIndex(int x, int y) {
        this.iconIndex = x + y * 10;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public void performEffect(Mob living, int amplifier) {
        if (this.id == EffectList.regeneration.id) {
            if (living.health < living.maxHealth) {
                living.heal(1);
            }
        } else if (this.id == EffectList.poison.id) {
            if (!(living instanceof Player) || living.health > 1) {
                living.damageEntity(DamageSource.magic, 1);
            }
        } else if (this.id == EffectList.hunger.id && living instanceof Player) {
            ((Player)living).addExhaustion(0.025F * (amplifier + 1));
        } else if (this.id == EffectList.heal.id) {
            living.heal(6 << amplifier);
        } else if (this.id == EffectList.harm.id) {
            living.damageEntity(DamageSource.magic, 6 << amplifier);
        }
    }

    public void affectEntity(Mob source, Mob target, int amp, double dist) {
        if (this.id == EffectList.harm.id) {
            int amount = (int)(dist * (6 << amp) + 0.5);
            if (source == null) {
                target.damageEntity(DamageSource.magic, amount);
            } else {
                target.damageEntity(DamageSource.magicDamage(target, source), amount);
            }
        } else if (this.id == EffectList.heal.id) {
            int amount = (int)(dist * (6 << amp) + 0.5);
            target.heal(amount);
        }
    }

    public boolean isInstant() {
        return this.defaultDuration == 1;
    }

    public boolean isReady(int i, int j) {
        int i3 = 25 >> j;
        return i3 > 0 ? i % i3 == 0 : true;
    }

    public Effect setPotionName(String name) {
        this.name = name;
        return this;
    }

    public String getPotionName() {
        return this.name;
    }

    public boolean hasIcon() {
        return this.iconIndex >= 0;
    }

    public int getIconIndex() {
        return this.iconIndex;
    }

    public boolean isNegative() {
        return this.isNegative;
    }

    protected Effect setNegative() {
        this.isNegative = true;
        return this;
    }

    protected Effect setMainEffect() {
        this.isMainEffect = true;
        return this;
    }

    public static String getDurationString(EffectData effect) {
        int dur = effect.getPotionDuration();
        int second = dur / 20;
        int minute = second / 60;
        second %= 60;
        return second < 10 ? minute + ":0" + second : minute + ":" + second;
    }

    public static String getDurationString(int i) {
        int dur = i;
        int second = dur / 20;
        int minute = second / 60;
        second %= 60;
        return second < 10 ? minute + ":0" + second : minute + ":" + second;
    }

    protected Effect setEffectiveness(double d) {
        this.effectiveness = d;
        return this;
    }

    public double getEffectiveness() {
        return this.effectiveness;
    }

    protected Effect setDuration(int dur) {
        this.defaultDuration = dur;
        return this;
    }

    public int getLiquidColor() {
        return this.liquidColor;
    }
}
