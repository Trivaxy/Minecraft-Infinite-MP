package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.BipedalModel;
import net.minecraft.game.entity.mob.monster.boss.Priest;
import org.lwjgl.opengl.GL11;

public class PriestRenderer extends MobRenderer<Priest> {
    private BipedalModel model = (BipedalModel)this.mainModel;
    private BipedalModel overlayModel = new BipedalModel(0.5F);

    public PriestRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    public void render(Priest entity, double x, double y, double z, float offset, float tick) {
        this.model.swingMain = this.getSwing(entity, tick);
        this.model.swingOff = this.getSwingOffhand(entity, tick);
        this.overlayModel.heldItemRight = entity.heldItem != null;
        super.render(entity, x, y - entity.yOffset, z, offset, tick);
    }

    protected void preRender(Priest entity, float tick) {
        GL11.glScalef(1.0F, 1.0F, 1.0F);
    }

    protected boolean renderOverlay(Priest entity, int layer, float tick) {
        this.loadTexture("/mob/pigman/priest_robes.png");
        this.setOverlay(this.overlayModel);
        return true;
    }
}
