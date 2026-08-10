package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class MooseModel extends QuadrupedModel {
    private final ModelPart neck;
    private final ModelPart tail;

    public MooseModel() {
        super(12, 0.0F);
        this.textureWidth = 180;
        this.textureHeight = 180;
        this.rotate = false;
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, -1.0F, 2.0F);
        this.body.cuboids.add(new Cuboid(this.body, 0, 0, -8.0F, -15.0F, -22.0F, 16, 15, 43, 0.0F, false));
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, -6.0F, -10.0F);
        this.body.addChild(this.neck);
        this.setRotation(this.neck, -0.3927F, 0.0F, 0.0F);
        this.neck.cuboids.add(new Cuboid(this.neck, 0, 61, -5.0F, -11.0F, -20.0F, 10, 13, 21, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -17.0F, -25.0F);
        this.body.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 121, 0, -3.0F, -5.0F, -20.0F, 6, 9, 12, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 3, 95, -4.0F, -7.0F, -11.0F, 8, 12, 12, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 62, 61, 0.0F, -14.1F, -10.0F, 26, 7, 10, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 62, 78, -26.0F, -14.1F, -10.0F, 26, 7, 10, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 127, 95, -3.0F, 4.0F, -7.0F, 6, 3, 4, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(-5.0F, -4.0F, -11.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 118, 21, -4.0F, 0.0F, -4.0F, 7, 28, 9, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(4.0F, -4.0F, -10.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 118, 21, -2.0F, 0.0F, -5.0F, 7, 28, 9, 0.0F, true));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(5.0F, -6.0F, 15.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 88, 95, -3.0F, 0.0F, -5.0F, 8, 30, 10, 0.0F, true));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(-5.0F, -6.0F, 15.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 88, 95, -5.0F, 0.0F, -5.0F, 8, 30, 10, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, -9.0F, 22.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 32, 119, -2.0F, -1.0F, 0.0F, 4, 24, 5, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.body.render(tick);
        this.frontRightLeg.render(tick);
        this.frontLeftLeg.render(tick);
        this.backLeftLeg.render(tick);
        this.backRightLeg.render(tick);
        this.tail.render(tick);
    }
}
