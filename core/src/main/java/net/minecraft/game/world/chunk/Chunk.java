package net.minecraft.game.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.util.math.MathHelper;

public class Chunk {
    public static Logger logger = Logger.getLogger("Minecraft");
    private ChunkSection[] sections;
    public Map<Integer, TileEntity> tileEntities;
    public List<Entity>[] entities;
    public BitSet skylightColumns;
    public World world;
    private byte[] biomes;
    public int[] precipitationMap;
    public int[] heightMap;
    public int[] landSurfaceHeightMap;
    public final int xPosition;
    public final int zPosition;
    public int minHeight;
    public long lastSaveTime;
    public static boolean isLit;
    public boolean isLoaded;
    public boolean isPopulated;
    public boolean isModified;
    public boolean isRendered;
    public boolean neverSave;
    public boolean beingDecorated;
    public boolean hasEntities;

    public Chunk(World world, int xCoord, int zCoord) {
        this.sections = new ChunkSection[world.getMaxSections()];
        this.biomes = new byte[256];
        this.precipitationMap = new int[256];
        this.skylightColumns = new BitSet(256);
        this.tileEntities = Collections.synchronizedMap(new HashMap<>());
        this.entities = new List[world.getMaxSections()];
        this.isPopulated = false;
        this.isModified = false;
        this.isRendered = false;
        this.hasEntities = false;
        this.lastSaveTime = 0L;
        this.world = world;
        this.xPosition = xCoord;
        this.zPosition = zCoord;
        this.heightMap = new int[256];
        this.landSurfaceHeightMap = new int[256];

        for (int entity = 0; entity < this.entities.length; entity++) {
            this.entities[entity] = new ArrayList<>();
        }

        Arrays.fill(this.precipitationMap, -999);
        Arrays.fill(this.biomes, (byte)-1);
    }

