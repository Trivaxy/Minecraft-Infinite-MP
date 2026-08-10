package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.SkeletonModel;
import net.minecraft.game.entity.mob.monster.boss.LichKing;
import net.minecraft.game.entity.mob.monster.skeleton.Lich;
import org.lwjgl.opengl.GL11;

public class LichRenderer extends MobRenderer<Lich> {
    private SkeletonModel overlayModel = new SkeletonModel(0.5F);

    public LichRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected void preRender(Lich entity, float tick) {
        GL11.glScalef(1.2F, 1.2F, 1.2F);
    }

    protected boolean renderOverlay(Lich entity, int layer, float tick) {
        if (entity instanceof LichKing) {
            this.loadTexture("/mob/skeleton/lich_king.png");
        } else {
            this.loadTexture("/mob/skeleton/lich_armor.png");
        }

        this.setOverlay(this.overlayModel);
        return true;
    }
}
