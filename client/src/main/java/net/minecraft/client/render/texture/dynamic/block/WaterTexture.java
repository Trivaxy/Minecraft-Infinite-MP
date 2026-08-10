package net.minecraft.client.render.texture.dynamic.block;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.util.math.MathHelper;

public class WaterTexture extends StitchedTexture implements DynamicTexture {
    protected float[] red;
    protected float[] green;
    protected float[] blue;
    protected float[] alpha;
    private byte[] imgData;
    private ByteBuffer texData;

    public WaterTexture() {
        super("water");
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

                for (int x1 = x - 1; x1 <= x + 1; x1++) {
                    int startX = x1 & this.width - 1;
                    int startY = y & this.height - 1;
                    offset += this.red[startX + startY * this.width];
                }

                this.green[x + y * this.width] = offset / 3.3F + this.blue[x + y * this.width] * 0.8F;
            }
        }

        for (int var12 = 0; var12 < this.width; var12++) {
            for (int y = 0; y < this.height; y++) {
                this.blue[var12 + y * this.width] = this.blue[var12 + y * this.width] + this.alpha[var12 + y * this.width] * 0.05F;
                if (this.blue[var12 + y * this.width] < 0.0F) {
                    this.blue[var12 + y * this.width] = 0.0F;
                }

                this.alpha[var12 + y * this.width] = this.alpha[var12 + y * this.width] - 0.1F;
                if (Math.random() < 0.05) {
                    this.alpha[var12 + y * this.width] = 0.5F;
                }
            }
        }

        float[] color = this.green;
        this.green = this.red;
        this.red = color;
        if (this.imgData == null) {
            this.imgData = new byte[this.width * this.height * 4];
        }

        for (int y = 0; y < this.width * this.height; y++) {
            float offset = this.red[y];
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
        this.textureSheet.bindImage(this.x, this.y, this.width, this.height, this.texData);
    }
}