    public Chunk(World world, short[] blocks, byte[] metadata, int xCoord, int zCoord) {
        this(world, xCoord, zCoord);
        int height = blocks.length / 256;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < height; y++) {
                    short block = blocks[y << 8 | z << 4 | x];
                    byte meta = metadata[y << 8 | z << 4 | x];
                    int section = y >> 4;
                    if (this.sections[section] == null) {
                        this.sections[section] = new ChunkSection(section << 4);
                    }

                    this.sections[section].setBlockID(x, y & 15, z, block);
                    this.sections[section].setBlockMetadata(x, y & 15, z, meta);
                }
            }
        }
    }

    public boolean isAtLocation(int xCoord, int zCoord) {
        return xCoord == this.xPosition && zCoord == this.zPosition;
    }

    public int getHeightmap(int x, int z) {
        return this.heightMap[z << 4 | x];
    }

    public int getSurfaceHeightmap(int x, int z) {
        return this.landSurfaceHeightMap[z << 4 | x];
    }

    public int getTopFilledSegment() {
        for (int i = this.sections.length - 1; i >= 0; i--) {
            if (this.sections[i] != null) {
                return this.sections[i].getY();
            }
        }

        return 0;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public void generateHeightMap() {
        this.minHeight = this.world.getWorldHeight();
        int top = this.getTopFilledSegment();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                this.precipitationMap[x + (z << 4)] = -999;
                int y = top + 16 - 1;
                if (top == 48) {
                    y = top + 16;
                }

                while (y > 0) {
                    int light = this.getBlockLightOpacity(x, y - 1, z);
                    if (light != 0) {
                        this.heightMap[z << 4 | x] = y;
                        if (y < this.minHeight) {
                            this.minHeight = y;
                        }
                        break;
                    }

                    y--;
                }

                if (y == 0) {
                    this.minHeight = 0;
                }
            }
        }

        this.isModified = true;
    }

    public void generateSkylightMap() {
        int top = this.getTopFilledSegment();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                this.precipitationMap[x + (z << 4)] = -999;
                int sectionSize = 15;
                int y = top + 16 - 1;
                if (top == 48) {
                    y = top + 16;
                }

                do {
                    sectionSize -= this.getBlockLightOpacity(x, y, z);
                    if (sectionSize > 0) {
                        ChunkSection var6 = this.sections[y >> 4];
                        if (var6 != null) {
                            var6.setSkylightValue(x, y & 15, z, sectionSize);
                            this.world.queueAll((this.xPosition << 4) + x, y, (this.zPosition << 4) + z);
                        }
                    }
                } while (--y > 0 && sectionSize > 0);
            }
        }

        this.isModified = true;
    }

    private void relightBlock(int x, int y, int z) {
        int i = this.heightMap[z << 4 | x];
        int j = Math.max(y, i);

        while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
            j--;
        }

        if (j != i) {
            this.heightMap[z << 4 | x] = (short)j;
            int height = this.heightMap[z << 4 | x];
            if (height < this.minHeight) {
                this.minHeight = height;
            }

            this.isModified = true;
        }
    }

    public int getBlockLightOpacity(int x, int y, int z) {
        return Block.lightOpacity[this.getBlockID(x, y, z)];
    }

    public ChunkSection addSection(int chunkY) {
        if (this.sections[chunkY] == null) {
            this.sections[chunkY] = new ChunkSection(chunkY << 4);
        }

        return this.sections[chunkY];
    }

    public int getBlockID(int x, int y, int z) {
        if (y >> 4 < this.sections.length && y >> 4 >= 0) {
            ChunkSection section = this.sections[y >> 4];
            return section != null ? section.getBlockID(x, y & 15, z) : 0;
        } else {
            return 0;
        }
    }

    public boolean setBlockAndState(int x, int y, int z, int id, int meta) {
        int heightIndex = z << 4 | x;
        if (y >= this.precipitationMap[heightIndex] - 1) {
            this.precipitationMap[heightIndex] = -999;
        }

        int height = this.heightMap[heightIndex];
        int targetID = this.getBlockID(x, y, z);
        if (targetID == id && this.getBlockMetadata(x, y, z) == meta) {
            return false;
        }

        ChunkSection section = this.sections[y >> 4];
        boolean skipLighting = false;
        if (section == null) {
            if (id == 0) {
                return false;
            }

            section = this.sections[y >> 4] = new ChunkSection(y >> 4 << 4);
            skipLighting = y >= height;
        }

        section.setBlockID(x, y & 15, z, id);
        int xPos = this.xPosition * 16 + x;
        int zPos = this.zPosition * 16 + z;
        if (targetID != 0) {
            if (!this.world.isOnline) {
                BlockList.blocks[targetID].onBlockRemoval(this.world, xPos, y, zPos);
            } else if (BlockList.blocks[targetID] instanceof ContainerBlock && targetID != id) {
                this.world.removeBlockTileEntity(xPos, y, zPos);
            }
        }

        if (section.getBlockID(x, y & 15, z) != id) {
            return false;
        }

        section.setBlockMetadata(x, y & 15, z, meta);
        if (skipLighting) {
            this.generateSkylightMap();
            this.initLighting();
        } else if (Block.lightOpacity[id & 4095] > 0) {
            if (y >= height) {
                this.relightBlock(x, y + 1, z);
            }
        } else if (y == height - 1) {
            this.relightBlock(x, y, z);
        }

        this.updateLight(x, y, z);
        if (id > 0) {
            if (!this.world.isOnline) {
                BlockList.blocks[id].onBlockAdded(this.world, xPos, y, zPos);
            }

            if (BlockList.blocks[id] instanceof ContainerBlock) {
                TileEntity tileentity = this.getTileEntity(x, y, z);
                if (tileentity == null) {
                    tileentity = ((ContainerBlock)BlockList.blocks[id]).getBlockEntity();
                } else {
                    tileentity.tick();
                }
            }
        } else if (targetID > 0 && BlockList.blocks[targetID] instanceof ContainerBlock) {
            TileEntity tileentity1 = this.getTileEntity(x, y, z);
            if (tileentity1 != null) {
                tileentity1.tick();
            }
        }

        this.isModified = true;
        return true;
    }

    public boolean setBlockID(int x, int y, int z, int id) {
        return this.setBlockAndState(x, y, z, id, 0);
    }

    public int getBlockMetadata(int x, int y, int z) {
        if (y >> 4 >= this.sections.length) {
            return 0;
        }

        ChunkSection section = this.sections[y >> 4];
        return section != null ? section.getBlockMetadata(x, y & 15, z) : 0;
    }

    public void setBlockMetadata(int x, int y, int z, int meta) {
        ChunkSection section = this.sections[y >> 4];
        if (section != null) {
            int currMeta = section.getBlockMetadata(x, y & 15, z);
            if (currMeta != meta) {
                this.isModified = true;
                section.setBlockMetadata(x, y & 15, z, meta);
            }
        }
    }

    public int getLightValue(LightLayer layer, int x, int y, int z) {
        ChunkSection section = this.sections[y >> 4];
        if (section == null) {
            return layer.defaultLightValue;
        } else if (layer == LightLayer.Sky) {
            return section.getExtSkylightValue(x, y & 15, z);
        } else {
            return layer == LightLayer.Block ? section.getBlocklightValue(x, y & 15, z) : layer.defaultLightValue;
        }
    }

    public void setLightValue(LightLayer layer, int x, int y, int z, int light) {
        ChunkSection section = this.sections[y >> 4];
        if (section == null) {
            section = this.sections[y >> 4] = new ChunkSection(y >> 4 << 4);
            if (!this.world.isOnline) {
                this.initLighting();
            }

            this.generateSkylightMap();
        }

        this.isModified = true;
        if (layer == LightLayer.Sky) {
            section.setSkylightValue(x, y & 15, z, light);
        } else if (layer == LightLayer.Block) {
            section.setBlocklightValue(x, y & 15, z, light);
        }
    }

    public int getBlockLightValue(int x, int y, int z, int light) {
        ChunkSection section = this.sections[y >> 4];
        if (section == null) {
            return light < LightLayer.Sky.defaultLightValue ? LightLayer.Sky.defaultLightValue - light : 0;
        }

        int skyLight = section.getExtSkylightValue(x, y & 15, z);
        if (skyLight > 0) {
            isLit = true;
        }

        skyLight -= light;
        int blockLight = section.getBlocklightValue(x, y & 15, z);
        if (blockLight > skyLight) {
            skyLight = blockLight;
        }

        return skyLight;
    }

    public void addEntity(Entity entity) {
        this.hasEntities = true;
        int entityX = MathHelper.floor_double(entity.posX / 16.0);
        int entityZ = MathHelper.floor_double(entity.posZ / 16.0);
        if (entityX == this.xPosition && entityZ == this.zPosition) {
            int entityY = MathHelper.floor_double(entity.posY / 16.0);
            if (entityY < 0) {
                entityY = 0;
            }

            if (entityY >= this.entities.length) {
                entityY = this.entities.length - 1;
            }

            entity.addedToChunk = true;
            entity.chunkX = this.xPosition;
            entity.chunkY = entityY;
            entity.chunkZ = this.zPosition;
            this.entities[entityY].add(entity);
            this.world.addEntityToHashmap(entity);
        } else {
            logger.warning("Wrong location! " + entity);
            entity.setDead();
        }
    }

    public void removeEntity(Entity entity) {
        if (entity.wasLit) {
            this.world.restoreBlockEmittance(entity.prevLitX, entity.prevLitY, entity.prevLitZ);
        }

        this.removeEntity(entity, entity.chunkY);
    }

    public void removeEntity(Entity entity, int index) {
        if (index < 0) {
            index = 0;
        }

        if (index >= this.entities.length) {
            index = this.entities.length - 1;
        }

        this.entities[index].remove(entity);
    }

    public boolean isBlockExposed(int x, int y, int z) {
        return y >= this.heightMap[z << 4 | x];
    }

    public TileEntity getTileEntity(int xCoord, int yCoord, int zCoord) {
        int packedCoords = xCoord * 8976890 + yCoord * 981131 + zCoord;
        TileEntity tileEntity = this.tileEntities.get(packedCoords);
        if (tileEntity == null) {
            int id = this.getBlockID(xCoord, yCoord, zCoord);
            if (!Block.isContainer.get(id)) {
                return null;
            }

            ContainerBlock container = (ContainerBlock)BlockList.blocks[id];
            container.onBlockAdded(this.world, this.xPosition * 16 + xCoord, yCoord, this.zPosition * 16 + zCoord);
            tileEntity = this.tileEntities.get(packedCoords);
        }

        return tileEntity;
    }

    public void addTileEntity(TileEntity tileEntity) {
        int x = tileEntity.x - this.xPosition * 16;
        int y = tileEntity.y;
        int z = tileEntity.z - this.zPosition * 16;
        this.setTileEntity(x, y, z, tileEntity);
    }

    public void setTileEntity(int xCoord, int yCoord, int zCoord, TileEntity tileEntity) {
        int packedCoords = xCoord * 8976890 + yCoord * 981131 + zCoord;
        tileEntity.world = this.world;
        tileEntity.x = this.xPosition * 16 + xCoord;
        tileEntity.y = yCoord;
        tileEntity.z = this.zPosition * 16 + zCoord;
        if (this.getBlockID(xCoord, yCoord, zCoord) != 0 && BlockList.blocks[this.getBlockID(xCoord & 15, yCoord, zCoord & 15)] instanceof ContainerBlock) {
            tileEntity.validate();
            this.tileEntities.put(packedCoords, tileEntity);
        } else {
            logger.warning("Attempted to place a tile entity where there was no entity tile!");
        }
    }

    public void removeTileEntity(int x, int y, int z) {
        int packedCoords = x * 8976890 + y * 981131 + z;
        if (this.isLoaded) {
            TileEntity tileEntity = this.tileEntities.remove(packedCoords);
            if (tileEntity != null) {
                tileEntity.invalidate();
            }
        }
    }

    public void onChunkLoad() {
        this.isLoaded = true;
        this.world.tileEntities.addAll(this.tileEntities.values());

        for (List<Entity> entity : this.entities) {
            this.world.addLoadedEntities(entity);
        }
    }

    public void onChunkUnload() {
        this.isLoaded = false;
        this.world.tileEntities.removeAll(this.tileEntities.values());

        for (List<Entity> entity : this.entities) {
            this.world.unloadEntities(entity);
        }
    }

    public void setModified() {
        this.isModified = true;
    }

    public void getEntitiesInBB(Entity entity, BoundingBox aabb, List<Entity> entities) {
        int minY = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
        int maxY = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
        if (minY < 0) {
            minY = 0;
        }

        if (maxY >= this.entities.length) {
            maxY = this.entities.length - 1;
        }

        for (int y = minY; y <= maxY; y++) {
            for (Entity entityToAdd : this.entities[y]) {
                if (entityToAdd != entity && entityToAdd.boundingBox.intersects(aabb)) {
                    entities.add(entityToAdd);
                }
            }
        }
    }

    public void getSpecificEntitiesInBB(Class<?> targetClass, BoundingBox aabb, ArrayList<Entity> entities) {
        int minY = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
        int maxY = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
        if (minY < 0) {
            minY = 0;
        }

        if (maxY >= this.entities.length) {
            maxY = this.entities.length - 1;
        }

        for (int y = minY; y <= maxY; y++) {
            for (Entity entityToAdd : this.entities[y]) {
                if (targetClass.isAssignableFrom(entityToAdd.getClass()) && entityToAdd.boundingBox.intersects(aabb)) {
                    entities.add(entityToAdd);
                }
            }
        }
    }

    public boolean save(boolean forced) {
        if (forced) {
            if (this.hasEntities && this.world.getWorldTime() != this.lastSaveTime) {
                return true;
            }
        } else if (this.hasEntities && this.world.getWorldTime() >= this.lastSaveTime + 600L) {
            return true;
        }

        return this.isModified;
    }

    public void populateChunk(ChunkLoader provider, ChunkLoader loader, int x, int z) {
        if (!this.isPopulated && provider.chunkExists(x + 1, z + 1) && provider.chunkExists(x, z + 1) && provider.chunkExists(x + 1, z)) {
            provider.populate(loader, x, z);
        }

        if (provider.chunkExists(x - 1, z)
            && !provider.generateChunk(x - 1, z).isPopulated
            && provider.chunkExists(x - 1, z + 1)
            && provider.chunkExists(x, z + 1)
            && provider.chunkExists(x - 1, z + 1)) {
            provider.populate(loader, x - 1, z);
        }

        if (provider.chunkExists(x, z - 1)
            && !provider.generateChunk(x, z - 1).isPopulated
            && provider.chunkExists(x + 1, z - 1)
            && provider.chunkExists(x + 1, z - 1)
            && provider.chunkExists(x + 1, z)) {
            provider.populate(loader, x, z - 1);
        }

        if (provider.chunkExists(x - 1, z - 1)
            && !provider.generateChunk(x - 1, z - 1).isPopulated
            && provider.chunkExists(x, z - 1)
            && provider.chunkExists(x - 1, z)) {
            provider.populate(loader, x - 1, z - 1);
        }
    }

    public boolean layerEmpty(int index, int y) {
        if (index < 0) {
            index = 0;
        }

        if (y >= this.world.getWorldHeight()) {
            y = this.world.getWorldHeight() - 1;
        }

        for (int i = index; i <= y; i += 16) {
            ChunkSection section = this.sections[i >> 4];
            if (section != null && !section.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void setSections(ChunkSection[] sections) {
        this.sections = sections;
    }

    public void setChunkData(byte[] block, int x, int z, boolean clear) {
        int dataSet = 0;

        for (int section = 0; section < this.sections.length; section++) {
            if ((x & 1 << section) != 0) {
                if (this.sections[section] == null) {
                    this.sections[section] = new ChunkSection(section << 4);
                }

                byte[] abyte0 = this.sections[section].getLowerBlockArray();
                int toCopy = Math.min(abyte0.length, block.length - dataSet);
                if (toCopy <= 0) {
                    break;
                }
                System.arraycopy(block, dataSet, abyte0, 0, toCopy);
                dataSet += toCopy;
            } else if (clear && this.sections[section] != null) {
                this.sections[section] = null;
            }
        }

        for (int var10 = 0; var10 < this.sections.length; var10++) {
            if ((x & 1 << var10) != 0 && this.sections[var10] != null) {
                byte[] metadataArray = this.sections[var10].getBlockMetadataArray();
                int toCopy = Math.min(metadataArray.length, block.length - dataSet);
                if (toCopy <= 0) {
                    break;
                }
                System.arraycopy(block, dataSet, metadataArray, 0, toCopy);
                dataSet += toCopy;
            }
        }

        for (int var11 = 0; var11 < this.sections.length; var11++) {
            if ((x & 1 << var11) != 0 && this.sections[var11] != null) {
                NibbleArray nibblearray = this.sections[var11].getBlocklightArray();
                int toCopy = Math.min(nibblearray.data.length, block.length - dataSet);
                if (toCopy <= 0) {
                    break;
                }
                System.arraycopy(block, dataSet, nibblearray.data, 0, toCopy);
                dataSet += toCopy;
            }
        }

        for (int var12 = 0; var12 < this.sections.length; var12++) {
            if ((x & 1 << var12) != 0 && this.sections[var12] != null) {
                NibbleArray nibblearray2 = this.sections[var12].getSkylightArray();
                int toCopy = Math.min(nibblearray2.data.length, block.length - dataSet);
                if (toCopy <= 0) {
                    break;
                }
                System.arraycopy(block, dataSet, nibblearray2.data, 0, toCopy);
                dataSet += toCopy;
            }
        }

        for (int var13 = 0; var13 < this.sections.length; var13++) {
            if ((z & 1 << var13) != 0) {
                if (this.sections[var13] == null) {
                    dataSet += 2048;
                } else {
                    NibbleArray nibblearray = this.sections[var13].getUpperBlockArray();
                    if (nibblearray == null) {
                        nibblearray = this.sections[var13].createUpperBlockArray();
                    }

                    int toCopy = Math.min(nibblearray.data.length, block.length - dataSet);
                    if (toCopy <= 0) {
                        break;
                    }
                    System.arraycopy(block, dataSet, nibblearray.data, 0, toCopy);
                    dataSet += toCopy;
                }
            } else if (clear && this.sections[var13] != null && this.sections[var13].getUpperBlockArray() != null) {
                this.sections[var13].clearUpperBlockArray();
            }
        }

        if (clear) {
            int toCopy = Math.min(this.biomes.length, block.length - dataSet);
            if (toCopy > 0) {
                System.arraycopy(block, dataSet, this.biomes, 0, toCopy);
            }
        }

        for (int var14 = 0; var14 < this.sections.length; var14++) {
            if (this.sections[var14] != null && (x & 1 << var14) != 0) {
                this.sections[var14].calculateBlocks();
            }
        }

        this.generateHeightMap();

        for (TileEntity tileEntity : this.tileEntities.values()) {
            tileEntity.tick();
        }
    }

    public boolean isLoaded() {
        return false;
    }

    public void removeInvalidBlocks() {
        ChunkSection[] sections = this.sections;

        for (ChunkSection section : sections) {
            if (section != null) {
                section.removeInvalidBlocks();
            }
        }
    }

    public byte[] getBiomes() {
        return this.biomes;
    }

    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }

    public void landSurfaceHeightMap(int[] heightMap) {
        this.landSurfaceHeightMap = (int[])heightMap.clone();
    }

    public Biome getBiomeAt(int x, int z, BiomeProvider provider) {
        int biomeID = this.biomes[z << 4 | x] & 255;
        if (biomeID == 255) {
            Biome biome = provider.getBiomeAt(this.xPosition << 4 | x, this.zPosition << 4 | z);
            biomeID = biome.id;
            this.biomes[z << 4 | x] = (byte)(biomeID & 0xFF);
        }

        return BiomeList.biomes[biomeID] == null ? BiomeList.biomeDefault : BiomeList.biomes[biomeID];
    }

    public boolean setBlockColumn(int x, int y, int z, int[] id) {
        for (int i = 0; i < id.length; i++) {
            int b = id[i];
            if (b >= 0) {
                this.setBlockAndState(x, y++, z, b & 4095, b >> 12);
            } else if (b < -1) {
                int count = -b;
                b = id[++i];
                if (b == -1) {
                    y += count;
                } else {
                    int meta = b >> 12;
                    int blockID = b & 4095;

                    while (count-- > 0) {
                        this.setBlockAndState(x, y++, z, blockID, meta);
                    }
                }
            } else {
                y++;
            }
        }

        return true;
    }

    public int isSpaceFree(int x, int z) {
        int height = x | z << 4;
        int heightValue = this.precipitationMap[height];
        if (heightValue == -999) {
            int maxHeight = this.world.getWorldHeight() - 1;
            heightValue = -1;

            while (maxHeight > 0 && heightValue == -1) {
                int blockID = this.getBlockID(x, maxHeight, z);
                Material mat = blockID == 0 ? MaterialList.air : BlockList.blocks[blockID].material;
                if (!mat.isSolid() && !mat.getIsLiquid()) {
                    maxHeight--;
                } else {
                    heightValue = maxHeight + 1;
                }
            }

            this.precipitationMap[height] = heightValue;
        }

        return heightValue;
    }

    public void initLighting() {
        this.world.blockLight.initBlockLight(this.xPosition, this.zPosition);
        if (!(this.world.currDim instanceof HellDimension)) {
            this.world.skyLight.initSkylight(this.xPosition, this.zPosition);
        }
    }

    public void updateLight(int localX, int worldY, int localZ) {
        int worldX = localX | this.xPosition << 4;
        int worldZ = localZ | this.zPosition << 4;
        this.world.blockLight.checkBlockEmittance(worldX, worldY, worldZ);
        if (!(this.world.currDim instanceof HellDimension)) {
            this.world.skyLight.checkSkyEmittance(worldX, worldY, worldZ);
        }
    }
}
