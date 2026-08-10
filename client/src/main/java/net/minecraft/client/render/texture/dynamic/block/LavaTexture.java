package net.minecraft.client.render.texture.dynamic.block;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.util.math.MathHelper;

public class LavaTexture extends StitchedTexture implements DynamicTexture {
    protected float[] red;
    protected float[] green;
    protected float[] blue;
    protected float[] alpha;
    private byte[] imgData;
    private ByteBuffer texData;

    public LavaTexture() {
        super("lava");
    }

    @Override
    public void updateAnimation() {
        if (this.red == null && this.green == null && this.blue == null && this.alpha == null) {
            this.red = new float[this.width * this.height];
            this.green = new float[this.width * this.height];
            this.blue = new float[this.width * this.height];
            this.alpha = new float[this.width * this.height];
        }

        this.ticks++;

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                float offset = 0.0F;
                int startX = (int)(MathHelper.sin(y * (float) Math.PI * 2.0F / 16.0F) * 1.2F);
                int startY = (int)(MathHelper.sin(x * (float) Math.PI * 2.0F / 16.0F) * 1.2F);

                for (int width = x - 1; width <= x + 1; width++) {
                    for (int height = y - 1; height <= y + 1; height++) {
                        int endX = width + startX & this.width - 1;
                        int endY = height + startY & this.width - 1;
                        offset += this.red[endX + endY * this.width];
                    }
                }

                this.green[x + y * this.width] = offset / 10.0F
                    + (
                            this.blue[(x + 0 & this.width - 1) + (y + 0 & this.width - 1) * this.width]
                                + this.blue[(x + 1 & this.width - 1) + (y + 0 & 15) * this.width]
                                + this.blue[(x + 1 & this.width - 1) + (y + 1 & this.width - 1) * this.width]
                                + this.blue[(x + 0 & this.width - 1) + (y + 1 & this.width - 1) * this.width]
                        )
                        / 4.0F
                        * 0.8F;
                this.blue[x + y * this.width] = this.blue[x + y * this.width] + this.alpha[x + y * this.width] * 0.01F;
                if (this.blue[x + y * this.width] < 0.0F) {
                    this.blue[x + y * this.width] = 0.0F;
                }

                this.alpha[x + y * this.width] = this.alpha[x + y * this.width] - 0.06F;
                if (Math.random() < 0.005) {
                    this.alpha[x + y * this.width] = 1.5F;
                }
            }
        }

        float[] colors = this.green;
        this.green = this.red;
        this.red = colors;
        if (this.imgData == null) {
            this.imgData = new byte[this.width * this.height * 4];
        }

        for (int y = 0; y < this.width * this.height; y++) {
            float offset = this.red[y] * 2.0F;
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
        this.textureSheet.bindImage(this.x, this.y, this.width, this.height, this.texData);
    }
}
