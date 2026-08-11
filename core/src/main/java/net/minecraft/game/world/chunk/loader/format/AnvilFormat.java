package net.minecraft.game.world.chunk.loader.format;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.game.world.biomes.providers.SingleProvider;
import net.minecraft.game.world.chunk.NibbleArray;
import net.minecraft.game.world.chunk.loader.handler.AnvilSaveHandler;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.chunk.loader.region.RegionFile;
import net.minecraft.game.world.chunk.loader.region.RegionFileCache;
import net.minecraft.util.ProgressBar;
import net.minecraft.util.math.MathHelper;

public class AnvilFormat extends SaveFormatBase {
    public static final Pattern chunkFilePattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

    public AnvilFormat(File file) {
        super(file);
    }

    @Override
    public String getFormatName() {
        return "Anvil+";
    }

    @Override
    public List<FormatComparator> getSaveList() {
        ArrayList<FormatComparator> arraylist = new ArrayList<>();

        for (File file : Objects.requireNonNull(this.savesDirectory.listFiles())) {
            if (file.isDirectory()) {
                String filename = file.getName();
                WorldInfo worldinfo = this.getWorldInfo(filename);
                if (worldinfo != null) {
                    boolean flag = worldinfo.getConversionStatus() != this.formatSaveVersion();
                    String name = worldinfo.getWorldName();
                    String version = worldinfo.getVersion();
                    if (name == null || MathHelper.stringNullOrLengthZero(name)) {
                        name = filename;
                    }

                    long l = 0L;
                    arraylist.add(
                        new FormatComparator(filename, name, version, worldinfo.getLastTimePlayed(), l, worldinfo.getGamemode(), flag, worldinfo.getCheats())
                    );
                }
            }
        }

        return arraylist;
    }

    protected int formatSaveVersion() {
        return 19133;
    }

    @Override
    public void flushCache() {
        RegionFileCache.clearFileReferences();
    }

    @Override
    public SaveHandler getSaveLoader(String dirPath, boolean force) {
        return new AnvilSaveHandler(this.savesDirectory, dirPath, force);
    }

    @Override
    public boolean isOldMapFormat(String name) {
        WorldInfo worldinfo = this.getWorldInfo(name);
        return worldinfo != null && worldinfo.getConversionStatus() != this.formatSaveVersion();
    }

    @Override
    public boolean convertMapFormat(String name, ProgressBar progress) {
        progress.setLoadingProgress(0);
        ArrayList<File> folders = new ArrayList<>();
        ArrayList<AnvilFormat.ChunkFile> chunkFiles = new ArrayList<>();
        File file = new File(this.savesDirectory, name);
        System.out.println("Scanning folders...");
        this.addChunkFiles(file, chunkFiles, folders);
        int total = folders.size() + chunkFiles.size();
        System.out.println("Total conversion count is " + total);
        this.convertChunks(file, chunkFiles, 0, total, progress);
        WorldInfo worldinfo = this.getWorldInfo(name);
        worldinfo.setConversionStatus(19133);
        SaveHandler isavehandler = this.getSaveLoader(name, false);
        isavehandler.saveWorldInfo(worldinfo, null);
        this.cleanupOldFiles(folders, chunkFiles.size(), total, progress);
        return true;
    }

