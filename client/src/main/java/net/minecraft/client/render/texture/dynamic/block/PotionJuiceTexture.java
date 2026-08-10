package net.minecraft.client.render.texture.dynamic.block;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.util.math.MathHelper;

public class PotionJuiceTexture extends StitchedTexture implements DynamicTexture {
    protected float[] red = new float[256];
    protected float[] green = new float[256];
    protected float[] blue = new float[256];
    protected float[] alpha = new float[256];
    private byte[] imgData;
    private ByteBuffer texData;

    public PotionJuiceTexture() {
        super("potionjuice");
    }

    @Override
    public void updateAnimation() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                float offset = 0.0F;
                int startX = (int)(MathHelper.sin(y * (float) Math.PI * 2.0F / 16.0F) * 1.2F);
                int startY = (int)(MathHelper.sin(x * (float) Math.PI * 2.0F / 16.0F) * 1.2F);

                for (int width = x - 1; width <= x + 1; width++) {
                    for (int height = y - 1; height <= y + 1; height++) {
                        int endX = width + startX & 15;
                        int endY = height + startY & 15;
                        offset += this.red[endX + endY * 16];
                    }
                }

                this.green[x + y * 16] = offset / 10.0F
                    + (
                            this.blue[(x + 0 & 15) + (y + 0 & 15) * 16]
                                + this.blue[(x + 1 & 15) + (y + 0 & 15) * 16]
                                + this.blue[(x + 1 & 15) + (y + 1 & 15) * 16]
                                + this.blue[(x + 0 & 15) + (y + 1 & 15) * 16]
                        )
                        / 4.0F
                        * 0.8F;
                this.blue[x + y * 16] = this.blue[x + y * 16] + this.alpha[x + y * 16] * 0.01F;
                if (this.blue[x + y * 16] < 0.0F) {
                    this.blue[x + y * 16] = 0.0F;
                }

                this.alpha[x + y * 16] = this.alpha[x + y * 16] - 0.06F;
                if (Math.random() < 0.005) {
                    this.alpha[x + y * 16] = 1.5F;
                }
            }
        }

        float[] color = this.green;
        this.green = this.red;
        this.red = color;
        if (this.imgData == null) {
            this.imgData = new byte[this.width * this.height * 4];
        }

        for (int y = 0; y < 256; y++) {
            float offset = this.red[y] * 2.0F;
            if (offset > 1.0F) {
                offset = 1.0F;
            }

            if (offset < 0.0F) {
                offset = 0.0F;
            }

            int startY = (int)(offset * 100.0F + 155.0F);
            int width = (int)(offset * offset * 255.0F);
            int height = (int)(offset * offset * offset * offset * 128.0F);
            int endX = (int)(startY * 0.3 + width * 0.4 + height * 0.3);
            startY = endX;
            width = endX;
            height = endX;
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
