package net.minecraft.game.world.mapdata;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class MapData {
    public List<MapInfo> players = new ArrayList<>();
    private final Map<Player, MapInfo> playersInfo = new HashMap<>();
    public Map<String, MapCoords> playersVisible = new LinkedHashMap<>();
    public ArrayList<MapCoords> flags = new ArrayList<>();
    public String mapName;
    public byte[] colors = new byte[16384];
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;
    private boolean dirty;
    public boolean unlockHeight;

    public MapData(String name) {
        this.mapName = name;
    }

    public void readFromNBT(CompoundTag tag) {
        this.dimension = tag.getByte("dimension");
        this.xCenter = tag.getInteger("xCenter");
        this.zCenter = tag.getInteger("zCenter");
        this.scale = tag.getByte("scale");
        this.unlockHeight = tag.getBoolean("caves");
        if (this.scale < 0) {
            this.scale = 0;
        }

        if (this.scale > 4) {
            this.scale = 4;
        }

        short width = tag.getShort("width");
        short height = tag.getShort("height");
        if (width == 128 && height == 128) {
            this.colors = tag.getByteArray("colors");
        } else {
            byte[] colorData = tag.getByteArray("colors");
            this.colors = new byte[16384];
            int x = (128 - width) / 2;
            int z = (128 - height) / 2;

            for (int colX = 0; colX < height; colX++) {
                int posX = colX + z;
                if (posX >= 0 || posX < 128) {
                    for (int colZ = 0; colZ < width; colZ++) {
                        int posZ = colZ + x;
                        if (posZ >= 0 || posZ < 128) {
                            this.colors[posZ + posX * 128] = colorData[colZ + colX * width];
                        }
                    }
                }
            }
        }

        this.flags.clear();
        ListTag<CompoundTag> flagList = tag.getTagList("flags");

        for (int i = 0; i < flagList.size(); i++) {
            CompoundTag flag = flagList.tagAt(i);
            byte type = flag.getByte("type");
            int x = flag.getInteger("x");
            int z = flag.getInteger("z");
            byte rot = flag.getByte("rot");
            this.flags.add(new MapCoords(this, type, x, z, rot));
        }
    }

    public void writeToNBT(CompoundTag tag) {
        tag.setByte("dimension", this.dimension);
        tag.setInteger("xCenter", this.xCenter);
        tag.setInteger("zCenter", this.zCenter);
        tag.setByte("scale", this.scale);
        tag.setShort("width", (short)128);
        tag.setShort("height", (short)128);
        tag.setByteArray("colors", this.colors);
        tag.setBoolean("caves", this.unlockHeight);
        ListTag<CompoundTag> flagList = new ListTag<>();

        for (MapCoords flag : this.flags) {
            CompoundTag flagTag = new CompoundTag();
            flagTag.setByte("type", flag.iconSize);
            flagTag.setInteger("x", flag.centerX);
            flagTag.setInteger("z", flag.centerZ);
            flagTag.setByte("rot", flag.iconRotation);
            flagList.add(flagTag);
        }

        tag.setTag("flags", flagList);
    }

    public void updateVisiblePlayers(Player player, ItemStack stack) {
        if (!this.playersInfo.containsKey(player)) {
            MapInfo info = new MapInfo(this, player);
            this.playersInfo.put(player, info);
            this.players.add(info);
        }

        if (!player.inventory.hasItemStack(stack)) {
            this.playersVisible.remove(player.username);
        }

        for (int pindex = 0; pindex < this.players.size(); pindex++) {
            MapInfo info = this.players.get(pindex);
            if (!info.player.isDead && (info.player.inventory.hasItemStack(stack) || stack.isInFrame())) {
                if (!stack.isInFrame()) {
                    this.updateData(0, info.player.world, info.player.username, info.player.posX, info.player.posZ, info.player.yaw);
                }
            } else {
                this.playersInfo.remove(info.player);
                this.players.remove(info);
            }
        }

        if (stack.isInFrame()) {
            this.updateData(
                1, player.world, "frame-" + stack.getFrame().entityId, stack.getFrame().xPosition, stack.getFrame().zPosition, stack.getFrame().direction * 90
            );
        }
    }

    private void updateData(int type, World world, String frame, double x, double z, double rot) {
        int scale = 1 << this.scale;
        float playerX = (float)(x - this.xCenter) / scale;
        float playerZ = (float)(z - this.zCenter) / scale;
        byte xOff = (byte)(playerX * 2.0F + 0.5);
        byte zOff = (byte)(playerZ * 2.0F + 0.5);
        byte offset = 63;
        byte pos;
        if (playerX >= -offset && playerZ >= -offset && playerX <= offset && playerZ <= offset) {
            rot += rot < 0.0 ? -8.0 : 8.0;
            pos = (byte)(rot * 16.0 / 360.0);
            if (this.dimension < 0) {
                int tick = (int)(world.getSettings().getWorldTime() / 10L);
                pos = (byte)(tick * tick * 34187121 + tick * 121 >> 15 & 15);
            }
        } else {
            if (Math.abs(playerX) >= 320.0F || Math.abs(playerZ) >= 320.0F) {
                this.playersVisible.remove(frame);
                return;
            }

            type = 6;
            pos = 0;
            if (playerX <= -offset) {
                xOff = (byte)(offset * 2 + 2.5);
            }

            if (playerZ <= -offset) {
                zOff = (byte)(offset * 2 + 2.5);
            }

            if (playerX >= offset) {
                xOff = (byte)(offset * 2 + 1);
            }

            if (playerZ >= offset) {
                zOff = (byte)(offset * 2 + 1);
            }
        }

        this.playersVisible.put(frame, new MapCoords(this, (byte)type, xOff, zOff, pos));
    }

    public byte[] getUpdatePacketData(ItemStack stack, World world, Player player) {
        MapInfo info = this.playersInfo.get(player);
        return info == null ? new byte[]{2, this.scale} : info.getPlayers(stack);
    }

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean state) {
        this.dirty = state;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setColumnDirty(int col, int min, int max) {
        this.markDirty();

        for (MapInfo mapInfo : this.players) {
            if (mapInfo.xCoords[col] < 0 || mapInfo.xCoords[col] > min) {
                mapInfo.xCoords[col] = min;
            }

            if (mapInfo.yCoords[col] < 0 || mapInfo.yCoords[col] < max) {
                mapInfo.yCoords[col] = max;
            }
        }
    }

    public void updateMPMapData(byte[] bytes) {
        if (bytes[0] == 0) {
            int x = bytes[1] & 255;
            int z = bytes[2] & 255;

            for (int index = 0; index < bytes.length - 3; index++) {
                this.colors[(index + z) * 128 + x] = bytes[index + 3];
            }

            this.markDirty();
        } else if (bytes[0] == 1) {
            this.playersVisible.clear();

            for (int index = 0; index < (bytes.length - 1) / 3; index++) {
                byte scale = (byte)(bytes[index * 3 + 1] >> 4);
                byte x = bytes[index * 3 + 2];
                byte z = bytes[index * 3 + 3];
                byte rot = (byte)(bytes[index * 3 + 1] & 15);
                this.playersVisible.put("icon-" + index, new MapCoords(this, scale, x, z, rot));
            }
        } else if (bytes[0] == 2) {
            this.scale = bytes[1];
        }
    }

    public MapInfo getMapInfo(Player player) {
        MapInfo info = this.playersInfo.get(player);
        if (info == null) {
            info = new MapInfo(this, player);
            this.playersInfo.put(player, info);
            this.players.add(info);
        }

        return info;
    }

    public void calculateMapCenter(double x, double z, int mapScale) {
        int i = 128 * (1 << mapScale);
        int j = MathHelper.floor_double((x + 64.0) / i);
        int k = MathHelper.floor_double((z + 64.0) / i);
        this.xCenter = j * i + i / 2 - 64;
        this.zCenter = k * i + i / 2 - 64;
    }
}
