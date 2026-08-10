package net.minecraft.client.render.entity.model.elemental;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ElementalModel extends Model {
    protected ModelPart[] rods = new ModelPart[12];
    protected ModelPart head;

    public ElementalModel() {
        for (int rod = 0; rod < this.rods.length; rod++) {
            this.rods[rod] = new ModelPart(this, 0, 16);
            this.rods[rod].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F, false);
        }

        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F, false);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float rotation, float yaw, float pitch, float tick) {
        this.animate(swing, anim, rotation, yaw, pitch, tick);
        this.head.render(tick);

        for (int rod = 0; rod < this.rods.length; rod++) {
            this.rods[rod].render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float rot, float yaw, float pitch, float tick) {
        float newRot = rot * (float) Math.PI * -0.1F;

        for (int rod = 0; rod < 4; rod++) {
            this.rods[rod].pivotY = -2.0F + MathHelper.cos((rod * 2 + rot) * 0.25F);
            this.rods[rod].pivotX = MathHelper.cos(newRot) * 9.0F;
            this.rods[rod].pivotZ = MathHelper.sin(newRot) * 9.0F;
            newRot += (float) (Math.PI / 2);
        }

        newRot = (float) (Math.PI / 4) + rot * (float) Math.PI * 0.03F;

        for (int var11 = 4; var11 < 8; var11++) {
            this.rods[var11].pivotY = 2.0F + MathHelper.cos((var11 * 2 + rot) * 0.25F);
            this.rods[var11].pivotX = MathHelper.cos(newRot) * 7.0F;
            this.rods[var11].pivotZ = MathHelper.sin(newRot) * 7.0F;
            newRot += (float) (Math.PI / 2);
        }

        newRot = 0.47123894F + rot * (float) Math.PI * -0.05F;

        for (int var12 = 8; var12 < 12; var12++) {
            this.rods[var12].pivotY = 11.0F + MathHelper.cos((var12 * 1.5F + rot) * 0.5F);
            this.rods[var12].pivotX = MathHelper.cos(newRot) * 5.0F;
            this.rods[var12].pivotZ = MathHelper.sin(newRot) * 5.0F;
            newRot += (float) (Math.PI / 2);
        }

        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
    }
}