    private void convertChunks(File directory, ArrayList<AnvilFormat.ChunkFile> chunkFiles, int start, int end, ProgressBar progress) {
        Collections.sort(chunkFiles);

        for (AnvilFormat.ChunkFile file : chunkFiles) {
            int x = file.getChunkX();
            int z = file.getChunkZ();
            RegionFile var11 = RegionFileCache.getRegionFile(directory, x, z);
            if (!var11.isChunkSaved(x & 31, z & 31)) {
                try {
                    DataInputStream io = new DataInputStream(new GZIPInputStream(new FileInputStream(file.getFile())));
                    CompoundTag levelTag = NBTReader.read(io);
                    io.close();
                    CompoundTag chunkTag = levelTag.getCompoundTag("Level");
                    AnvilFormat.LegacyChunkData data = loadLegacyChunk(chunkTag);
                    CompoundTag newLevelTag = new CompoundTag();
                    CompoundTag newChunkTag = new CompoundTag();
                    newLevelTag.setTag("Level", newChunkTag);
                    convertChunk(data, newChunkTag, new SingleProvider(BiomeList.biomeDefault, Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE));
                    DataOutputStream out = var11.getDataOutputStream(x & 31, z & 31);
                    NBTReader.write(newLevelTag, out);
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            start++;
            int loadProgress = (int)Math.round(100.0 * start / end);
            progress.setLoadingProgress(loadProgress);
        }

        RegionFileCache.clearFileReferences();
    }

    private void cleanupOldFiles(ArrayList<File> files, int curr, int max, ProgressBar progress) {
        for (File file : files) {
            File[] filesList = file.listFiles();
            deleteFiles(filesList);
            file.delete();
            curr++;
            int loadProgress = (int)Math.round(100.0 * curr / max);
            progress.setLoadingProgress(loadProgress);
        }
    }

    private void addChunkFiles(File folder, ArrayList<AnvilFormat.ChunkFile> files, ArrayList<File> chunkFolders) {
        FileFilter chunkFolderFilter = pathname -> {
            if (pathname.isDirectory()) {
                Matcher match = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])").matcher(pathname.getName());
                return match.matches();
            } else {
                return false;
            }
        };
        FilenameFilter chunkFileFilter = (pathname, name) -> {
            Matcher match = chunkFilePattern.matcher(name);
            return match.matches();
        };

        for (File chunkFolder : folder.listFiles(chunkFolderFilter)) {
            chunkFolders.add(chunkFolder);

            for (File chunkFile : chunkFolder.listFiles(chunkFolderFilter)) {
                for (File finalFile : chunkFile.listFiles(chunkFileFilter)) {
                    files.add(new AnvilFormat.ChunkFile(finalFile));
                }
            }
        }
    }

    public static AnvilFormat.LegacyChunkData loadLegacyChunk(CompoundTag tag) {
        int x = tag.getInteger("xPos");
        int z = tag.getInteger("zPos");
        AnvilFormat.LegacyChunkData data = new AnvilFormat.LegacyChunkData(x, z);
        data.blocks = tag.getByteArray("Blocks");
        data.data = new AnvilFormat.LegacyNibbleArray(tag.getByteArray("Data"), 7);
        data.skyLight = new AnvilFormat.LegacyNibbleArray(tag.getByteArray("SkyLight"), 7);
        data.blockLight = new AnvilFormat.LegacyNibbleArray(tag.getByteArray("BlockLight"), 7);
        data.heightmap = tag.getByteArray("HeightMap");
        data.terrainPopulated = tag.getBoolean("TerrainPopulated");
        data.entities = tag.getTagList("Entities");
        data.tileEntities = tag.getTagList("TileEntities");
        data.tileTicks = tag.getTagList("TileTicks");

        try {
            data.lastUpdated = tag.getLong("LastUpdate");
        } catch (ClassCastException classcastexception) {
            data.lastUpdated = tag.getInteger("LastUpdate");
        }

        return data;
    }

    public static void convertChunk(AnvilFormat.LegacyChunkData data, CompoundTag tag, BiomeProvider biomeProvider) {
        tag.setInteger("xPos", data.x);
        tag.setInteger("zPos", data.z);
        tag.setLong("LastUpdate", data.lastUpdated);
        int[] ai = new int[data.heightmap.length];

        for (int i = 0; i < data.heightmap.length; i++) {
            ai[i] = data.heightmap[i];
        }

        tag.setIntArray("HeightMap", ai);
        tag.setBoolean("TerrainPopulated", data.terrainPopulated);
        ListTag<CompoundTag> nbttaglist = new ListTag<ListTag<CompoundTag>>().setKey("Sections");

        for (int section = 0; section < 8; section++) {
            boolean isEmpty = true;

            for (int x = 0; x < 16 && isEmpty; x++) {
                for (int y = 0; y < 16 && isEmpty; y++) {
                    for (int z = 0; z < 16; z++) {
                        int l1 = x << 11 | z << 7 | section << 4 | y;
                        byte byte0 = data.blocks[l1];
                        if (byte0 != 0) {
                            isEmpty = false;
                            break;
                        }
                    }
                }
            }

            if (!isEmpty) {
                byte[] blocks = new byte[4096];
                byte[] metadata = new byte[4096];
                NibbleArray skyLight = new NibbleArray(blocks.length, 4);
                NibbleArray blockLight = new NibbleArray(blocks.length, 4);

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            int l2 = x << 11 | z << 7 | section << 4 | y;
                            byte byte1 = data.blocks[l2];
                            blocks[y << 8 | z << 4 | x] = (byte)(byte1 & 0xFF);
                            metadata[y << 8 | z << 4 | x] = (byte)(data.data.get(x, section << 4 | y, z) & 15);
                            skyLight.set(x, y, z, data.skyLight.get(x, section << 4 | y, z));
                            blockLight.set(x, y, z, data.blockLight.get(x, section << 4 | y, z));
                        }
                    }
                }

