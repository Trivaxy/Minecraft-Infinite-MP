package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.SkeletonModel;
import net.minecraft.game.entity.mob.monster.skeleton.Bogged;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.skeleton.Stray;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import org.lwjgl.opengl.GL11;

public class SkeletonRenderer extends MobRenderer<Skeleton> {
    private SkeletonModel model = (SkeletonModel)this.mainModel;
    private SkeletonModel overlayModel = new SkeletonModel(1.0F);
    private SkeletonModel armorModel = new SkeletonModel(0.5F);

    public SkeletonRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    public void render(Skeleton entity, double x, double y, double z, float offset, float tick) {
        this.overlayModel.riding = this.armorModel.riding = this.model.riding = entity.isRiding();
        super.render(entity, x, y, z, offset, tick);
    }

    protected boolean renderOverlay(Skeleton entity, int layer, float tick) {
        if (entity instanceof Bogged) {
            this.loadTexture("/mob/skeleton/bogged_robe.png");
            this.enableOverlays(this.overlayModel);
            this.setOverlay(this.overlayModel);
            return true;
        }

        if (entity instanceof Stray) {
            this.loadTexture("/mob/skeleton/lich_robe.png");
            this.enableOverlays(this.overlayModel);
            this.setOverlay(this.overlayModel);
            return true;
        }

        int slot = 3 - layer;
        ItemStack stack = entity.armorInventory[slot];
        if (stack == null) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof Armor) {
            Armor armorPiece = (Armor)item;
            this.loadTexture("/armor/" + Armor.armors[armorPiece.renderIndex] + "_" + (layer == 2 ? 2 : 1) + ".png");
            SkeletonModel model = layer == 2 ? this.armorModel : this.overlayModel;
            model.head.render = layer == 0;
            model.hair.render = layer == 0;
            model.body.render = layer == 1 || layer == 2;
            model.rightArm.render = layer == 1;
            model.leftArm.render = layer == 1;
            model.rightLeg.render = layer == 2 || layer == 3;
            model.leftLeg.render = layer == 2 || layer == 3;
            this.setOverlay(model);
            if (armorPiece.tier == ArmorMaterial.CLOTH) {
                int armorColor = armorPiece.getColor(stack);
                float red = (armorColor >> 16 & 0xFF) / 255.0F;
                float green = (armorColor >> 8 & 0xFF) / 255.0F;
                float blue = (armorColor & 0xFF) / 255.0F;
                GL11.glColor3f(red, green, blue);
            }
        }

        return true;
    }

    private void enableOverlays(SkeletonModel model) {
        model.head.render = true;
        model.hair.render = true;
        model.body.render = true;
        model.rightArm.render = true;
        model.leftArm.render = true;
        model.rightLeg.render = true;
        model.leftLeg.render = true;
    }
}
