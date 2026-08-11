package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Witch extends Monster {
    private String[] names = new String[]{"Iris", "Callista", "Kalliope", "Reagan", "Mina", "Zaleria", "Ferula", "Aileen", "Cadence", "Shearah"};

    public Witch(World world) {
        super(world);
        this.setSize(0.8F, 2.34F);
        this.setHealth(250);
        this.texture = "/mob/witch.png";
        this.moveSpeed = 0.25F;
        this.boss = true;
        this.tier = 3;
        this.scoreValue = 1000;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(4, new RangedAttack(this, this.moveSpeed, -7, 100));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(6, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.rand.nextInt(50) == 0) {
            if (this.fire > 0) {
                this.addEffect(new EffectData(EffectList.fireResistance.id, 300, 1));
            }

            if (this.air <= 0) {
                this.addEffect(new EffectData(EffectList.waterBreathing.id, 500, 1));
            }

            if (this.rand.nextInt(5) == 0) {
                this.addEffect(new EffectData(EffectList.heal.id, 1, 1));
            } else {
                this.addEffect(new EffectData(EffectList.resistance.id, 100, 0));
            }
        }
    }

    @Override
    public void addEffect(EffectData effect) {
        if (!EffectList.effects[effect.getPotionID()].isNegative()) {
            super.addEffect(effect);
        }
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public int getSpawnID() {
        return 60;
    }

    @Override
    public int getDrop() {
        return ItemList.pouch.id;
    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (!this.world.isOnline) {
            this.dropItem(ItemList.potion.id, 1, this.rand.nextInt(18), 1.0F);
        }
    }

    @Override
    public final String getSound() {
        return "mob.witch";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.witchhurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.witchdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.zombiestep", 0.15F, 1.0F);
    }
}
