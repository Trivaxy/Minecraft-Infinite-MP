package net.minecraft.game.texture;

public class IconFlipped implements Icon {
    private final Icon base;
    private final boolean fu;
    private final boolean fv;

    public IconFlipped(Icon base, boolean flipu, boolean flipv) {
        this.base = base;
        this.fu = flipu;
        this.fv = flipv;
    }

    @Override
    public int getIconWidth() {
        return this.base.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return this.base.getIconHeight();
    }

    @Override
    public int getOriginX() {
        return this.base.getOriginX();
    }

    @Override
    public int getOriginY() {
        return this.base.getOriginY();
    }

    @Override
    public float getMinU() {
        return this.fu ? this.base.getMaxU() : this.base.getMinU();
    }

    @Override
    public float getMaxU() {
        return this.fu ? this.base.getMinU() : this.base.getMaxU();
    }

    @Override
    public float getMinV() {
        return this.fv ? this.base.getMinV() : this.base.getMinV();
    }

    @Override
    public float getMaxV() {
        return this.fv ? this.base.getMinV() : this.base.getMaxV();
    }

    @Override
    public float getInterpolatedU(double u) {
        float u2 = this.getMaxU() - this.getMinU();
        return this.getMinU() + u2 * ((float)u / 16.0F);
    }

    @Override
    public float getInterpolatedV(double v) {
        float v2 = this.getMaxV() - this.getMinV();
        return this.getMinV() + v2 * ((float)v / 16.0F);
    }

    @Override
    public String getIconName() {
        return this.base.getIconName();
    }

    @Override
    public int getSheetWidth() {
        return this.base.getSheetWidth();
    }

    @Override
    public int getSheetHeight() {
        return this.base.getSheetHeight();
    }
}
