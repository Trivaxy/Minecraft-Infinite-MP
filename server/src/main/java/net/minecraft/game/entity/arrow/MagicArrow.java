package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class MagicArrow extends Arrow {
    public MagicArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowEffect.id;
    }

    public MagicArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowEffect.id;
    }

    public MagicArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowEffect.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        if (entityHit instanceof Mob) {
            ((Mob)entityHit).addEffect(new EffectData(this.rand.nextInt(19) + 1, 300, 0));
        }

        super.onEntityHit(entityHit);
    }

    @Override
    protected void produceParticles() {
        double red = (EffectList.effects[this.rand.nextInt(19) + 1].getLiquidColor() >> 16 & 0xFF) / 255.0;
        double green = (EffectList.effects[this.rand.nextInt(19) + 1].getLiquidColor() >> 8 & 0xFF) / 255.0;
        double blue = (EffectList.effects[this.rand.nextInt(19) + 1].getLiquidColor() >> 0 & 0xFF) / 255.0;
        this.world.spawnParticle(ParticleType.SPELL, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, red, green, blue);
    }
}
