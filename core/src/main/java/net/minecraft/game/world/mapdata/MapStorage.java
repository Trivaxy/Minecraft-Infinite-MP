package net.minecraft.game.world.mapdata;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.primitives.ShortTag;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;

public class MapStorage {
    private final SaveHandler saveHandler;
    private final Map<String, MapData> loadedMap = new HashMap<>();
    private final List<MapData> loadedData = new ArrayList<>();
    private final Map<String, Short> ids = new HashMap<>();

    public MapStorage(SaveHandler save) {
        this.saveHandler = save;
        this.loadIds();
    }

    public MapData loadData(Class<? extends MapData> data, String name) {
        MapData mapData = this.loadedMap.get(name);
        if (mapData != null) {
            return mapData;
        }

        if (this.saveHandler != null) {
            try {
                File file = this.saveHandler.getMapFileFromName(name);
                if (file != null && file.exists()) {
                    try {
                        mapData = data.getConstructor(String.class).newInstance(name);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to instantiate " + data.toString(), ex);
                    }

                    FileInputStream inputStream = new FileInputStream(file);
                    CompoundTag tag = NBTReader.readCompressed(inputStream);
                    inputStream.close();
                    mapData.readFromNBT(tag.getCompoundTag("data"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (mapData != null) {
            this.loadedMap.put(name, mapData);
            this.loadedData.add(mapData);
        }

        return mapData;
    }

    public void setData(String name, MapData data) {
        if (data == null) {
            throw new RuntimeException("Can't set null data");
        }

        if (this.loadedMap.containsKey(name)) {
            this.loadedData.remove(this.loadedMap.remove(name));
        }

        this.loadedMap.put(name, data);
        this.loadedData.add(data);
    }

    public void saveAllData() {
        for (MapData data : this.loadedData) {
            this.saveData(data);
        }
    }

    private void saveData(MapData data) {
        if (this.saveHandler != null) {
            try {
                File file = this.saveHandler.getMapFileFromName(data.mapName);
                if (file != null) {
                    CompoundTag tag = new CompoundTag();
                    data.writeToNBT(tag);
                    CompoundTag dataTag = new CompoundTag();
                    dataTag.setCompoundTag("data", tag);
                    FileOutputStream output = new FileOutputStream(file);
                    NBTReader.writeCompressed(dataTag, output);
                    output.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadIds() {
        try {
            this.ids.clear();
            if (this.saveHandler == null) {
                return;
            }

            File file = this.saveHandler.getMapFileFromName("idcounts");
            if (file != null && file.exists()) {
                DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
                CompoundTag tag = NBTReader.read(inputStream);
                inputStream.close();

                for (Tag dataTag : tag.getTags()) {
                    if (dataTag instanceof ShortTag) {
                        ShortTag id = (ShortTag)dataTag;
                        String name = id.getKey();
                        short hash = id.shortValue;
                        this.ids.put(name, hash);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getUniqueId(String name) {
        Short id = this.ids.get(name);
        if (id == null) {
            id = (short)0;
        } else {
            id = (short)(id + 1);
        }

        this.ids.put(name, id);
        if (this.saveHandler == null) {
            return id;
        }

        try {
            File file = this.saveHandler.getMapFileFromName("idcounts");
            if (file != null) {
                CompoundTag tag = new CompoundTag();

                for (String identifier : this.ids.keySet()) {
                    short hash = this.ids.get(identifier);
                    tag.setShort(identifier, hash);
                }

                DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
                NBTReader.write(tag, out);
                out.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }
}
