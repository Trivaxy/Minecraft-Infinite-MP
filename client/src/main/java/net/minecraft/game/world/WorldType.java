package net.minecraft.game.world;

public enum WorldType {
    DEFAULT("DEFAULT", "selectWorld.mapType.normal", 0),
    FLAT("FLAT", "selectWorld.mapType.flat", 1),
    OLD("OLD", "selectWorld.mapType.old", 2),
    OCEAN("OCEAN", "selectWorld.mapType.ocean", 3),
    INLAND("INLAND", "selectWorld.mapType.inland", 4),
    CAVES("CAVES", "selectWorld.mapType.caves", 5),
    FLOATING("FLOATING", "selectWorld.mapType.floating", 6),
    AMPLIFIED("AMPLIFIED", "selectWorld.mapType.amplified", 7),
    BIOME("SINGLE_BIOME", "selectWorld.mapType.singleBiome", 8);

    public final String typeName;
    public final String translateKey;
    public final int id;

    WorldType(String name, String translatekey, int id) {
        this.typeName = name;
        this.translateKey = translatekey;
        this.id = id;
    }

    public static WorldType fromNumber(int num) {
        for (WorldType e : values()) {
            if (e.id == num) {
                return e;
            }
        }

        return DEFAULT;
    }

    public static WorldType fromString(String name) {
        for (WorldType e : values()) {
            if (e.typeName.equals(name)) {
                return e;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
