package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.ModelPart;

public class BearModel extends QuadrupedModel {
    public BearModel() {
        super(12, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 10.0F, -12.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 34, -3.5F, -5.0F, -7.0F, 7, 7, 7, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 36, 0, -2.5F, -2.0F, -9.0F, 5, 4, 2, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, 1.5F, -7.0F, -5.0F, 3, 3, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -4.5F, -7.0F, -5.0F, 3, 3, 1, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(-6.0F, 14.0F, -12.0F);
        this.body.cuboids.add(new Cuboid(this.body, 0, 0, 0.0F, 0.0F, 0.0F, 12, 22, 12, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(3.5F, 14.0F, 6.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 28, 34, -2.0F, 0.0F, -2.0F, 4, 10, 5, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-3.5F, 14.0F, 6.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 28, 34, -2.0F, 0.0F, -2.0F, 4, 10, 5, 0.0F, false));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(3.5F, 14.0F, -8.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 28, 34, -2.0F, 0.0F, -2.0F, 4, 10, 5, 0.0F, false));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(-3.5F, 14.0F, -8.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 28, 34, -2.0F, 0.0F, -2.0F, 4, 10, 5, 0.0F, false));
    }
}
