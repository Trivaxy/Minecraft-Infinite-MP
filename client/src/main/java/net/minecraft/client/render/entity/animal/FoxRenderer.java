package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.FoxModel;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import org.lwjgl.opengl.GL11;

public final class FoxRenderer extends MobRenderer<Fox> {
    private FoxModel foxModel = (FoxModel)this.mainModel;
    private BlocksRenderer blockRenderer = new BlocksRenderer();

    public FoxRenderer(Model modelBase, float shadowSize) {
        super(modelBase, shadowSize);
    }

    public void render(Fox fox, double x, double y, double z, float offset, float tick) {
        this.foxModel.isSleeping = fox.sleeping;
        this.foxModel.isSitting = fox.isSitting();
        switch (fox.type) {
            case 1:
                fox.texture = "/mob/fox/snow_fox" + (fox.sleeping ? "_sleeping.png" : ".png");
                break;
            case 2:
                fox.texture = "/mob/fox/desert_fox" + (fox.sleeping ? "_sleeping.png" : ".png");
                break;
            default:
                fox.texture = "/mob/fox/fox" + (fox.sleeping ? "_sleeping.png" : ".png");
        }

        super.render(fox, x, y, z, offset, tick);
    }

    protected void renderEquippedItems(Fox entityFox, float partialTick) {
        if (entityFox.heldItem != null) {
            GL11.glPushMatrix();
            this.foxModel.head.applyRotation(0.0625F);
            GL11.glTranslatef(0.0F, 0.0F, -0.3F);
            float scale = 0.3F;
            if (entityFox.heldItem.itemID < 4096 && this.blockRenderer.isModel3D(BlockList.blocks[entityFox.heldItem.itemID].getRenderType())) {
                GL11.glScalef(scale / 1.5F, -scale / 1.5F, scale / 1.5F);
                GL11.glTranslatef(0.3F, -1.5F, -0.6F);
            } else {
                GL11.glTranslatef(-0.15F, 0.2F, -0.3F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }

            this.renderManager.itemRenderer.renderItemModel(entityFox.heldItem);
            GL11.glPopMatrix();
        }
    }
}
