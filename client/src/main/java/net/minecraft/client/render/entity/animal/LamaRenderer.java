package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.LamaModel;
import net.minecraft.game.entity.mob.animal.mounts.Lama;

public class LamaRenderer extends MobRenderer<Lama> {
    private final String[] colors = new String[]{
        "white",
        "silver",
        "gray",
        "pink",
        "magenta",
        "magenta",
        "purple",
        "blue",
        "light_blue",
        "cyan",
        "lime",
        "green",
        "lime",
        "yellow",
        "orange",
        "red",
        "black",
        "brown"
    };
    private LamaModel saddleModel = new LamaModel(0.5F);

    public LamaRenderer(Model mainModel, float shadowSize) {
        super(mainModel, shadowSize);
    }

    protected boolean renderOverlay(Lama lama, int type, float tick) {
        if (lama.isRideable()) {
            this.loadTexture("/mob/llama/decor/decor_" + this.colors[lama.saddle] + ".png");
            this.setOverlay(this.saddleModel);
            return true;
        } else {
            return false;
        }
    }
}
