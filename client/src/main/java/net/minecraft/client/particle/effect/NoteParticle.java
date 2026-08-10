package net.minecraft.client.particle.effect;

import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class NoteParticle extends GlowParticle {
    public NoteParticle(World world, double x, double y, double z, float color, float scaleFactor) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.01;
        this.motionY *= 0.01;
        this.motionZ *= 0.01;
        this.motionY += 0.2;
        this.red = MathHelper.sin((color + 0.0F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
        this.green = MathHelper.sin((color + 0.33333334F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
        this.blue = MathHelper.sin((color + 0.6666667F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
        this.scale *= 0.75F;
        this.scale *= scaleFactor;
        this.maxAge = 6;
        this.noClip = false;
        this.textureIndex = 128;
    }
}
