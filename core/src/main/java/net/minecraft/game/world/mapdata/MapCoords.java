package net.minecraft.game.world.mapdata;

public class MapCoords {
    public byte iconSize;
    public int centerX;
    public int centerZ;
    public byte iconRotation;
    final MapData data;

    public MapCoords(MapData data, byte size, int x, int z, byte rot) {
        this.data = data;
        this.iconSize = size;
        this.centerX = x;
        this.centerZ = z;
        this.iconRotation = rot;
    }
}
