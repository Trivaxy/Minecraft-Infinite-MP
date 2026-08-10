package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.QuadrupedModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.cow.Cow;
import org.lwjgl.opengl.GL11;

public class CowRenderer extends MobRenderer<Cow> {
    public CowRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected void renderEquippedItems(Cow cow, float tick) {
        if (cow.getShearedDrop() > 0) {
            Block mushroom = BlockList.blocks[cow.getShearedDrop()];
            this.loadTexture("/terrain.png");
            GL11.glEnable(2884);
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, -0.7F, 0.7F);
            GL11.glTranslatef(0.2F, 0.3F, 0.5F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            BlocksRenderer.plantModel.renderInGUI(mushroom, 0);
            GL11.glTranslatef(0.1F, 0.0F, -0.6F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            BlocksRenderer.plantModel.renderInGUI(mushroom, 0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ((QuadrupedModel)this.mainModel).head.applyRotation(0.0625F);
            GL11.glScalef(0.7F, -0.7F, 0.7F);
            GL11.glTranslatef(0.0F, 0.75F, -0.2F);
            GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
            BlocksRenderer.plantModel.renderInGUI(mushroom, 0);
            GL11.glPopMatrix();
            GL11.glDisable(2884);
        }
    }
}
