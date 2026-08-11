package net.minecraft.game.item.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.mapdata.MapColor;
import net.minecraft.game.world.mapdata.MapCoords;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.game.world.mapdata.MapInfo;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.world.MapPacket;
import net.minecraft.util.math.MathHelper;

public class MapItem extends Item {
    public MapItem(int id) {
        super(id);
        this.setHasSubtypes(true);
    }

    public static MapData getMPMapData(short mapID, World world) {
        String mapName = "map_" + mapID;
        MapData data = world.loadMapData(MapData.class, mapName);
        if (data == null) {
            data = new MapData(mapName);
            world.setMapData(mapName, data);
        }

        return data;
    }

    public MapData getMapData(ItemStack stack, World world) {
        String mapID = "map_" + stack.getItemDamage();
        MapData data = world.loadMapData(MapData.class, mapID);
        if (data == null) {
            stack.setItemDamage(world.getMapId("map"));
            mapID = "map_" + stack.getItemDamage();
            data = new MapData(mapID);
            data.scale = 3;
            data.calculateMapCenter(world.getSettings().getSpawnX(), world.getSettings().getSpawnZ(), data.scale);
            data.dimension = (byte)world.currDim.dimension;
            data.markDirty();
            world.setMapData(mapID, data);
        }

        return data;
    }

