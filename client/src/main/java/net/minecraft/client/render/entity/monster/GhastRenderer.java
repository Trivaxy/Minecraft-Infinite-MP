package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.elemental.GhastModel;
import net.minecraft.game.entity.mob.monster.Ghast;
import org.lwjgl.opengl.GL11;

public class GhastRenderer extends MobRenderer<Ghast> {
    public GhastRenderer() {
        super(new GhastModel(), 0.5F);
    }

    protected void preRender(Ghast ghast, float tick) {
        float attackTimer = (ghast.prevAttackCounter + (ghast.attackCounter - ghast.prevAttackCounter) * tick) / 20.0F;
        if (attackTimer < 0.0F) {
            attackTimer = 0.0F;
        }

        attackTimer = 1.0F / (attackTimer * attackTimer * attackTimer * attackTimer * attackTimer * 2.0F + 1.0F);
        float scaleVertical = (8.0F + attackTimer) / 2.0F;
        float scaleHorizontal = (8.0F + 1.0F / attackTimer) / 2.0F;
        GL11.glScalef(scaleHorizontal, scaleVertical, scaleHorizontal);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
