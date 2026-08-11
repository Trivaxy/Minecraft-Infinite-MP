package net.minecraft.game.world.mapdata;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class MapInfo {
    final MapData data;
    public final Player player;
    public int[] xCoords;
    public int[] yCoords;
    private byte[] lastPos;
    private int randIndex;
    private int refreshTicks;
    public int updateTicks;
    private boolean isFull;

    public MapInfo(MapData data, Player player) {
        this.data = data;
        this.xCoords = new int[128];
        this.yCoords = new int[128];
        this.randIndex = 0;
        this.refreshTicks = 0;
        this.isFull = false;
        this.player = player;

        for (int index = 0; index < this.xCoords.length; index++) {
            this.xCoords[index] = 0;
            this.yCoords[index] = 127;
        }
    }

    public byte[] getPlayers(ItemStack stack) {
        if (!this.isFull) {
            byte[] data = new byte[]{2, this.data.scale};
            this.isFull = true;
            return data;
        }

        if (--this.refreshTicks < 0) {
            this.refreshTicks = 4;
            byte[] data = new byte[this.data.playersVisible.size() * 3 + 1];
            data[0] = 1;
            int index = 0;

            for (MapCoords var5 : this.data.playersVisible.values()) {
                data[index * 3 + 1] = (byte)(var5.iconSize << 4 | var5.iconRotation & 15);
                data[index * 3 + 2] = (byte)var5.centerX;
                data[index * 3 + 3] = (byte)var5.centerZ;
                index++;
            }

            boolean framedMap = !stack.isInFrame();
            if (this.lastPos != null && this.lastPos.length == data.length) {
                for (int pos = 0; pos < data.length; pos++) {
                    if (data[pos] != this.lastPos[pos]) {
                        framedMap = false;
                        break;
                    }
                }
            } else {
                framedMap = false;
            }

            if (!framedMap) {
                this.lastPos = data;
                return data;
            }
        }

        for (int loop = 0; loop < 1; loop++) {
            int index = this.randIndex++ * 11 % 128;
            if (this.xCoords[index] >= 0) {
                int y = this.yCoords[index] - this.xCoords[index] + 1;
                int pos = this.xCoords[index];
                byte[] positions = new byte[y + 3];
                positions[0] = 0;
                positions[1] = (byte)index;
                positions[2] = (byte)pos;

                for (int curr = 0; curr < positions.length - 3; curr++) {
                    positions[curr + 3] = this.data.colors[(curr + pos) * 128 + index];
                }

                this.yCoords[index] = -1;
                this.xCoords[index] = -1;
                return positions;
            }
        }

        return null;
    }
}