    public void updateMapData(World world, Entity entity, MapData data) {
        if (world.currDim.dimension == data.dimension && entity instanceof Player) {
            short width = 128;
            short height = 128;
            int scale = 1 << data.scale;
            int centerX = data.xCenter;
            int centerZ = data.zCenter;
            int playerX = MathHelper.floor_double(entity.posX - centerX) / scale + width / 2;
            int playerZ = MathHelper.floor_double(entity.posZ - centerZ) / scale + height / 2;
            int realScale = 128 / scale;
            MapInfo info = data.getMapInfo((Player)entity);
            info.updateTicks++;

            for (int offsettedX = playerX - realScale + 1; offsettedX < playerX + realScale; offsettedX++) {
                if ((offsettedX & 15) == (info.updateTicks & 15)) {
                    int col = 255;
                    int ticks = 0;
                    double zoomVal = 0.0;

                    for (int offsettedZ = playerZ - realScale - 1; offsettedZ < playerZ + realScale; offsettedZ++) {
                        if (offsettedX >= 0 && offsettedZ >= -1 && offsettedX < width && offsettedZ < height) {
                            int realX = offsettedX - playerX;
                            int realZ = offsettedZ - playerZ;
                            boolean hasPlayerMoved = realX * realX + realZ * realZ > (realScale - 2) * (realScale - 2);
                            int chunkX = (centerX / scale + offsettedX - width / 2) * scale;
                            int chunkZ = (centerZ / scale + offsettedZ - height / 2) * scale;
                            int[] ids = new int[4096];
                            Chunk currChunk = world.getChunkAt(chunkX, chunkZ);
                            if (!currChunk.isLoaded()) {
                                int posX = chunkX & 15;
                                int posZ = chunkZ & 15;
                                int increments = 0;
                                double zoom = 0.0;

                                for (int val1 = 0; val1 < scale; val1++) {
                                    for (int val2 = 0; val2 < scale; val2++) {
                                        int val3 = currChunk.getHeightmap(val1 + posX, val2 + posZ) + 1;
                                        if (data.dimension == 1) {
                                            val3 = world.getSeaLevel();
                                        }

                                        if (data.unlockHeight) {
                                            val3 = (int)entity.posY;
                                        }

                                        int block = 0;
                                        if (val3 > 1) {
                                            boolean visible;
                                            do {
                                                visible = true;
                                                block = currChunk.getBlockID(val1 + posX, val3 - 1, val2 + posZ);
                                                if (block == 0) {
                                                    visible = false;
                                                } else if (val3 > 0 && block > 0 && BlockList.blocks[block].material.mapColor == MapColor.air) {
                                                    visible = false;
                                                }

                                                int pixelX = chunkX / scale - data.xCenter / scale;
                                                int pixelZ = chunkZ / scale - data.zCenter / scale;
                                                boolean hasFlag = false;
                                                byte flagMeta = 0;

                                                for (int bx = 0; bx < scale; bx++) {
                                                    for (int bz = 0; bz < scale; bz++) {
                                                        int x = posX + bx;
                                                        int z = posZ + bz;
                                                        int y = currChunk.getHeightmap(x, z) + 1;
                                                        int blockID = currChunk.getBlockID(x, y, z);
                                                        if (blockID == BlockList.flag.id) {
                                                            hasFlag = true;
                                                            flagMeta = (byte)((currChunk.getBlockMetadata(x, y, z) - 1) / 4);
                                                        }
                                                    }
                                                }

                                                if (!hasFlag) {
                                                    data.flags.removeIf(fx -> fx.centerX == pixelX && fx.centerZ == pixelZ);
                                                } else {
                                                    boolean exists = false;

                                                    for (MapCoords f : data.flags) {
                                                        if (f.centerX == pixelX && f.centerZ == pixelZ) {
                                                            exists = true;
                                                            break;
                                                        }
                                                    }

                                                    if (!exists) {
                                                        data.flags.add(new MapCoords(data, (byte)7, (byte)pixelX, (byte)pixelZ, flagMeta));
                                                    }
                                                }

                                                if (!visible) {
                                                    if (--val3 <= 0) {
                                                        break;
                                                    }

                                                    block = currChunk.getBlockID(val1 + posX, val3 - 1, val2 + posZ);
                                                }
                                            } while (val3 > 0 && !visible);

                                            if (val3 > 0 && block != 0 && BlockList.blocks[block].material.getIsLiquid()) {
                                                int val4 = val3 - 1;

                                                int var41;
                                                do {
                                                    var41 = currChunk.getBlockID(val1 + posX, val4--, val2 + posZ);
                                                    increments++;
                                                } while (val4 > 0 && var41 != 0 && BlockList.blocks[var41].material.getIsLiquid());
                                            }
                                        }

                                        zoom += (double)val3 / (scale * scale);
                                        ids[block]++;
                                    }
                                }

                                increments /= scale * scale;
                                int var48 = 0;
                                int val2 = 0;

                                for (int val3 = 0; val3 < 4096; val3++) {
                                    if (ids[val3] > var48) {
                                        val2 = val3;
                                        var48 = ids[val3];
                                    }
                                }

                                double zoomScale = (zoom - zoomVal) * 4.0 / (scale + 4) + ((offsettedX + offsettedZ & 1) - 0.5) * 0.4;
                                byte zoomLvl = 1;
                                if (zoomScale > 0.6) {
                                    zoomLvl = 2;
                                }

                                if (zoomScale < -0.6) {
                                    zoomLvl = 0;
                                }

                                int val4 = 0;
                                if (val2 > 0) {
                                    MapColor color = BlockList.blocks[val2].material.mapColor;
                                    if (color == MapColor.water) {
                                        zoomScale = increments * 0.1 + (offsettedX + offsettedZ & 1) * 0.2;
                                        zoomLvl = 1;
                                        if (zoomScale < 0.5) {
                                            zoomLvl = 2;
                                        }

                                        if (zoomScale > 0.9) {
                                            zoomLvl = 0;
                                        }
                                    }

                                    val4 = color.index;
                                }

                                zoomVal = zoom;
                                if (offsettedZ >= 0
                                    && realX * realX + realZ * realZ < realScale * realScale
                                    && (!hasPlayerMoved || (offsettedX + offsettedZ & 1) != 0)) {
                                    byte colVal = data.colors[offsettedX + offsettedZ * width];
                                    byte opacity = (byte)(val4 * 4 + zoomLvl);
                                    if (colVal != opacity) {
                                        if (col > offsettedZ) {
                                            col = offsettedZ;
                                        }

                                        if (ticks < offsettedZ) {
                                            ticks = offsettedZ;
                                        }

                                        data.colors[offsettedX + offsettedZ * width] = opacity;
                                    }
                                }
                            }
                        }
                    }

                    if (col <= ticks) {
                        data.setColumnDirty(offsettedX, col, ticks);
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int e, boolean shouldTick) {
        if (!world.isOnline) {
            MapData data = this.getMapData(stack, world);
            if (entity instanceof Player) {
                data.updateVisiblePlayers((Player)entity, stack);
            }

            if (shouldTick) {
                this.updateMapData(world, entity, data);
            }
        }
    }

    public Packet createMapDataPacket(ItemStack stack, World world, Player player) {
        byte[] dataPoints = this.getMapData(stack, world).getUpdatePacketData(stack, world, player);
        return dataPoints == null ? null : new MapPacket((short)ItemList.map.id, (short)stack.getItemDamage(), dataPoints);
    }

    @Override
    public void onCrafted(ItemStack stack, World world, Player player) {
        if (stack.hasTag() && stack.getTag().getBoolean("map_is_scaling")) {
            MapData data = ItemList.map.getMapData(stack, world);
            stack.setItemDamage(world.getMapId("map"));
            MapData realData = new MapData("map_" + stack.getItemDamage());
            realData.scale = (byte)(data.scale + 1);
            if (realData.scale > 4) {
                realData.scale = 4;
            }

            realData.calculateMapCenter(data.xCenter, data.zCenter, realData.scale);
            realData.dimension = data.dimension;
            realData.markDirty();
            world.setMapData("map_" + stack.getItemDamage(), realData);
        }

        if (stack.hasTag() && stack.getTag().getBoolean("caves_map")) {
            MapData data = ItemList.map.getMapData(stack, world);
            stack.setItemDamage(world.getMapId("map"));
            MapData realData = new MapData("map_" + stack.getItemDamage());
            realData.unlockHeight = true;
            realData.scale = data.scale;
            realData.calculateMapCenter(data.xCenter, data.zCenter, data.scale);
            realData.dimension = data.dimension;
            realData.markDirty();
            world.setMapData("map_" + stack.getItemDamage(), realData);
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return true;
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
