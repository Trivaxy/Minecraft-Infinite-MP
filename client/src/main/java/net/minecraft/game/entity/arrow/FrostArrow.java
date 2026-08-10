package net.minecraft.game.entity.arrow;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class FrostArrow extends Arrow {
    public FrostArrow(World world) {
        super(world);
        this.pickupID = ItemList.arrowFrost.id;
    }

    public FrostArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.pickupID = ItemList.arrowFrost.id;
    }

    public FrostArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
        this.pickupID = ItemList.arrowFrost.id;
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        if (entityHit instanceof Player) {
            ((Mob)entityHit).addEffect(new EffectData(EffectList.slowness.id, 100, 2));
        } else {
            entityHit.freeze = 100;
        }

        super.onEntityHit(entityHit);
    }

    @Override
    protected void produceParticles() {
        double red = (EffectList.effects[EffectList.swiftness.id].getLiquidColor() >> 16 & 0xFF) / 255.0;
        double green = (EffectList.effects[EffectList.swiftness.id].getLiquidColor() >> 8 & 0xFF) / 255.0;
        double blue = (EffectList.effects[EffectList.swiftness.id].getLiquidColor() >> 0 & 0xFF) / 255.0;
        this.world.spawnParticle(ParticleType.SPELL, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, red, green, blue);
    }
}
