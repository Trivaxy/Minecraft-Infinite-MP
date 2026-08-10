package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class TurtleModel extends QuadrupedModel {
    private final ModelPart tail;
    private final ModelPart tailEnd;

    public TurtleModel() {
        super(12, 0.0F);
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.rotate = false;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 18.0F, -5.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 5, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 17.5F, 1.0F);
        this.body.cuboids.add(new Cuboid(this.body, 12, 9, -6.0F, -3.5F, -6.0F, 12, 7, 12, 0.0F, false));
        this.body.cuboids.add(new Cuboid(this.body, 12, 28, -6.5F, -0.5F, -6.5F, 13, 2, 13, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(3.5F, 21.0F, -3.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 60, 22, -1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(-3.5F, 21.0F, -3.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 64, 37, -1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F, false));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(3.5F, 21.0F, 5.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 0, 22, -1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F, false));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(-3.5F, 21.0F, 5.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 0, 37, -1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 17.5F, 6.75F);
        this.tailEnd = new ModelPart(this);
        this.tailEnd.setPivots(0.0F, 0.0F, 0.0F);
        this.tail.addChild(this.tailEnd);
        this.setRotation(this.tailEnd, 0.8727F, 0.0F, 0.0F);
        this.tailEnd.cuboids.add(new Cuboid(this.tailEnd, 76, 39, -1.0F, -0.1299F, -0.4563F, 2, 3, 1, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        super.render(entity, swing, anim, angle, headYaw, headPitch, tick);
        this.tail.render(tick);
    }
}