                CompoundTag nbttagcompound = new CompoundTag();
                nbttagcompound.setByte("Y", (byte)(section & 0xFF));
                nbttagcompound.setByteArray("Blocks", blocks);
                nbttagcompound.setByteArray("Data", metadata);
                nbttagcompound.setByteArray("SkyLight", skyLight.data);
                nbttagcompound.setByteArray("BlockLight", blockLight.data);
                nbttaglist.add(nbttagcompound);
            }
        }

        tag.setTag("Sections", nbttaglist);
        byte[] biomes = new byte[256];
        Arrays.fill(biomes, (byte)1);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomes[z << 4 | x] = (byte)(biomeProvider.getBiomeAt(data.x << 4 | x, data.z << 4 | z).id & 0xFF);
            }
        }

        tag.setByteArray("Biomes", biomes);
        tag.setTag("Entities", data.entities);
        tag.setTag("TileEntities", data.tileEntities);
        if (data.tileTicks != null) {
            tag.setTag("TileTicks", data.tileTicks);
        }
    }

    static class ChunkFile implements Comparable<AnvilFormat.ChunkFile> {
        private final File file;
        private final int chunkX;
        private final int chunkZ;

        public ChunkFile(File file) {
            this.file = file;
            Matcher match = AnvilFormat.chunkFilePattern.matcher(file.getName());
            if (match.matches()) {
                this.chunkX = Integer.parseInt(match.group(1), 36);
                this.chunkZ = Integer.parseInt(match.group(2), 36);
            } else {
                this.chunkX = 0;
                this.chunkZ = 0;
            }
        }

        public int compareTo(AnvilFormat.ChunkFile file) {
            int thisX = this.chunkX >> 5;
            int otherX = file.chunkX >> 5;
            if (thisX == otherX) {
                int thisZ = this.chunkZ >> 5;
                int otherZ = file.chunkZ >> 5;
                return thisZ - otherZ;
            } else {
                return thisX - otherX;
            }
        }

        public File getFile() {
            return this.file;
        }

        public int getChunkX() {
            return this.chunkX;
        }

        public int getChunkZ() {
            return this.chunkZ;
        }
    }

    public static class LegacyChunkData {
        public AnvilFormat.LegacyNibbleArray blockLight;
        public AnvilFormat.LegacyNibbleArray skyLight;
        public AnvilFormat.LegacyNibbleArray data;
        public ListTag<CompoundTag> entities;
        public ListTag<CompoundTag> tileEntities;
        public ListTag<CompoundTag> tileTicks;
        public byte[] heightmap;
        public byte[] blocks;
        public final int x;
        public final int z;
        public long lastUpdated;
        public boolean terrainPopulated;

        public LegacyChunkData(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }

    public static class LegacyNibbleArray {
        public final byte[] data;
        private final int depthBits;
        private final int quadBits;

        public LegacyNibbleArray(byte[] arr, int bits) {
            this.data = arr;
            this.depthBits = bits;
            this.quadBits = bits + 4;
        }

        public int get(int x, int y, int z) {
            int i = x << this.quadBits | z << this.depthBits | y;
            int j = i >> 1;
            int k = i & 1;
            return k == 0 ? this.data[j] & 15 : this.data[j] >> 4 & 15;
        }
    }
}
