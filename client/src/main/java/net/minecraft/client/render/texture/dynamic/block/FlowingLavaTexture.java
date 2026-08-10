package net.minecraft.client.render.texture.dynamic.block;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.util.math.MathHelper;

public class FlowingLavaTexture extends StitchedTexture implements DynamicTexture {
    protected float[] red;
    protected float[] green;
    protected float[] blue;
    protected float[] alpha;
    private byte[] imgData;
    private ByteBuffer texData;
    int tickCounter = 0;

    public FlowingLavaTexture() {
        super("lava_flow");
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

        this.tickCounter++;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float offset = 0.0F;
                int startX = (int)(MathHelper.sin(y * (float) Math.PI * 2.0F / 16.0F) * 1.2F);
                int startY = (int)(MathHelper.sin(x * (float) Math.PI * 2.0F / 16.0F) * 1.2F);

                for (int width = x - 1; width <= x + 1; width++) {
                    for (int height = y - 1; height <= y + 1; height++) {
                        int endX = width + startX & size - 1;
                        int endY = height + startY & size - 1;
                        offset += this.red[endX + endY * size];
                    }
                }

                this.green[x + y * size] = offset / 10.0F
                    + (
                            this.blue[(x + 0 & size - 1) + (y + 0 & size - 1) * size]
                                + this.blue[(x + 1 & 15) + (y + 0 & size - 1) * size]
                                + this.blue[(x + 1 & size - 1) + (y + 1 & size - 1) * size]
                                + this.blue[(x + 0 & size - 1) + (y + 1 & size - 1) * size]
                        )
                        / 4.0F
                        * 0.8F;
                this.blue[x + y * size] = this.blue[x + y * size] + this.alpha[x + y * size] * 0.01F;
                if (this.blue[x + y * size] < 0.0F) {
                    this.blue[x + y * size] = 0.0F;
                }

                this.alpha[x + y * size] = this.alpha[x + y * size] - 0.06F;
                if (Math.random() < 0.005) {
                    this.alpha[x + y * size] = 1.5F;
                }
            }
        }

        float[] colors = this.green;
        this.green = this.red;
        this.red = colors;
        if (this.imgData == null) {
            this.imgData = new byte[pixels * 4];
        }

        for (int y = 0; y < pixels; y++) {
            float offset = this.red[y - this.tickCounter / 3 * size & pixels - 1] * 2.0F;
            offset = MathHelper.clamp(offset, 0.0F, 1.0F);
            int startY = (int)(offset * 100.0F + 155.0F);
            int width = (int)(offset * offset * 255.0F);
            int height = (int)(offset * offset * offset * offset * 128.0F);
            this.imgData[y * 4 + 0] = (byte)startY;
            this.imgData[y * 4 + 1] = (byte)width;
            this.imgData[y * 4 + 2] = (byte)height;
            this.imgData[y * 4 + 3] = -1;
        }

        if (this.texData == null) {
            this.texData = ByteBuffer.allocateDirect(this.imgData.length);
        }

        ((Buffer)this.texData).clear();
        this.texData.put(this.imgData);
        ((Buffer)this.texData).position(0).limit(this.imgData.length);

        for (int w = 0; w < 2; w++) {
            for (int h = 0; h < 2; h++) {
                this.textureSheet.bindImage(this.x + w * size, this.y + h * size, size, size, this.texData);
            }
        }
    }
}
