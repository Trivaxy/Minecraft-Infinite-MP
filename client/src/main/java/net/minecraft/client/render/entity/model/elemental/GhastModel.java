package net.minecraft.client.render.entity.model.elemental;

import java.util.Random;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GhastModel extends Model {
    private ModelPart body;
    private ModelPart[] tentacles = new ModelPart[9];

    public GhastModel() {
        byte offset = -16;
        this.body = new ModelPart(this, 0, 0);
        this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false);
        this.body.pivotY += 24 + offset;
        Random randomizer = new Random(1660L);

        for (int tentacle = 0; tentacle < this.tentacles.length; tentacle++) {
            this.tentacles[tentacle] = new ModelPart(this, 0, 0);
            float pivotX = ((tentacle % 3 - tentacle / 3 % 2 * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
            float pivotZ = (tentacle / 3 / 2.0F * 2.0F - 1.0F) * 5.0F;
            int randomOffset = randomizer.nextInt(7) + 8;
            this.tentacles[tentacle].addBox(-1.0F, 0.0F, -1.0F, 2, randomOffset, 2, 0.0F, false);
            this.tentacles[tentacle].pivotX = pivotX;
            this.tentacles[tentacle].pivotZ = pivotZ;
            this.tentacles[tentacle].pivotY = 31 + offset;
        }
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.body.render(tick);

        for (int var8 = 0; var8 < this.tentacles.length; var8++) {
            this.tentacles[var8].render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        for (int tentacle = 0; tentacle < this.tentacles.length; tentacle++) {
            this.tentacles[tentacle].rotationX = 0.2F * MathHelper.sin(angle * 0.3F + tentacle) + 0.4F;
        }
    }
}
