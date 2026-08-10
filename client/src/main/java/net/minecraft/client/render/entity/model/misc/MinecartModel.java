package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class MinecartModel extends Model {
    public ModelPart[] sides = new ModelPart[7];

    public MinecartModel() {
        this.sides[0] = new ModelPart(this, 0, 10);
        this.sides[1] = new ModelPart(this, 0, 0);
        this.sides[2] = new ModelPart(this, 0, 0);
        this.sides[3] = new ModelPart(this, 0, 0);
        this.sides[4] = new ModelPart(this, 0, 0);
        this.sides[5] = new ModelPart(this, 44, 10);
        byte pivotX = 20;
        byte pivotY = 8;
        byte pivotZ = 16;
        byte offset = 4;
        this.sides[0].addBox(-pivotX / 2, -pivotZ / 2, -1.0F, pivotX, pivotZ, 2, 0.0F, false);
        this.sides[0].setPivots(0.0F, 0 + offset, 0.0F);
        this.sides[5].addBox(-pivotX / 2 + 1, -pivotZ / 2 + 1, -1.0F, pivotX - 2, pivotZ - 2, 1, 0.0F, false);
        this.sides[5].setPivots(0.0F, 0 + offset, 0.0F);
        this.sides[1].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.sides[1].setPivots(-pivotX / 2 + 1, 0 + offset, 0.0F);
        this.sides[2].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.sides[2].setPivots(pivotX / 2 - 1, 0 + offset, 0.0F);
        this.sides[3].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.sides[3].setPivots(0.0F, 0 + offset, -pivotZ / 2 + 1);
        this.sides[4].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.sides[4].setPivots(0.0F, 0 + offset, pivotZ / 2 - 1);
        this.sides[0].rotationX = (float) (Math.PI / 2);
        this.sides[1].rotationY = (float) (Math.PI * 3.0 / 2.0);
        this.sides[2].rotationY = (float) (Math.PI / 2);
        this.sides[3].rotationY = (float) Math.PI;
        this.sides[5].rotationX = (float) (-Math.PI / 2);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.sides[5].pivotY = 4.0F - angle;

        for (int side = 0; side < 6; side++) {
            this.sides[side].render(tick);
        }
    }
}
