package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class OstrichModel extends Model {
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart neck;
    private final ModelPart neckPart;
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public OstrichModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 8.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 0, 0, -4.0F, -8.0F, -8.0F, 8, 9, 12, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.setRotation(this.torso, -0.2182F, 0.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 34, 34, -3.0F, -12.0F, 2.0F, 6, 11, 2, 0.0F, false));
        this.rightWing = new ModelPart(this, 18, 21);
        this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 7, 8, 0.0F, false);
        this.rightWing.setPivots(-5.0F, -7.0F, -3.0F);
        this.body.addChild(this.rightWing);
        this.leftWing = new ModelPart(this, 0, 21);
        this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 7, 8, 0.0F, false);
        this.leftWing.setPivots(5.0F, -7.0F, -3.0F);
        this.body.addChild(this.leftWing);
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, -1.0F, -5.0F);
        this.body.addChild(this.neck);
        this.neckPart = new ModelPart(this);
        this.neckPart.setPivots(0.0F, -7.0F, -2.0F);
        this.neck.addChild(this.neckPart);
        this.setRotation(this.neckPart, 0.2618F, 0.0F, 0.0F);
        this.neckPart.cuboids.add(new Cuboid(this.neckPart, 0, 36, -1.0F, -6.8296F, -2.7059F, 2, 13, 3, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -12.0F, -4.0F);
        this.neck.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 28, 0, -2.5F, -5.0F, -5.0F, 5, 5, 6, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 4, -1.5F, -3.0F, -6.0F, 3, 1, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -1.5F, -2.0F, -8.0F, 3, 1, 3, 0.0F, false));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(2.0F, 9.0F, -1.0F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 20, 36, -1.0F, 0.0F, -3.0F, 2, 15, 3, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(-2.0F, 9.0F, -1.0F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 10, 36, -1.0F, 0.0F, -3.0F, 2, 15, 3, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float animSpd, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, animSpd, angle, headYaw, headPitch, tick);
        this.body.render(tick);
        this.leftLeg.render(tick);
        this.rightLeg.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = this.neck.rotationX = MathHelper.clamp(headPitch / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.head.rotationY = this.neck.rotationY = MathHelper.clamp(headYaw / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.rightWing.rotationZ = angle;
        this.leftWing.rotationZ = -angle;
    }
}
