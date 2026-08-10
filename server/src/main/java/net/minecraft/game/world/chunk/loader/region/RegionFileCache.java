package net.minecraft.game.world.chunk.loader.region;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class RegionFileCache {
    private static final Object2ObjectOpenHashMap<File, Reference<RegionFile>> cache = new Object2ObjectOpenHashMap<>();

    public static synchronized RegionFile getRegionFile(File dir, int chunkX, int chunkZ) {
        File folder = new File(dir, "region");
        File file = new File(folder, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
        Reference<RegionFile> reference = cache.get(file);
        if (reference != null) {
            RegionFile regionFile = reference.get();
            if (regionFile != null) {
                return regionFile;
            }
        }

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (cache.size() >= 256) {
            clearFileReferences();
        }

        RegionFile regionFile = new RegionFile(file);
        cache.put(file, new SoftReference<>(regionFile));
        return regionFile;
    }

    public static synchronized void clearFileReferences() {
        for (Reference<RegionFile> reference : cache.values()) {
            try {
                RegionFile regionFile = reference.get();
                if (regionFile != null) {
                    regionFile.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        cache.clear();
    }

    public static synchronized CompoundTag getInputStream(File file, int chunkX, int chunkZ) throws IOException {
        RegionFile regionfile = getRegionFile(file, chunkX, chunkZ);

        try (DataInputStream datainputstream = regionfile.getDataInputStream(chunkX & 31, chunkZ & 31)) {
            if (datainputstream != null) {
                return NBTReader.read(datainputstream);
            }
        }

        return null;
    }

    public static synchronized void getOutputStream(File file, int chunkX, int chunkZ, CompoundTag nbttagcompound) throws IOException {
        RegionFile regionfile = getRegionFile(file, chunkX, chunkZ);

        try (DataOutputStream dataoutputstream = regionfile.getDataOutputStream(chunkX & 31, chunkZ & 31)) {
            NBTReader.write(nbttagcompound, dataoutputstream);
        }
    }
}
