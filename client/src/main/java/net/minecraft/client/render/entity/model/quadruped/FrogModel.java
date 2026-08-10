package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class FrogModel extends Model {
    private final ModelPart mainBody;
    private final ModelPart head;
    private final ModelPart armLeft;
    private final ModelPart armRight;
    private final ModelPart legLeft;
    private final ModelPart legRight;

    public FrogModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.mainBody = new ModelPart(this);
        this.mainBody.setPivots(0.0F, 21.1377F, -0.5545F);
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.mainBody.addChild(this.head);
        this.setRotation(this.head, -0.3491F, 0.0F, 0.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -2.0F, -1.5F, -3.5F, 4, 3, 7, 0.0F, false));
        this.armLeft = new ModelPart(this);
        this.armLeft.setPivots(2.0F, 21.0F, 0.0F);
        this.armLeft.cuboids.add(new Cuboid(this.armLeft, 0, 10, -1.0F, 3.0F, -3.0F, 4, 0, 3, 0.0F, false));
        this.armLeft.cuboids.add(new Cuboid(this.armLeft, 0, 0, 0.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));
        this.armRight = new ModelPart(this);
        this.armRight.setPivots(-2.0F, 21.0F, 0.0F);
        this.armRight.cuboids.add(new Cuboid(this.armRight, 8, 10, -3.0F, 3.0F, -3.0F, 4, 0, 3, 0.0F, false));
        this.armRight.cuboids.add(new Cuboid(this.armRight, 3, 3, -1.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));
        this.legLeft = new ModelPart(this);
        this.legLeft.setPivots(2.0F, 22.0F, 2.0F);
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, 12, 0, 1.0F, 2.0F, -2.0F, 3, 0, 3, 0.0F, false));
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, 8, 13, 0.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F, false));
        this.legRight = new ModelPart(this);
        this.legRight.setPivots(-2.0F, 22.0F, 2.0F);
        this.legRight.cuboids.add(new Cuboid(this.legRight, 12, 3, -4.0F, 2.0F, -2.0F, 3, 0, 3, 0.0F, false));
        this.legRight.cuboids.add(new Cuboid(this.legRight, 0, 13, -2.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.mainBody.render(tick);
        this.armLeft.render(tick);
        this.armRight.render(tick);
        this.legLeft.render(tick);
        this.legRight.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.mainBody.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 0.575F * anim;
        this.armLeft.rotationX = this.armRight.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
        this.legLeft.rotationX = this.legRight.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
    }
}
