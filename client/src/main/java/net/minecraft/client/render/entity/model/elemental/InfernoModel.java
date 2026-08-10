package net.minecraft.client.render.entity.model.elemental;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class InfernoModel extends Model {
    private final ModelPart head;
    private final ModelPart core;
    private final ModelPart spinner;
    private final ModelPart cover1;
    private final ModelPart shield1;
    private final ModelPart cover2;
    private final ModelPart shield2;
    private final ModelPart cover3;
    private final ModelPart shield3;

    public InfernoModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.core = new ModelPart(this);
        this.core.setPivots(0.25F, 13.0F, 0.0F);
        this.core.cuboids.add(new Cuboid(this.core, 52, 0, -1.0F, -6.0F, -2.5F, 3, 10, 3, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -4.0F, -16.0F, -4.0F, 8, 8, 8, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 20, -4.5F, -17.0F, -4.5F, 9, 3, 9, 0.0F, false));
        this.core.addChild(this.head);
        this.spinner = new ModelPart(this);
        this.spinner.setPivots(0.0F, 0.0F, 0.0F);
        this.core.addChild(this.spinner);
        this.cover1 = new ModelPart(this);
        this.cover1.setPivots(0.0F, 9.0F, 0.0F);
        this.spinner.addChild(this.cover1);
        this.shield1 = new ModelPart(this);
        this.shield1.setPivots(0.0F, 0.0F, 0.0F);
        this.cover1.addChild(this.shield1);
        this.setRotation(this.shield1, 0.1309F, 0.0F, 0.0F);
        this.shield1.cuboids.add(new Cuboid(this.shield1, 42, 12, -4.5F, -19.9463F, -8.188F, 10, 19, 1, 0.0F, false));
        this.cover2 = new ModelPart(this);
        this.cover2.setPivots(0.0F, 9.0F, 0.0F);
        this.spinner.addChild(this.cover2);
        this.setRotation(this.cover2, 0.0F, -2.0944F, 0.0F);
        this.shield2 = new ModelPart(this);
        this.shield2.setPivots(0.0F, 0.0F, 0.0F);
        this.cover2.addChild(this.shield2);
        this.setRotation(this.shield2, 0.1309F, 0.0F, 0.0F);
        this.shield2.cuboids.add(new Cuboid(this.shield2, 42, 12, -4.5F, -19.9463F, -8.188F, 10, 19, 1, 0.0F, false));
        this.cover3 = new ModelPart(this);
        this.cover3.setPivots(0.0F, 9.0F, 0.0F);
        this.spinner.addChild(this.cover3);
        this.setRotation(this.cover3, 0.0F, 2.2689F, 0.0F);
        this.shield3 = new ModelPart(this);
        this.shield3.setPivots(0.0F, 0.0F, 0.0F);
        this.cover3.addChild(this.shield3);
        this.setRotation(this.shield3, 0.1309F, 0.0F, 0.0F);
        this.shield3.cuboids.add(new Cuboid(this.shield3, 42, 12, -4.5F, -19.9463F, -8.188F, 10, 19, 1, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.core.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float rotation, float yaw, float pitch, float tick) {
        float angle = 0.03141593F;
        this.cover1.pivotY = 10.0F + MathHelper.cos(rotation * 0.15F);
        this.cover1.pivotX = MathHelper.cos(angle) * 5.0F - 4.0F;
        this.cover1.pivotZ = MathHelper.sin(angle) * 5.0F;
        this.cover2.pivotY = 10.0F + MathHelper.cos(rotation * 0.15F);
        this.cover2.pivotX = MathHelper.cos(angle) * 5.0F - 4.0F;
        this.cover2.pivotZ = MathHelper.sin(angle) * 5.0F;
        this.cover3.pivotY = 10.0F + MathHelper.cos(rotation * 0.15F);
        this.cover3.pivotX = MathHelper.cos(angle) * 5.0F - 4.0F;
        this.cover3.pivotZ = MathHelper.sin(angle) * 5.0F;
        angle = rotation * (float) Math.PI * 0.03F;
        this.cover1.rotationY = angle * 2.0F;
        angle += 1.0995574F;
        float backup = angle;
        this.cover2.rotationY = angle * 2.0F;
        backup += 1.0995574F;
        this.cover3.rotationY = backup * 2.0F;
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
    }
}
