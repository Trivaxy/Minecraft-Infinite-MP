package net.minecraft.game.texture;

public interface Icon {
    int getIconWidth();

    int getIconHeight();

    int getOriginX();

    int getOriginY();

    float getMinU();

    float getMaxU();

    float getInterpolatedU(double var1);

    float getInterpolatedV(double var1);

    float getMinV();

    float getMaxV();

    int getSheetWidth();

    int getSheetHeight();

    String getIconName();
}
