package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.mounts.horse.Pegasus;

public class PegasusModel extends HorseModel {
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart horn = new ModelPart(this);

    public PegasusModel() {
        super(0.0F, 128);
        this.horn.setPivots(0.0F, 0.0F, 0.0F);
        this.setRotation(this.horn, -1.5F, 0.0F, 0.0F);
        this.headPart.addChild(this.horn);
        this.horn.cuboids.add(new Cuboid(this.horn, 15, 55, -0.5F, 2.0F, -9.0F, 1, 1, 5, 0.0F, false));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(5.0F, -7.0F, -10.0F);
        this.body.addChild(this.rightWing);
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 66, 17, 0.0F, -0.5F, -6.0F, 9, 1, 9, 0.0F, false));
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 68, 30, 9.0F, -0.5F, -7.0F, 14, 1, 9, 0.0F, false));
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 71, 43, 23.0F, -0.5F, -6.0F, 1, 1, 9, 0.0F, false));
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 66, 17, 0.0F, -1.0F, -3.0F, 9, 1, 9, 0.0F, false));
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 68, 30, 9.0F, -1.0F, -4.0F, 14, 1, 9, 0.0F, false));
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 71, 43, 23.0F, -1.0F, -3.0F, 1, 1, 9, 0.0F, false));
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(-5.0F, -7.0F, -10.0F);
        this.body.addChild(this.leftWing);
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 66, 17, -9.0F, -0.5F, -6.0F, 9, 1, 9, 0.0F, true));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 68, 30, -23.0F, -0.5F, -7.0F, 14, 1, 9, 0.0F, true));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 71, 43, -24.0F, -0.5F, -6.0F, 1, 1, 9, 0.0F, true));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 66, 17, -9.0F, -1.5F, -3.0F, 9, 1, 9, 0.0F, true));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 68, 30, -23.0F, -1.5F, -4.0F, 14, 1, 9, 0.0F, true));
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 71, 43, -24.0F, -1.5F, -3.0F, 1, 1, 9, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        float wingBend = -((float)Math.acos(((Pegasus)entity).wingFold));
        if (entity.onGround) {
            this.rightWing.rotationX = 1.5F;
            this.leftWing.rotationX = 1.5F;
            this.rightWing.rotationY = 4.7F;
            this.leftWing.rotationY = 1.56F;
            this.rightWing.pivotZ = -15.0F;
            this.leftWing.pivotZ = -15.0F;
            this.rightWing.pivotY = -0.5F;
            this.leftWing.pivotY = -0.5F;
            this.rightWing.rotationZ = 0.0F;
            this.leftWing.rotationZ = 0.0F;
        } else {
            this.rightWing.rotationX = 0.0F;
            this.leftWing.rotationX = 0.0F;
            this.rightWing.rotationY = 0.0F;
            this.leftWing.rotationY = 0.0F;
            this.rightWing.pivotZ = -10.0F;
            this.leftWing.pivotZ = -10.0F;
            this.rightWing.pivotY = -5.0F;
            this.leftWing.pivotY = -5.0F;
            this.rightWing.rotationZ = ((Pegasus)entity).wingAngle + wingBend;
            this.leftWing.rotationZ = -(((Pegasus)entity).wingAngle + wingBend);
        }

        super.render(entity, swing, anim, angle, headYaw, headPitch, tick);
    }
}
