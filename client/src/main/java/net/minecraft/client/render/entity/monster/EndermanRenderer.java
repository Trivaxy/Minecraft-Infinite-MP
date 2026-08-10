package net.minecraft.client.render.entity.monster;

import java.util.Random;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.bipedal.EndermanModel;
import net.minecraft.game.entity.mob.monster.Stalker;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class EndermanRenderer extends MobRenderer<Stalker> {
    private EndermanModel endermanModel = (EndermanModel)this.mainModel;
    private Random rnd = new Random();

    public EndermanRenderer() {
        super(new EndermanModel(), 0.5F);
        this.setOverlay(this.endermanModel);
    }

    public void render(Stalker entity, double x, double y, double z, float off, float tick) {
        this.endermanModel.isAttacking = entity.attackTarget != null;
        if (entity.attackTarget != null) {
            double offset = 0.02;
            x += this.rnd.nextGaussian() * offset;
            z += this.rnd.nextGaussian() * offset;
        }

        super.render(entity, x, y, z, off, tick);
    }

    protected boolean renderOverlay(Stalker entity, int layer, float tick) {
        if (layer != 0) {
            return false;
        }

        this.loadTexture("/mob/ghost/void.png");
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
        return true;
    }
}
