package net.minecraft.game.entity.mob.monster.creeper;

import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class CreeperMagic extends Creeper {
    public CreeperMagic(World world) {
        super(world);
        this.texture = "/mob/creeper/creeper_purple.png";
    }

    @Override
    protected void explode() {
        this.world.createMagicExplosion(this, this.posX, this.posY, this.posZ, 3.0F, null, 0);
    }

    @Override
    public void addEffect(EffectData effect) {
        if (!EffectList.effects[effect.getPotionID()].isNegative()) {
            super.addEffect(effect);
        }
    }
}
