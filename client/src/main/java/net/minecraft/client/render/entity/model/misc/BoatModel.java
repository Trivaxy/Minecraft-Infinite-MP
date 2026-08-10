package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class BoatModel extends Model {
    public ModelPart[] faces = new ModelPart[5];

    public BoatModel() {
        this.faces[0] = new ModelPart(this, 0, 8);
        this.faces[1] = new ModelPart(this, 0, 0);
        this.faces[2] = new ModelPart(this, 0, 0);
        this.faces[3] = new ModelPart(this, 0, 0);
        this.faces[4] = new ModelPart(this, 0, 0);
        byte pivotX = 24;
        byte pivotY = 6;
        byte pivotZ = 20;
        byte pivotOff = 4;
        this.faces[0].addBox(-pivotX / 2, -pivotZ / 2 + 2, -3.0F, pivotX, pivotZ - 4, 4, 0.0F, false);
        this.faces[0].setPivots(0.0F, 0 + pivotOff, 0.0F);
        this.faces[1].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.faces[1].setPivots(-pivotX / 2 + 1, 0 + pivotOff, 0.0F);
        this.faces[2].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.faces[2].setPivots(pivotX / 2 - 1, 0 + pivotOff, 0.0F);
        this.faces[3].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.faces[3].setPivots(0.0F, 0 + pivotOff, -pivotZ / 2 + 1);
        this.faces[4].addBox(-pivotX / 2 + 2, -pivotY - 1, -1.0F, pivotX - 4, pivotY, 2, 0.0F, false);
        this.faces[4].setPivots(0.0F, 0 + pivotOff, pivotZ / 2 - 1);
        this.faces[0].rotationX = (float) (Math.PI / 2);
        this.faces[1].rotationY = (float) (Math.PI * 3.0 / 2.0);
        this.faces[2].rotationY = (float) (Math.PI / 2);
        this.faces[3].rotationY = (float) Math.PI;
    }

    @Override
    public void render(Entity e, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        for (int side = 0; side < 5; side++) {
            this.faces[side].render(tick);
        }
    }
}
