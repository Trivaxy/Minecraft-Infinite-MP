package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.client.render.entity.model.bipedal.MonkeyModel;
import net.minecraft.game.entity.mob.animal.Monkey;
import org.lwjgl.opengl.GL11;

public final class MonkeyRenderer extends MobRenderer<Monkey> {
    private MonkeyModel modelMain = (MonkeyModel)this.mainModel;

    public MonkeyRenderer(Model modelBase, float shadowSize) {
        super(modelBase, shadowSize);
    }

    protected void renderEquippedItems(Monkey monke, float tick) {
        if (monke.item != null) {
            GL11.glPushMatrix();
            this.postRender(this.modelMain.rightArm, 0.0625F);
            GL11.glTranslatef(-0.26F, 1.35F, -0.15F);
            float scale = 0.25F;
            GL11.glTranslatef(0.1F, 0.15F, 0.3F);
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            this.renderManager.itemRenderer.renderItemModel(monke.item);
            GL11.glPopMatrix();
        }
    }

    public void postRender(ModelPart model, float deltaTime) {
        if (model.rotationX != 0.0F || model.rotationY != 0.0F || model.rotationZ != 0.0F) {
            GL11.glTranslatef(model.pivotX * deltaTime, model.pivotY * deltaTime, model.pivotZ * deltaTime);
            if (model.rotationX != 0.0F) {
                GL11.glRotatef(model.rotationX * 8.594367F, 0.0F, 0.0F, 1.0F);
            }

            if (model.rotationY != 0.0F) {
                GL11.glRotatef(model.rotationY * 8.594367F, 0.0F, 1.0F, 0.0F);
            }

            if (model.rotationZ != 0.0F) {
                GL11.glRotatef(model.rotationZ * 8.594367F, 1.0F, 0.0F, 0.0F);
            }
        } else if (model.pivotX != 0.0F || model.pivotY != 0.0F || model.pivotZ != 0.0F) {
            GL11.glTranslatef(model.pivotZ * deltaTime, model.pivotY * deltaTime, model.pivotX * deltaTime);
        }
    }
}
