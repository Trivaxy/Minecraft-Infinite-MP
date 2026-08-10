package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.ZombieModel;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import org.lwjgl.opengl.GL11;

public class ZombieRenderer extends MobRenderer<Zombie> {
    private ZombieModel model = (ZombieModel)this.mainModel;
    private ZombieModel armorModel = new ZombieModel(1.0F);
    private ZombieModel legsModel = new ZombieModel(0.5F);

    public ZombieRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    public void render(Zombie entity, double x, double y, double z, float offset, float tick) {
        this.armorModel.riding = this.legsModel.riding = this.model.riding = entity.isRiding();
        super.render(entity, x, y, z, offset, tick);
    }

    protected boolean renderOverlay(Zombie entity, int layer, float tick) {
        int slot = 3 - layer;
        ItemStack stack = entity.armorInventory[slot];
        if (stack != null && stack.getItem() instanceof Armor) {
            Armor armorPiece = (Armor)stack.getItem();
            this.loadTexture("/armor/" + Armor.armors[armorPiece.renderIndex] + "_" + (layer == 2 ? 2 : 1) + ".png");
            ZombieModel model = layer == 2 ? this.legsModel : this.armorModel;
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
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
