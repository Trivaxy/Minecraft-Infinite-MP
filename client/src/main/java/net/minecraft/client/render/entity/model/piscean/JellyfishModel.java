package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class JellyfishModel extends Model {
    private final ModelPart body;
    private final ModelPart[] tentacles = new ModelPart[4];

    public JellyfishModel() {
        byte offset = -16;
        this.body = new ModelPart(this, 0, 0);
        this.body.addBox(-6.0F, -8.0F, -6.0F, 12, 6, 12, 0.0F, false);
        this.body.pivotY += 24 + offset;

        for (int tentacle = 0; tentacle < this.tentacles.length; tentacle++) {
            this.tentacles[tentacle] = new ModelPart(this, 48, 0);
            float rotation = (float)((Math.PI / 4) + tentacle * Math.PI * 2.0 / this.tentacles.length);
            float pivotX = (float)Math.cos(rotation) * 3.0F;
            float pivotZ = (float)Math.sin(rotation) * 3.0F;
            this.tentacles[tentacle].addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F, false);
            this.tentacles[tentacle].pivotX = pivotX;
            this.tentacles[tentacle].pivotZ = pivotZ;
            this.tentacles[tentacle].pivotY = 22 + offset;
            rotation = (float)(tentacle * Math.PI * -2.0 / this.tentacles.length + (Math.PI / 4));
            this.tentacles[tentacle].rotationY = rotation;
        }
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.body.render(tick);

        for (int tentacle = 0; tentacle < this.tentacles.length; tentacle++) {
            this.tentacles[tentacle].render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float rot, float headYaw, float headPitch, float tick) {
        for (int tentacle = 0; tentacle < this.tentacles.length; tentacle++) {
            this.tentacles[tentacle].rotationX = rot;
        }
    }
}
