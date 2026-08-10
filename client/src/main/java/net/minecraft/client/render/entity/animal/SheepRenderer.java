package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.SheepModel;
import net.minecraft.game.entity.mob.animal.Sheep;
import org.lwjgl.opengl.GL11;

public class SheepRenderer extends MobRenderer<Sheep> {
    private SheepModel sheepModel = (SheepModel)this.mainModel;

    public SheepRenderer(Model model, Model fur, float shadow) {
        super(model, shadow);
        this.setOverlay(fur);
    }

    public void render(Sheep sheep, double x, double y, double z, float offset, float tick) {
        this.sheepModel.grazing = sheep.grazing;
        super.render(sheep, x, y, z, offset, tick);
    }

    protected boolean renderOverlay(Sheep sheep, int layer, float tick) {
        this.loadTexture("/mob/sheep/sheep_fur.png");
        GL11.glColor3f(
            Sheep.fleeceColors[sheep.getFleeceColor()][0], Sheep.fleeceColors[sheep.getFleeceColor()][1], Sheep.fleeceColors[sheep.getFleeceColor()][2]
        );
        return layer == 0 && !sheep.getSheared();
    }
}
