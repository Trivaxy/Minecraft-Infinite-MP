package net.minecraft.game.item.potion;

import net.minecraft.game.entity.mob.Mob;

public class EffectData {
    private int potionID;
    private int duration;
    private int amplifier;
    public boolean isSpecial;

    public EffectData(int id, int duration, int amplifier) {
        this.potionID = id;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public EffectData(int id, int duration, int amplifier, boolean special) {
        this.potionID = id;
        this.duration = duration;
        this.amplifier = amplifier;
        this.isSpecial = special;
    }

    public EffectData(EffectData effect) {
        this.potionID = effect.potionID;
        this.duration = effect.duration;
        this.amplifier = effect.amplifier;
    }

    public void combine(EffectData effect) {
        if (this.potionID != effect.potionID) {
            System.err.println("This method should only be called for matching effects!");
        }

        if (effect.amplifier > this.amplifier) {
            this.amplifier = effect.amplifier;
            this.duration = effect.duration;
        } else if (effect.amplifier == this.amplifier && this.duration < effect.duration) {
            this.duration = effect.duration;
        }
    }

    public int getPotionID() {
        return this.potionID;
    }

    public int getPotionDuration() {
        return this.duration;
    }

    public int getPotionAmplifier() {
        return this.amplifier;
    }

    public boolean onUpdate(Mob living) {
        if (this.duration > 0) {
            if (EffectList.effects[this.potionID].isReady(this.duration, this.amplifier)) {
                this.performEffect(living);
            }

            this.deincrementDuration();
        }

        return this.duration > 0;
    }

    private int deincrementDuration() {
        return --this.duration;
    }

    public void performEffect(Mob living) {
        if (this.duration > 0) {
            EffectList.effects[this.potionID].performEffect(living, this.amplifier);
        }
    }

    public String getPotionName() {
        return EffectList.effects[this.potionID].getPotionName();
    }

    @Override
    public int hashCode() {
        return this.potionID;
    }
}
