package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class StatueModel extends Model {
    private final ModelPart statue;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart pedestal;
    private final ModelPart head;
    private final ModelPart headwear;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart base;

    public StatueModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.statue = new ModelPart(this);
        this.statue.setPivots(0.0F, -6.0F, 0.0F);
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(-1.9F, 12.0F, 0.0F);
        this.statue.addChild(this.rightLeg);
        this.setRotation(this.rightLeg, 0.0F, 0.0F, 0.0873F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 0, 16, -2.1F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(1.9F, 12.0F, 0.0F);
        this.statue.addChild(this.leftLeg);
        this.setRotation(this.leftLeg, 0.0F, 0.0F, -0.0873F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 0, 16, -1.9F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 12.0F, 0.0F);
        this.statue.addChild(this.body);
        this.body.cuboids.add(new Cuboid(this.body, 16, 16, -4.0F, -12.0F, -2.0F, 8, 12, 4, 0.0F, false));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.rightWing);
        this.setRotation(this.rightWing, -0.1309F, 0.0F, 0.0F);
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 5, 52, -1.0F, -13.0F, 0.0F, 2, 10, 2, 0.0F, false));
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.leftWing);
        this.setRotation(this.leftWing, 0.0F, 0.0F, 0.2618F);
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 55, -19.0F, -11.0F, 2.0F, 1, 4, 1, 0.0F, false));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 55, -18.0F, -13.0F, 2.0F, 1, 8, 1, 0.0F, false));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 55, -15.0F, -4.0F, 2.0F, 5, 1, 1, 0.0F, false));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 55, -17.0F, -12.0F, 2.0F, 9, 8, 1, 0.0F, false));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 55, -8.0F, -11.0F, 2.0F, 5, 6, 1, 0.0F, false));
        this.pedestal = new ModelPart(this);
        this.pedestal.setPivots(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.pedestal);
        this.setRotation(this.pedestal, 0.0F, 0.0F, -0.2618F);
        this.pedestal.cuboids.add(new Cuboid(this.pedestal, 0, 55, 18.0F, -11.0F, 2.0F, 1, 4, 1, 0.0F, false));
        this.pedestal.cuboids.add(new Cuboid(this.pedestal, 0, 55, 17.0F, -13.0F, 2.0F, 1, 8, 1, 0.0F, false));
        this.pedestal.cuboids.add(new Cuboid(this.pedestal, 0, 55, 10.0F, -4.0F, 2.0F, 5, 1, 1, 0.0F, false));
        this.pedestal.cuboids.add(new Cuboid(this.pedestal, 0, 55, 3.0F, -11.0F, 2.0F, 5, 6, 1, 0.0F, false));
        this.pedestal.cuboids.add(new Cuboid(this.pedestal, 0, 55, 8.0F, -12.0F, 2.0F, 9, 8, 1, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -11.0F, -1.0F);
        this.body.addChild(this.head);
        this.setRotation(this.head, 0.3491F, 0.0F, 0.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
        this.headwear = new ModelPart(this);
        this.headwear.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.headwear);
        this.headwear.cuboids.add(new Cuboid(this.headwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(5.0F, -10.0F, 0.0F);
        this.body.addChild(this.leftArm);
        this.setRotation(this.leftArm, -1.199F, 0.3743F, 0.122F);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(-5.0F, -10.0F, 0.0F);
        this.body.addChild(this.rightArm);
        this.setRotation(this.rightArm, -1.1889F, -0.4084F, -0.1582F);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.base = new ModelPart(this);
        this.base.setPivots(0.0F, 24.0F, 0.0F);
        this.base.cuboids.add(new Cuboid(this.base, 0, 45, -7.0F, -5.0F, -7.0F, 14, 5, 14, 0.0F, false));
        this.base.cuboids.add(new Cuboid(this.base, 0, 45, -8.0F, -6.0F, -8.0F, 16, 3, 16, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.statue.render(tick);
        this.base.render(tick);
    }
}
