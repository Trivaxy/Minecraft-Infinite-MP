package net.minecraft.game.world.chunk.loader;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkSection;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.chunk.NibbleArray;
import net.minecraft.game.world.chunk.loader.region.RegionFileCache;
import net.minecraft.game.world.util.NextTickListEntry;
import net.minecraft.util.io.IThreadedFileIO;
import net.minecraft.util.io.ThreadedFileIOBase;

public class AnvilLoader implements IThreadedFileIO, FileLoader {
    static Logger logger = Logger.getLogger("Minecraft");
    private final ObjectList<AnvilLoader.PendingLoader> chunksToRemove = new ObjectArrayList<>();
    private final LongSet chunkCoordinates = new LongOpenHashSet();
    private final Object syncLock = new Object();
    private final File saveDir;

    public AnvilLoader(File file) {
        this.saveDir = file;
    }

    @Override
    public Chunk loadChunk(World world, int x, int z) throws IOException {
        CompoundTag nbttagcompound = null;
        long chunkPosition = ChunkUtils.getPackedCoords(x, z);
        synchronized (this.syncLock) {
            if (this.chunkCoordinates.contains(chunkPosition)) {
                for (AnvilLoader.PendingLoader pending : this.chunksToRemove) {
                    if (pending.chunkPos == chunkPosition) {
                        nbttagcompound = pending.chunkTag;
                        break;
                    }
                }
            }
        }

        if (nbttagcompound == null) {
            nbttagcompound = RegionFileCache.getInputStream(this.saveDir, x, z);
            if (nbttagcompound == null) {
                return null;
            }
        }

        return this.readChunkFromNBT(world, x, z, nbttagcompound);
    }

    protected Chunk readChunkFromNBT(World world, int x, int z, CompoundTag tag) {
        if (!tag.hasKey("Level")) {
            logger.warning("Chunk file at " + x + "," + z + " is missing level data, skipping");
            return null;
        }

        if (!tag.getCompoundTag("Level").hasKey("Sections")) {
            logger.warning("Chunk file at " + x + "," + z + " is missing block data, skipping");
            return null;
        }

        Chunk chunk = this.readChunkFromNBT(world, tag.getCompoundTag("Level"));
        if (!chunk.isAtLocation(x, z)) {
            logger.warning(
                "Chunk file at "
                    + x
                    + ","
                    + z
                    + " is in the wrong location; relocating. (Expected "
                    + x
                    + ", "
                    + z
                    + ", got "
                    + chunk.xPosition
                    + ", "
                    + chunk.zPosition
                    + ")"
            );
            tag.setInteger("xPos", x);
            tag.setInteger("zPos", z);
            chunk = this.readChunkFromNBT(world, tag.getCompoundTag("Level"));
        }

        chunk.removeInvalidBlocks();
        return chunk;
    }

