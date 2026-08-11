package net.minecraft.game.world.generation.features.structure.stronghold;

public class DoorFacing {
    static final int[] doorEnum = new int[DoorType.values().length];

    static {
        try {
            doorEnum[DoorType.OPENING.ordinal()] = 1;
        } catch (NoSuchFieldError var4) {
        }

        try {
            doorEnum[DoorType.WOOD_DOOR.ordinal()] = 2;
        } catch (NoSuchFieldError var3) {
        }

        try {
            doorEnum[DoorType.GRATES.ordinal()] = 3;
        } catch (NoSuchFieldError var2) {
        }

        try {
            doorEnum[DoorType.IRON_DOOR.ordinal()] = 4;
        } catch (NoSuchFieldError var1) {
        }
    }
}
