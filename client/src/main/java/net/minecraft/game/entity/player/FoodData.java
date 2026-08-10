package net.minecraft.game.entity.player;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.item.food.FoodItem;

public class FoodData {
    private int hunger = 20;
    private int prevHunger = 20;
    private int timer = 0;
    private float saturation = 5.0F;
    private float exhaustion;

    public void addStats(int hunger, float sat) {
        this.hunger = Math.min(hunger + this.hunger, 20);
        this.saturation = Math.min(this.saturation + hunger * sat * 2.0F, this.hunger);
    }

    public void addStats(FoodItem food) {
        this.addStats(food.healAmount, food.saturationAmount);
    }

    public void onUpdate(Player player) {
        int difficulty = player.world.settings.getDifficulty();
        this.prevHunger = this.hunger;
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturation > 0.0F) {
                this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            } else if (difficulty > 0) {
                this.hunger = Math.max(this.hunger - 1, 0);
            }
        }

        if (this.saturation > 0.0F && player.health < player.maxHealth && this.hunger >= 20) {
            this.timer++;
            if (this.timer >= 10) {
                player.heal(1);
                this.addExhaustion(Math.min(this.saturation, 6.0F));
                this.timer = 0;
            }
        } else if (this.hunger >= 18 && player.health < player.maxHealth) {
            this.timer++;
            if (this.timer >= 80) {
                player.heal(1);
                this.addExhaustion(6.0F);
                this.timer = 0;
            }
        } else if (this.hunger <= 0) {
            this.timer++;
            if (this.timer >= 80) {
                if (player.health > 10 || difficulty >= 3 || player.health > 1 && difficulty >= 2) {
                    player.damageEntity(DamageSource.starve, 1);
                }

                this.timer = 0;
            }
        } else {
            this.timer = 0;
        }
    }

    public void load(CompoundTag tag) {
        if (tag.hasKey("foodLevel")) {
            this.hunger = tag.getInteger("foodLevel");
            this.timer = tag.getInteger("foodTickTimer");
            this.saturation = tag.getFloat("foodSaturationLevel");
            this.exhaustion = tag.getFloat("foodExhaustionLevel");
        }
    }

    public void save(CompoundTag tag) {
        tag.setInteger("foodLevel", this.hunger);
        tag.setInteger("foodTickTimer", this.timer);
        tag.setFloat("foodSaturationLevel", this.saturation);
        tag.setFloat("foodExhaustionLevel", this.exhaustion);
    }

    public int getHunger() {
        return this.hunger;
    }

    public int prevHunger() {
        return this.prevHunger;
    }

    public boolean needFood() {
        return this.hunger < 20;
    }

    public void addExhaustion(float amount) {
        this.exhaustion = Math.min(this.exhaustion + amount, 40.0F);
    }

    public float getSaturation() {
        return this.saturation;
    }

    public void setHunger(int amount) {
        this.hunger = amount;
    }

    public void setSaturation(float amount) {
        this.saturation = amount;
    }
}
