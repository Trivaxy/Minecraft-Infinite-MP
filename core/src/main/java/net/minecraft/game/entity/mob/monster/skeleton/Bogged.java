package net.minecraft.game.entity.mob.monster.skeleton;

import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Bogged extends Skeleton {
    public Bogged(World world) {
        super(world);
        this.texture = "/mob/skeleton/bogged.png";
        this.scoreValue = 150;
        this.dropId = ItemList.arrowPoison.id;
        this.vampiric = false;
        this.clearAttributes();
        this.behaviors.addTask(4, new RangedAttack(this, this.moveSpeed, 3, 50));
    }

    @Override
    public int getSpawnID() {
        return 53;
    }

    @Override
    public void addEffect(EffectData effect) {
        if (effect.getPotionID() != EffectList.poison.id) {
            super.addEffect(effect);
        }
    }
}
