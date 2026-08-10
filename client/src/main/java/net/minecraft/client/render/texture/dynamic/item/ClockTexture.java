package net.minecraft.client.render.texture.dynamic.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.util.math.MathHelper;

public class ClockTexture extends StitchedTexture implements DynamicTexture {
    private double updateTicks;
    private double rotation;

    public ClockTexture() {
        super("clock");
    }

    @Override
    public void updateAnimation() {
        Minecraft mc = Minecraft.getMinecraft();
        double sunAngle = 0.0;
        if (mc.world != null && mc.player != null) {
            sunAngle = mc.world.getSunAngle(1.0F);
            if (mc.world.currDim instanceof VoidDimension) {
                sunAngle = (float)(Math.random() * (float) Math.PI * 2.0);
            }
        }

        double offset = sunAngle - this.updateTicks;

        while (offset < -0.5) {
            offset++;
        }

        while (offset >= 0.5) {
            offset--;
        }

        offset = MathHelper.clamp(offset, -1.0, 1.0);
        this.rotation += offset * 0.1;
        this.rotation *= 0.8;
        this.updateTicks = this.updateTicks + this.rotation;
        int frame = (int)((this.updateTicks + 1.0) * this.textures.size()) % this.textures.size();

        while (frame < 0) {
            frame = (frame + this.textures.size()) % this.textures.size();
        }

        if (frame != this.frames) {
            this.frames = frame;
            this.textureSheet.copyFrom(this.x, this.y, this.textures.get(this.frames), this.rotated);
        }
    }
}