    @Override
    public void saveChunk(World world, Chunk chunk) throws IOException {
        world.checkSessionLock();

        try {
            CompoundTag nbttagcompound = new CompoundTag();
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound.setTag("Level", nbttagcompound1);
            this.writeChunkToNBT(chunk, world, nbttagcompound1);
            this.queueNextIO(ChunkUtils.getPackedCoords(chunk.xPosition, chunk.zPosition), nbttagcompound);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void queueNextIO(long chunkPos, CompoundTag par2NBTTagCompound) {
        synchronized (this.syncLock) {
            if (this.chunkCoordinates.contains(chunkPos)) {
                for (int i = 0; i < this.chunksToRemove.size(); i++) {
                    if (this.chunksToRemove.get(i).chunkPos == chunkPos) {
                        this.chunksToRemove.set(i, new AnvilLoader.PendingLoader(chunkPos, par2NBTTagCompound));
                        return;
                    }
                }
            }

            this.chunksToRemove.add(new AnvilLoader.PendingLoader(chunkPos, par2NBTTagCompound));
            this.chunkCoordinates.add(chunkPos);
            ThreadedFileIOBase.threadedIOInstance.queueIO(this);
        }
    }

    @Override
    public boolean writeNextIO() {
        AnvilLoader.PendingLoader loader;
        synchronized (this.syncLock) {
            if (this.chunksToRemove.isEmpty()) {
                return false;
            }

            loader = this.chunksToRemove.remove(0);
            this.chunkCoordinates.remove(loader.chunkPos);
        }

        try {
            this.writeChunkNBTTags(loader);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return true;
    }

    private void writeChunkNBTTags(AnvilLoader.PendingLoader loader) throws IOException {
        RegionFileCache.getOutputStream(this.saveDir, ChunkUtils.getChunkX(loader.chunkPos), ChunkUtils.getChunkZ(loader.chunkPos), loader.chunkTag);
    }

    private void writeChunkToNBT(Chunk chunk, World world, CompoundTag tag) {
        world.checkSessionLock();
        tag.setInteger("xPos", chunk.xPosition);
        tag.setInteger("zPos", chunk.zPosition);
        tag.setLong("LastUpdate", world.getWorldTime());
        tag.setIntArray("HeightMap", chunk.heightMap);
        tag.setBoolean("TerrainPopulated", chunk.isPopulated);
        ChunkSection[] sections = chunk.getSections();
        ListTag<CompoundTag> nbttaglist = new ListTag<>();
        nbttaglist.setKey("Sections");

        for (ChunkSection section : sections) {
            if (section != null && section.getBlocks() != 0) {
                CompoundTag nbttagcompound = new CompoundTag();
                nbttagcompound.setByte("Y", (byte)(section.getY() >> 4 & 0xFF));
                nbttagcompound.setByteArray("Blocks", section.getLowerBlockArray());
                if (section.getUpperBlockArray() != null) {
                    nbttagcompound.setByteArray("Add", section.getUpperBlockArray().data);
                }

                nbttagcompound.setByteArray("Data", section.getBlockMetadataArray());
                nbttagcompound.setByteArray("SkyLight", section.getSkylightArray().data);
                nbttagcompound.setByteArray("BlockLight", section.getBlocklightArray().data);
                nbttaglist.add(nbttagcompound);
            }
        }

        tag.setTag("Sections", nbttaglist);
        tag.setByteArray("Biomes", chunk.getBiomes());
        chunk.hasEntities = false;
        ListTag<CompoundTag> entityTagList = new ListTag<>();
        List[] var17 = chunk.entities;
        int var19 = var17.length;

        for (int var21 = 0; var21 < var19; var21++) {
            for (Object candidate : var17[var21]) {
                Entity entity = (Entity)candidate;
                chunk.hasEntities = true;
                CompoundTag entityTag = new CompoundTag();
                if (entity.addEntityID(entityTag)) {
                    entityTagList.add(entityTag);
                }
            }
        }

        tag.setTag("Entities", entityTagList);
        ListTag<CompoundTag> teTagList = new ListTag<>();

        for (TileEntity tileEntity : chunk.tileEntities.values()) {
            CompoundTag teTag = new CompoundTag();
            tileEntity.writeToNBT(teTag);
            teTagList.add(teTag);
        }

        tag.setTag("TileEntities", teTagList);
        List<NextTickListEntry> list = this.getPendingTicks(world, chunk, false);
        if (list != null) {
            long time = world.getWorldTime();
            ListTag<CompoundTag> tagList = new ListTag<>();

            for (NextTickListEntry nextticklistentry : list) {
                CompoundTag blockTag = new CompoundTag();
                blockTag.setInteger("i", nextticklistentry.blockID);
                blockTag.setInteger("x", nextticklistentry.xCoord);
                blockTag.setInteger("y", nextticklistentry.yCoord);
                blockTag.setInteger("z", nextticklistentry.zCoord);
                blockTag.setInteger("t", (int)(nextticklistentry.delay - time));
                tagList.add(blockTag);
            }

            tag.setTag("TileTicks", tagList);
        }
    }

    private Chunk readChunkFromNBT(World world, CompoundTag tag) {
        int x = tag.getInteger("xPos");
        int z = tag.getInteger("zPos");
        Chunk chunk = new Chunk(world, x, z);
        chunk.heightMap = tag.getIntArray("HeightMap");
        chunk.isPopulated = tag.getBoolean("TerrainPopulated");
        ListTag<? extends Tag> nbttaglist = tag.getTagList("Sections");
        byte maxSections = (byte)world.getMaxSections();
        if (maxSections == 0) {
            maxSections = 16;
        }

        world.setMaxSections(maxSections);
        ChunkSection[] sections = new ChunkSection[maxSections];

        for (int k = 0; k < nbttaglist.size(); k++) {
            CompoundTag nbttagcompound = (CompoundTag)nbttaglist.get(k);
            byte yValue = nbttagcompound.getByte("Y");
            ChunkSection section = new ChunkSection(yValue << 4);
            section.setLowerBlockArray(nbttagcompound.getByteArray("Blocks"));
            if (nbttagcompound.hasKey("Add")) {
                section.setUpperBlockArray(new NibbleArray(nbttagcompound.getByteArray("Add"), 4));
            }

            section.setBlockMetadataArray(nbttagcompound.getByteArray("Data"));
            section.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight"), 4));
            section.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight"), 4));
            section.calculateBlocks();
            sections[yValue] = section;
        }

        chunk.setSections(sections);
        if (tag.hasKey("Biomes")) {
            chunk.setBiomes(tag.getByteArray("Biomes"));
        }

        ListTag<? extends Tag> entities = tag.getTagList("Entities");
        if (entities != null) {
            for (int index = 0; index < entities.size(); index++) {
                CompoundTag entityTagAt = (CompoundTag)entities.get(index);
                Entity entity = EntityManager.createEntityFromNBT(entityTagAt, world);
                chunk.hasEntities = true;
                if (entity != null) {
                    chunk.addEntity(entity);
                    Entity rider = entity;

                    for (CompoundTag riderTag = entityTagAt; riderTag.hasExtraValues("Riding", 10); riderTag = riderTag.getCompoundTag("Riding")) {
                        Entity mount = EntityManager.createEntityFromNBT(riderTag.getCompoundTag("Riding"), world);
                        if (mount != null) {
                            chunk.addEntity(mount);
                            rider.mountEntity(mount);
                        }

                        rider = mount;
                    }
                }
            }
        }

        ListTag<? extends Tag> tileEntities = tag.getTagList("TileEntities");
        if (tileEntities != null) {
            for (int index = 0; index < tileEntities.size(); index++) {
                CompoundTag compoundTag = (CompoundTag)tileEntities.get(index);
                TileEntity tileentity = TileEntity.loadFromNBT(compoundTag);
                if (tileentity != null) {
                    chunk.addTileEntity(tileentity);
                }
            }
        }

        if (tag.hasKey("TileTicks")) {
            ListTag<? extends Tag> ticks = tag.getTagList("TileTicks");
            if (ticks != null) {
                for (int index = 0; index < ticks.size(); index++) {
                    CompoundTag ticksTag = (CompoundTag)ticks.get(index);
                    this.forceTickFromLoad(
                        world, ticksTag.getInteger("x"), ticksTag.getInteger("y"), ticksTag.getInteger("z"), ticksTag.getInteger("i"), ticksTag.getInteger("t")
                    );
                }
            }
        }

        return chunk;
    }

    public List<NextTickListEntry> getPendingTicks(World world, Chunk chunk, boolean clear) {
        ArrayList<NextTickListEntry> nextTickList = null;
        int minX = chunk.xPosition << 4;
        int maxX = minX + 16;
        int minZ = chunk.zPosition << 4;
        int maxZ = minZ + 16;

        for (NextTickListEntry tick : world.tickSet) {
            if (tick.xCoord >= minX && tick.xCoord < maxX && tick.zCoord >= minZ && tick.zCoord < maxZ) {
                if (clear) {
                    world.tickList.remove(tick);
                }

                if (nextTickList == null) {
                    nextTickList = new ArrayList<>();
                }

                nextTickList.add(tick);
            }
        }

        return nextTickList;
    }

    public void forceTickFromLoad(World world, int x, int y, int z, int id, int time) {
        NextTickListEntry tick = new NextTickListEntry(x, y, z, id);
        if (id > 0) {
            tick.setDelay(time + world.settings.getWorldTime());
        }

        if (!world.tickSet.contains(tick)) {
            world.tickSet.add(tick);
            world.tickList.add(tick);
        }
    }

    private static class PendingLoader {
        public final long chunkPos;
        public final CompoundTag chunkTag;

        public PendingLoader(long chunkPos, CompoundTag chunkTag) {
            this.chunkPos = chunkPos;
            this.chunkTag = chunkTag;
        }
    }
}
