package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class PoisonArrow extends Arrow {
    public PoisonArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowPoison.id;
    }

    public PoisonArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowPoison.id;
    }

    public PoisonArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowPoison.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        if (entityHit instanceof Mob) {
            ((Mob)entityHit).addEffect(new EffectData(EffectList.poison.id, 100, 0));
        }

        super.onEntityHit(entityHit);
    }

    @Override
    protected void produceParticles() {
        double red = (EffectList.effects[EffectList.poison.id].getLiquidColor() >> 16 & 0xFF) / 255.0;
        double green = (EffectList.effects[EffectList.poison.id].getLiquidColor() >> 8 & 0xFF) / 255.0;
        double blue = (EffectList.effects[EffectList.poison.id].getLiquidColor() >> 0 & 0xFF) / 255.0;
        this.world.spawnParticle(ParticleType.SPELL, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, red, green, blue);
    }
}
