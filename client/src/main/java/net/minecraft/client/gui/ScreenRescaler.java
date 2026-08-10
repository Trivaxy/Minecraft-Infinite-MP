package net.minecraft.client.gui;

import net.minecraft.client.options.OptionValues;

public class ScreenRescaler {
    private int scaledWidth;
    private int scaledHeight;
    public int scaleFactor;

    public ScreenRescaler(int width, int height) {
        this.scaledWidth = width;
        this.scaledHeight = height;
        int scale = OptionValues.guiScale;
        if (scale == 0) {
            scale = 1000;
        }

        this.scaleFactor = 1;

        while (this.scaleFactor < scale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            this.scaleFactor++;
        }

        this.scaledWidth = this.scaledWidth / this.scaleFactor;
        this.scaledHeight = this.scaledHeight / this.scaleFactor;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }
}
