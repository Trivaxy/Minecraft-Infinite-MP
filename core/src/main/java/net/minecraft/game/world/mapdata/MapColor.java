package net.minecraft.game.world.mapdata;

public class MapColor {
    public static final MapColor[] mapColors = new MapColor[16];
    public static final MapColor air = new MapColor(0, 0);
    public static final MapColor grass = new MapColor(1, 8368696);
    public static final MapColor sand = new MapColor(2, 16247203);
    public static final MapColor cloth = new MapColor(3, 10987431);
    public static final MapColor lava = new MapColor(4, 16711680);
    public static final MapColor ice = new MapColor(5, 10526975);
    public static final MapColor iron = new MapColor(6, 10987431);
    public static final MapColor foliage = new MapColor(7, 31744);
    public static final MapColor snow = new MapColor(8, 16777215);
    public static final MapColor clay = new MapColor(9, 10791096);
    public static final MapColor dirt = new MapColor(10, 12020271);
    public static final MapColor stone = new MapColor(11, 7368816);
    public static final MapColor water = new MapColor(12, 4210943);
    public static final MapColor wood = new MapColor(13, 6837042);
    public final int value;
    public final int index;

    private MapColor(int var1, int var2) {
        this.index = var1;
        this.value = var2;
        mapColors[var1] = this;
    }
}
