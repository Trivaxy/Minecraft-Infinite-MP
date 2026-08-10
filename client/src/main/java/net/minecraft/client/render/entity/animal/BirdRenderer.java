package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.flying.FlyingMob;
import net.minecraft.util.math.MathHelper;

public class BirdRenderer extends MobRenderer<FlyingMob> {
    public BirdRenderer(Model model, float shadowSize) {
        super(model, shadowSize);
    }

    protected float getDefaultAngle(FlyingMob birb, float tick) {
        float pos = birb.newPos + (birb.lastGroundPos - birb.newPos) * tick;
        float dist = birb.startPos + (birb.destPos - birb.startPos) * tick;
        return (MathHelper.sin(pos) + 1.0F) * dist;
    }
}
