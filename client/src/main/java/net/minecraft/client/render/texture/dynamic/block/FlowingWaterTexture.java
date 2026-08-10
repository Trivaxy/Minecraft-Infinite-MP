package net.minecraft.client.render.texture.dynamic.block;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.util.math.MathHelper;

public class FlowingWaterTexture extends StitchedTexture implements DynamicTexture {
    protected float[] red;
    protected float[] green;
    protected float[] blue;
    protected float[] alpha;
    private byte[] imgData;
    private ByteBuffer texData;

    public FlowingWaterTexture() {
        super("water_flow");
    }

    @Override
    public void updateAnimation() {
        int size = this.width / 2;
        int pixels = this.width / 2 * (this.height / 2);
        if (this.red == null && this.green == null && this.blue == null && this.alpha == null) {
            this.red = new float[pixels];
            this.green = new float[pixels];
            this.blue = new float[pixels];
            this.alpha = new float[pixels];
        }

        this.ticks++;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float offset = 0.0F;

                for (int y2 = y - 2; y2 <= y; y2++) {
                    int startX = x & size - 1;
                    int startY = y2 & size - 1;
                    offset += this.red[startX + startY * size];
                }

                this.green[x + y * size] = offset / 3.2F + this.blue[x + y * size] * 0.8F;
            }
        }

        for (int var14 = 0; var14 < size; var14++) {
            for (int y = 0; y < size; y++) {
                this.blue[var14 + y * size] = this.blue[var14 + y * size] + this.alpha[var14 + y * size] * 0.05F;
                if (this.blue[var14 + y * size] < 0.0F) {
                    this.blue[var14 + y * size] = 0.0F;
                }

                this.alpha[var14 + y * size] = this.alpha[var14 + y * size] - 0.3F;
                if (Math.random() < 0.2) {
                    this.alpha[var14 + y * size] = 0.5F;
                }
            }
        }

        float[] color = this.green;
        this.green = this.red;
        this.red = color;
        if (this.imgData == null) {
            this.imgData = new byte[pixels * 4];
        }

        for (int y = 0; y < pixels; y++) {
            float offset = this.red[y - this.ticks * size & pixels - 1];
            offset = MathHelper.clamp(offset, 0.0F, 1.0F);
            float offsetIncreased = offset * offset;
            int red = (int)(32.0F + offsetIncreased * 32.0F);
            int blue = (int)(50.0F + offsetIncreased * 64.0F);
            int alpha = (int)(146.0F + offsetIncreased * 50.0F);
            int greenDivider = 1;
            if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.currDim instanceof VoidDimension) {
                greenDivider = 255;
            }

            this.imgData[y * 4 + 0] = (byte)red;
            this.imgData[y * 4 + 1] = (byte)blue;
            this.imgData[y * 4 + 2] = (byte)(-1 / greenDivider);
            this.imgData[y * 4 + 3] = (byte)alpha;
        }

        if (this.texData == null) {
            this.texData = ByteBuffer.allocateDirect(this.imgData.length);
        }

        ((Buffer)this.texData).clear();
        this.texData.put(this.imgData);
        ((Buffer)this.texData).position(0).limit(this.imgData.length);

        for (int width = 0; width < 2; width++) {
            for (int height = 0; height < 2; height++) {
                this.textureSheet.bindImage(this.x + width * size, this.y + height * size, size, size, this.texData);
            }
        }
    }
}
