package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.Goat;
import net.minecraft.util.math.MathHelper;

public class GoatModel extends Model {
    private final ModelPart neck;
    private final ModelPart neckPart;
    private final ModelPart head;
    private final ModelPart hornRight;
    private final ModelPart hornLeft;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart jawBack;
    private final ModelPart headPart;
    private final ModelPart mouth;
    private final ModelPart beard;
    private final ModelPart mouthPart;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart hindRightLeg;
    private final ModelPart hindLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private boolean attacking;

    public GoatModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, 10.5F, -7.0F);
        this.neckPart = new ModelPart(this);
        this.neckPart.setPivots(0.0F, -3.3285F, -3.527F);
        this.neck.addChild(this.neckPart);
        this.setRotation(this.neckPart, -0.6109F, 0.0F, 0.0F);
        this.neckPart.cuboids.add(new Cuboid(this.neckPart, 0, 16, -1.5F, -2.5F, -4.0F, 3, 5, 8, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -4.9686F, -7.6313F);
        this.neck.addChild(this.head);
        this.setRotation(this.head, 0.0873F, 0.0F, 0.0F);
        this.hornRight = new ModelPart(this);
        this.hornRight.setPivots(-1.4F, -4.5574F, 0.3149F);
        this.head.addChild(this.hornRight);
        this.setRotation(this.hornRight, -0.2182F, 0.0F, 0.0F);
        this.hornRight.cuboids.add(new Cuboid(this.hornRight, 50, 10, -0.5F, -3.5186F, -0.5806F, 1, 4, 1, 0.0F, false));
        this.hornLeft = new ModelPart(this);
        this.hornLeft.setPivots(1.4F, -4.6324F, 0.4127F);
        this.head.addChild(this.hornLeft);
        this.setRotation(this.hornLeft, -0.2182F, 0.0F, 0.0F);
        this.hornLeft.cuboids.add(new Cuboid(this.hornLeft, 50, 0, -0.5F, -3.5186F, -0.5806F, 1, 4, 1, 0.0F, false));
        this.earRight = new ModelPart(this);
        this.earRight.setPivots(-3.0436F, -3.6463F, 0.1871F);
        this.head.addChild(this.earRight);
        this.setRotation(this.earRight, -1.0908F, -0.0873F, 0.1745F);
        this.earRight.cuboids.add(new Cuboid(this.earRight, 50, 10, -0.0669F, -1.4473F, -0.8069F, 1, 3, 6, 0.0F, false));
        this.earLeft = new ModelPart(this);
        this.earLeft.setPivots(3.0436F, -3.8963F, 0.1871F);
        this.head.addChild(this.earLeft);
        this.setRotation(this.earLeft, -1.0908F, 0.0873F, -0.1745F);
        this.earLeft.cuboids.add(new Cuboid(this.earLeft, 50, 0, -0.9764F, -1.3303F, -0.5903F, 1, 3, 6, 0.0F, false));
        this.jawBack = new ModelPart(this);
        this.jawBack.setPivots(0.0F, 0.2441F, 2.1168F);
        this.head.addChild(this.jawBack);
        this.setRotation(this.jawBack, -0.9163F, 0.0F, 0.0F);
        this.jawBack.cuboids.add(new Cuboid(this.jawBack, 17, 0, -2.0F, -2.9077F, -1.2011F, 4, 6, 1, 0.0F, false));
        this.headPart = new ModelPart(this);
        this.headPart.setPivots(0.0F, -0.7173F, 1.1115F);
        this.head.addChild(this.headPart);
        this.setRotation(this.headPart, -0.9163F, 0.0F, 0.0F);
        this.headPart.cuboids.add(new Cuboid(this.headPart, 0, 0, -2.0F, -3.1199F, -3.8263F, 4, 11, 4, 0.0F, false));
        this.mouth = new ModelPart(this);
        this.mouth.setPivots(0.0F, 1.8021F, -0.3487F);
        this.head.addChild(this.mouth);
        this.beard = new ModelPart(this);
        this.beard.setPivots(0.0F, 2.5098F, -2.5021F);
        this.mouth.addChild(this.beard);
        this.setRotation(this.beard, -1.1781F, 0.0F, 0.0F);
        this.beard.cuboids.add(new Cuboid(this.beard, 39, 0, -1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F, false));
        this.mouthPart = new ModelPart(this);
        this.mouthPart.setPivots(0.0F, -0.0142F, -0.0013F);
        this.mouth.addChild(this.mouthPart);
        this.setRotation(this.mouthPart, -0.9163F, 0.0F, 0.0F);
        this.mouthPart.cuboids.add(new Cuboid(this.mouthPart, 28, 0, -2.0F, -0.8045F, -0.9242F, 4, 6, 1, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 11.5322F, -0.0682F);
        this.body.cuboids.add(new Cuboid(this.body, 6, 10, -4.0F, -4.5322F, -9.9318F, 8, 9, 20, 0.0F, false));
        this.body.cuboids.add(new Cuboid(this.body, 31, 41, -2.5F, 4.4678F, 2.0682F, 5, 1, 6, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, -3.3749F, 9.4859F);
        this.body.addChild(this.tail);
        this.setRotation(this.tail, 0.3491F, 0.0F, 0.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 54, 41, -2.0F, -0.7786F, -0.2443F, 4, 6, 1, 0.0F, false));
        this.hindRightLeg = new ModelPart(this);
        this.hindRightLeg.setPivots(2.5F, 16.0F, -7.5F);
        this.hindRightLeg.cuboids.add(new Cuboid(this.hindRightLeg, 0, 41, -1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F, false));
        this.hindLeftLeg = new ModelPart(this);
        this.hindLeftLeg.setPivots(-2.5F, 16.0F, -7.5F);
        this.hindLeftLeg.cuboids.add(new Cuboid(this.hindLeftLeg, 13, 41, -1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(2.5F, 16.0F, 7.5F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 0, 53, -1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-2.5F, 16.0F, 7.5F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 13, 53, -1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float rotAngle, float headYaw, float headPitch, float tick) {
        this.attacking = ((Goat)entity).attackTarget != null && ((Goat)entity).navigator.pathToTarget(((Goat)entity).attackTarget) != null;
        this.animate(swing, anim, rotAngle, headYaw, headPitch, tick);
        this.neck.render(tick);
        this.body.render(tick);
        this.hindRightLeg.render(tick);
        this.hindLeftLeg.render(tick);
        this.frontRightLeg.render(tick);
        this.frontLeftLeg.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float rot, float headYaw, float headPitch, float tick) {
        if (this.attacking) {
            this.head.rotationX = this.neck.rotationX = 0.5F;
            this.head.rotationY = this.neck.rotationY = 0.0F;
        } else {
            this.head.rotationY = this.neck.rotationY = MathHelper.clamp(headYaw / (180.0F / (float)Math.PI), -0.5F, 0.5F);
            this.head.rotationX = this.neck.rotationX = MathHelper.clamp(headPitch / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        }

        this.hindRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.hindLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.4F * anim;
        this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.4F * anim;
        this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
