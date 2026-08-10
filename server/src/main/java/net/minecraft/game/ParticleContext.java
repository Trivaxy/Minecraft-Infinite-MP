package net.minecraft.game;

import net.minecraft.game.item.Item;

public class ParticleContext {
    public static int id = 0;
    public static int meta = 0;

    public static ParticleType setIconContext(ParticleType type, Item item) {
        id = item.id;
        meta = 0;
        return type;
    }

    public static ParticleType setIconContext(ParticleType type, int iid, int imeta) {
        id = iid;
        meta = imeta;
        return type;
    }

    public static void resetContext() {
        id = 0;
        meta = 0;
    }
}
