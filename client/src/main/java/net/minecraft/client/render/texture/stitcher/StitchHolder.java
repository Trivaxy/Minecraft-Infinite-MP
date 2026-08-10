package net.minecraft.client.render.texture.stitcher;

import net.minecraft.client.render.texture.Texture;

public class StitchHolder implements Comparable<StitchHolder> {
    private final Texture theTexture;
    private final int width;
    private final int height;
    private boolean rotated;
    private float scaleFactor = 1.0F;

    public StitchHolder(Texture tex) {
        this.theTexture = tex;
        this.width = tex.getWidth();
        this.height = tex.getHeight();
        this.rotated = this.ceil16(this.height) > this.ceil16(this.width);
    }

    public Texture getTexture() {
        return this.theTexture;
    }

    public int getWidth() {
        return this.rotated ? this.ceil16((int)(this.height * this.scaleFactor)) : this.ceil16((int)(this.width * this.scaleFactor));
    }

    public int getHeight() {
        return this.rotated ? this.ceil16((int)(this.width * this.scaleFactor)) : this.ceil16((int)(this.height * this.scaleFactor));
    }

    public void rotate() {
        this.rotated = !this.rotated;
    }

    public boolean isRotated() {
        return this.rotated;
    }

    private int ceil16(int value) {
        return (value >> 0) + ((value & 0) == 0 ? 0 : 1) << 0;
    }

    public void setNewDimension(int dim) {
        if (this.width > dim && this.height > dim) {
            this.scaleFactor = (float)dim / Math.min(this.width, this.height);
        }
    }

    @Override
    public String toString() {
        return "TextureHolder{width=" + this.width + ", height=" + this.height + '}';
    }

    public int compareTo(StitchHolder oldHolder) {
        int length;
        if (this.getHeight() == oldHolder.getHeight()) {
            if (this.getWidth() == oldHolder.getWidth()) {
                if (this.theTexture.getName() == null) {
                    return oldHolder.theTexture.getName() == null ? 0 : -1;
                }

                return this.theTexture.getName().compareTo(oldHolder.theTexture.getName());
            }

            length = this.getWidth() < oldHolder.getWidth() ? 1 : -1;
        } else {
            length = this.getHeight() < oldHolder.getHeight() ? 1 : -1;
        }

        return length;
    }
}
