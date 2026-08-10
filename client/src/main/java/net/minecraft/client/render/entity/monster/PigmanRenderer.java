package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.BipedalModel;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class PigmanRenderer extends MobRenderer<Pigman> {
    private BipedalModel model = (BipedalModel)this.mainModel;
    private BipedalModel overlayModel = new BipedalModel(0.5F);

    public PigmanRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    public void render(Pigman entity, double x, double y, double z, float offset, float tick) {
        this.model.swingMain = this.overlayModel.swingMain = this.getSwing(entity, tick);
        this.model.swingOff = this.overlayModel.swingOff = this.getSwingOffhand(entity, tick);
        this.model.leftHanded = this.overlayModel.leftHanded = entity.isSwingingOffhand;
        this.model.heldItemLeft = this.overlayModel.heldItemLeft = entity.item != null;
        this.model.heldItemRight = this.overlayModel.heldItemRight = true;
        super.render(entity, x, y - entity.yOffset, z, offset, tick);
    }

    protected boolean renderOverlay(Pigman entity, int layer, float tick) {
        this.loadTexture("/mob/pigman/valkyrie_armor.png");
        this.setOverlay(this.overlayModel);
        return true;
    }

    protected void renderEquippedItems(Pigman entity, float tick) {
        GL11.glPushMatrix();
        this.model.rightArm.applyRotation(0.0625F);
        if (entity.type == 1) {
            GL11.glTranslatef(0.3F, 0.35F, -0.45F);
        } else {
            GL11.glTranslatef(-0.0625F, 0.4375F, -0.3625F);
        }

        float scale = 0.55F;
        GL11.glTranslatef(0.0F, 0.15F, 0.3F);
        GL11.glScalef(scale, -scale, scale);
        if (entity.type == 1) {
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            this.renderManager.itemRenderer.renderItemModel(new ItemStack(ItemList.crossbow));
        } else {
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            this.renderManager.itemRenderer.renderItemModel(new ItemStack(ItemList.swordValkyrie));
        }

        GL11.glPopMatrix();
        if (entity.item != null) {
            GL11.glPushMatrix();
            this.model.leftArm.applyRotation(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, -0.3625F);
            GL11.glTranslatef(-0.1F, 0.2F, 0.3F);
            scale = 0.35F;
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(-120.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(145.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
            this.renderManager.itemRenderer.renderItemModel(entity.item);
            GL11.glPopMatrix();
        }
    }
}
