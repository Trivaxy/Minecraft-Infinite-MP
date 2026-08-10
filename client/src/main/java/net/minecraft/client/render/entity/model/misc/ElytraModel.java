package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;

public class ElytraModel extends Model {
    private final ModelPart rightWing;
    private final ModelPart leftWing = new ModelPart(this, 22, 0);

    public ElytraModel() {
        this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F, false);
        this.rightWing = new ModelPart(this, 22, 0);
        this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F, true);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.leftWing.render(scale);
        this.rightWing.render(scale);
    }

    public void animateWings(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float rotX = (float) (Math.PI / 12);
        float rotY = (float) (-Math.PI / 12);
        float pivotY = 0.0F;
        float rotZ = 0.0F;
        if (entityIn instanceof Mob && ((Mob)entityIn).isGliding()) {
            float offset = 1.0F;
            if (entityIn.motionY < 0.0) {
                Vector vec3d = Vector.createVector(entityIn.motionX, entityIn.motionY, entityIn.motionZ).normalize();
                offset = 1.0F - (float)Math.pow(-vec3d.yCoord, 1.5);
            }

            rotX = offset * (float) (Math.PI / 9) + (1.0F - offset) * rotX;
            rotY = offset * (float) (-Math.PI / 2) + (1.0F - offset) * rotY;
        } else if (entityIn.sneaking) {
            rotX = (float) Math.PI * 2.0F / 9.0F;
            rotY = (float) (-Math.PI / 4);
            pivotY = 0.0F;
            rotZ = 0.08726646F;
        }

        this.leftWing.pivotX = 5.0F;
        this.leftWing.pivotY = pivotY;
        if (entityIn instanceof Player) {
            Player player = (Player)entityIn;
            player.rotateElytraX = player.rotateElytraX + (rotX - player.rotateElytraX) * 0.1F;
            player.rotateElytraY = player.rotateElytraY + (rotZ - player.rotateElytraY) * 0.1F;
            player.rotateElytraZ = player.rotateElytraZ + (rotY - player.rotateElytraZ) * 0.1F;
            this.leftWing.rotationX = player.rotateElytraX;
            this.leftWing.rotationY = player.rotateElytraY;
            this.leftWing.rotationZ = player.rotateElytraZ;
            if (player.isGliding() || player.crawling) {
                this.leftWing.rotationY += 90.0F;
            }
        } else {
            this.leftWing.rotationX = rotX;
            this.leftWing.rotationZ = rotY;
            this.leftWing.rotationY = rotZ;
        }

        this.rightWing.pivotX = -this.leftWing.pivotX;
        this.rightWing.rotationY = -this.leftWing.rotationY;
        this.rightWing.pivotY = this.leftWing.pivotY;
        this.rightWing.rotationX = this.leftWing.rotationX;
        this.rightWing.rotationZ = -this.leftWing.rotationZ;
    }
}
