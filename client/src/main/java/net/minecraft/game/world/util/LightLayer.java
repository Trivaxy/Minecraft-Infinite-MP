package net.minecraft.game.world.util;

public enum LightLayer {
    Sky(15),
    Block(0);

    public final int defaultLightValue;

    LightLayer(int lightValue) {
        this.defaultLightValue = lightValue;
    }
}
