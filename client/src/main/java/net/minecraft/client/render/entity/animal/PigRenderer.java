package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.mounts.Pig;

public class PigRenderer extends MobRenderer<Pig> {
    public PigRenderer(Model model, Model overlay, float shadow) {
        super(model, shadow);
        this.setOverlay(overlay);
    }

    protected boolean renderOverlay(Pig pig, int layer, float tick) {
        this.loadTexture("/mob/pig/saddle.png");
        return layer == 0 && pig.isRideable();
    }
}
