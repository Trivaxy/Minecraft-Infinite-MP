package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BookModel extends Model {
    public ModelPart coverRight = new ModelPart(this);
    public ModelPart coverLeft = new ModelPart(this);
    public ModelPart pagesRight = new ModelPart(this);
    public ModelPart pagesLeft = new ModelPart(this);
    public ModelPart flippingPageRight = new ModelPart(this);
    public ModelPart flippingPageLeft = new ModelPart(this);
    public ModelPart bookSpine = new ModelPart(this);

    public BookModel() {
        this.coverRight.setTextureOffset(0, 0);
        this.coverRight.addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0, 0.0F, false);
        this.coverRight.setPivots(0.0F, 0.0F, -1.0F);
        this.coverLeft.setTextureOffset(16, 0);
        this.coverLeft.addBox(0.0F, -5.0F, 0.0F, 6, 10, 0, 0.0F, false);
        this.coverLeft.setPivots(0.0F, 0.0F, 1.0F);
        this.pagesRight.setTextureOffset(0, 10);
        this.pagesRight.addBox(0.0F, -4.0F, -0.99F, 5, 8, 1, 0.0F, false);
        this.pagesLeft.setTextureOffset(12, 10);
        this.pagesLeft.addBox(0.0F, -4.0F, -0.01F, 5, 8, 1, 0.0F, false);
        this.flippingPageRight.setTextureOffset(24, 10);
        this.flippingPageRight.addBox(0.0F, -4.0F, 0.0F, 5, 8, 0, 0.0F, false);
        this.flippingPageLeft.setTextureOffset(24, 10);
        this.flippingPageLeft.addBox(0.0F, -4.0F, 0.0F, 5, 8, 0, 0.0F, false);
        this.bookSpine.setTextureOffset(12, 0);
        this.bookSpine.addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0, 0.0F, false);
        this.bookSpine.rotationY = (float) (Math.PI / 2);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.coverRight.render(tick);
        this.coverLeft.render(tick);
        this.bookSpine.render(tick);
        this.pagesRight.render(tick);
        this.pagesLeft.render(tick);
        this.flippingPageRight.render(tick);
        this.flippingPageLeft.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        float yawOffset = (MathHelper.sin(swing * 0.02F) * 0.1F + 1.25F) * yaw;
        this.coverRight.rotationY = (float) Math.PI + yawOffset;
        this.coverLeft.rotationY = -yawOffset;
        this.pagesRight.rotationY = yawOffset;
        this.pagesLeft.rotationY = -yawOffset;
        this.bookSpine.pivotX = -0.001F;
        this.flippingPageRight.rotationY = yawOffset - yawOffset * 2.0F * anim;
        this.flippingPageLeft.rotationY = yawOffset - yawOffset * 2.0F * angle;
        this.pagesRight.pivotX = MathHelper.sin(yawOffset);
        this.pagesLeft.pivotX = MathHelper.sin(yawOffset);
        this.flippingPageRight.pivotX = MathHelper.sin(yawOffset);
        this.flippingPageLeft.pivotX = MathHelper.sin(yawOffset);
    }
}
