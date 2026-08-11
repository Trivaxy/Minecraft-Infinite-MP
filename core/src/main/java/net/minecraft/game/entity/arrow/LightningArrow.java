package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class LightningArrow extends Arrow {
    public LightningArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowLightning.id;
    }

    public LightningArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowLightning.id;
    }

    public LightningArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowLightning.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        this.world.spawnEntity(new LightningBolt(this.world, entityHit.posX, entityHit.posY, entityHit.posZ));
        super.onEntityHit(entityHit);
    }

    @Override
    protected void produceParticles() {
        double red = (EffectList.effects[EffectList.haste.id].getLiquidColor() >> 16 & 0xFF) / 255.0;
        double green = (EffectList.effects[EffectList.haste.id].getLiquidColor() >> 8 & 0xFF) / 255.0;
        double blue = (EffectList.effects[EffectList.haste.id].getLiquidColor() >> 0 & 0xFF) / 255.0;
        this.world.spawnParticle(ParticleType.SPELL, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, red, green, blue);
    }
}
