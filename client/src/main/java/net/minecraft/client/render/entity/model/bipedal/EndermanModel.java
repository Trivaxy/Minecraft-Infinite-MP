package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class EndermanModel extends Model {
    private final ModelPart legLeft;
    private final ModelPart legRight;
    private final ModelPart torso;
    private final ModelPart armRight;
    private final ModelPart armLeft;
    private final ModelPart skull;
    private final ModelPart jaw;
    public boolean isAttacking = false;

    public EndermanModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.legLeft = new ModelPart(this);
        this.legLeft.setPivots(-3.25F, 1.0F, 0.0F);
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, 0, 44, -1.75F, 0.0F, -2.0F, 4, 23, 4, 0.0F, false));
        this.legRight = new ModelPart(this);
        this.legRight.setPivots(3.0F, 1.0F, 0.0F);
        this.legRight.cuboids.add(new Cuboid(this.legRight, 16, 44, -2.0F, 0.0F, -2.0F, 4, 23, 4, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 1.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -5.0F, -19.0F, -3.0F, 10, 19, 6, 0.0F, false));
        this.armRight = new ModelPart(this);
        this.armRight.setPivots(5.0F, -19.0F, 0.0F);
        this.torso.addChild(this.armRight);
        this.armRight.cuboids.add(new Cuboid(this.armRight, 40, 39, 0.0F, 0.0F, -2.0F, 4, 22, 4, 0.0F, false));
        this.armRight.cuboids.add(new Cuboid(this.armRight, 56, 12, -0.5F, 12.0F, -2.5F, 5, 11, 5, 0.0F, true));
        this.armLeft = new ModelPart(this);
        this.armLeft.setPivots(-5.0F, -19.0F, 0.0F);
        this.torso.addChild(this.armLeft);
        this.setRotation(this.armLeft, 0.0304F, 0.0035F, 0.0414F);
        this.armLeft.cuboids.add(new Cuboid(this.armLeft, 40, 12, -4.0F, 0.0F, -2.0F, 4, 22, 4, 0.0F, false));
        this.armLeft.cuboids.add(new Cuboid(this.armLeft, 56, 12, -4.5F, 12.0F, -2.5F, 5, 11, 5, 0.0F, false));
        this.skull = new ModelPart(this);
        this.skull.setPivots(0.0F, -19.0F, 0.0F);
        this.torso.addChild(this.skull);
        this.setRotation(this.skull, 0.0294F, 0.0389F, 0.0075F);
        this.skull.cuboids.add(new Cuboid(this.skull, 0, 26, -5.0F, -10.0F, -5.0F, 10, 8, 10, 0.0F, false));
        this.jaw = new ModelPart(this);
        this.jaw.setPivots(0.0F, -2.0F, 4.0F);
        this.skull.addChild(this.jaw);
        this.jaw.cuboids.add(new Cuboid(this.jaw, 32, 0, -5.0F, 0.0F, -9.0F, 10, 2, 10, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.legLeft.render(tick);
        this.legRight.render(tick);
        this.torso.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.skull.rotationY = yaw / (180.0F / (float)Math.PI);
        this.skull.rotationX = pitch / (180.0F / (float)Math.PI);
        this.armLeft.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 2.0F * anim * 0.5F;
        this.armRight.rotationX = MathHelper.cos(swing * 0.6662F) * 2.0F * anim * 0.5F;
        this.armLeft.rotationZ = 0.0F;
        this.armRight.rotationZ = 0.0F;
        this.legLeft.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.legRight.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        if (this.isAttacking) {
            this.jaw.rotationX = 0.2F;
        } else {
            this.jaw.rotationX = 0.0F;
        }
    }
}
