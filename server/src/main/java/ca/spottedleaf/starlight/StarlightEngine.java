package ca.spottedleaf.starlight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkSection;
import net.minecraft.game.world.chunk.EmptyChunk;
import net.minecraft.game.world.chunk.NibbleArray;
import net.minecraft.game.world.dimension.HellDimension;

public class StarlightEngine {
    protected static final int AIR_BLOCK_STATE = 0;
    protected static final int ALL_DIRECTIONS_BITSET = 63;
    protected static final StarlightEngine.AxisDirection[] DIRECTIONS = StarlightEngine.AxisDirection.values();
    protected static final StarlightEngine.AxisDirection[] AXIS_DIRECTIONS = DIRECTIONS;
    protected static final StarlightEngine.AxisDirection[][] OLD_CHECK_DIRECTIONS = new StarlightEngine.AxisDirection[64][];
    protected static final StarlightEngine.AxisDirection[] ONLY_HORIZONTAL_DIRECTIONS = new StarlightEngine.AxisDirection[]{
        StarlightEngine.AxisDirection.POSITIVE_X,
        StarlightEngine.AxisDirection.NEGATIVE_X,
        StarlightEngine.AxisDirection.POSITIVE_Z,
        StarlightEngine.AxisDirection.NEGATIVE_Z
    };
    protected static final long FLAG_WRITE_LEVEL = 2305843009213693952L;
    protected static final long FLAG_RECHECK_LEVEL = 4611686018427387904L;
    protected static final long FLAG_HAS_SIDED_TRANSPARENT_BLOCKS = Long.MIN_VALUE;
    protected long[] increaseQueue = new long[4096];
    protected int increaseQueueInitialLength;
    protected long[] decreaseQueue = new long[4096];
    protected int decreaseQueueInitialLength;
    protected final Chunk[] chunkCache = new Chunk[25];
    protected final World world;
    protected int encodeOffsetX;
    protected int encodeOffsetY;
    protected int encodeOffsetZ;
    protected int coordinateOffset;
    protected int chunkOffsetX;
    protected int chunkOffsetY;
    protected int chunkOffsetZ;
    protected int chunkIndexOffset;
    protected int chunkSectionIndexOffset;
    protected final boolean skylightPropagator;
    protected final int emittedLightMask;
    protected final boolean isClientSide;

    protected final long[] resizeIncreaseQueue() {
        return this.increaseQueue = Arrays.copyOf(this.increaseQueue, this.increaseQueue.length * 2);
    }

    protected final long[] resizeDecreaseQueue() {
        return this.decreaseQueue = Arrays.copyOf(this.decreaseQueue, this.decreaseQueue.length * 2);
    }

    public StarlightEngine(boolean skylightPropagator, World world) {
        this.skylightPropagator = skylightPropagator;
        this.emittedLightMask = skylightPropagator ? 0 : 15;
        this.isClientSide = false;
        this.world = world;
    }

    protected final void setupEncodeOffset(int centerX, int centerY, int centerZ) {
        this.encodeOffsetX = 31 - centerX;
        this.encodeOffsetY = 32;
        this.encodeOffsetZ = 31 - centerZ;
        this.coordinateOffset = this.encodeOffsetX + (this.encodeOffsetZ << 6) + (this.encodeOffsetY << 12);
        this.chunkOffsetX = 2 - (centerX >> 4);
        this.chunkOffsetY = 2;
        this.chunkOffsetZ = 2 - (centerZ >> 4);
        this.chunkIndexOffset = this.chunkOffsetX + 5 * this.chunkOffsetZ;
        this.chunkSectionIndexOffset = this.chunkIndexOffset + 25 * this.chunkOffsetY;
    }

    protected final void setupCaches(World chunkProvider, int centerX, int centerY, int centerZ, boolean relaxed) {
        int centerChunkX = centerX >> 4;
        int centerChunkY = centerY >> 4;
        int centerChunkZ = centerZ >> 4;
        this.setupEncodeOffset(centerChunkX * 16 + 7, centerChunkY * 16 + 7, centerChunkZ * 16 + 7);
        int radius = 1;

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                int cx = centerChunkX + dx;
                int cz = centerChunkZ + dz;
                Chunk chunk = !chunkProvider.chunkExists(cx, cz) ? null : chunkProvider.getChunk(cx, cz);
                if (chunk == null) {
                    if (!relaxed) {
                        throw new IllegalArgumentException("Trying to propagate light update before 1 radius neighbours ready");
                    }
                } else {
                    this.setChunkInCache(cx, cz, chunk);
                }
            }
        }
    }

    protected final Chunk getChunkInCache(int chunkX, int chunkZ) {
        try {
            return this.chunkCache[chunkX + 5 * chunkZ + this.chunkIndexOffset];
        } catch (Exception ex) {
            return new EmptyChunk(this.world, chunkZ, chunkZ);
        }
    }

    protected final void setChunkInCache(int chunkX, int chunkZ, Chunk chunk) {
        this.chunkCache[chunkX + 5 * chunkZ + this.chunkIndexOffset] = chunk;
    }

    protected final NibbleArray getNibbleFromCache(int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = this.getChunkInCache(chunkX, chunkZ);
        if (chunk != null && chunkY >= 0 && chunkY < chunk.getSections().length) {
            ChunkSection section = chunk.getSections()[chunkY];
            if (section == null) {
                return null;
            } else {
                return this.skylightPropagator ? section.getSkylightArray() : section.getBlocklightArray();
            }
        } else {
            return null;
        }
    }

    protected final int getBlockState(int worldX, int worldY, int worldZ) {
        Chunk chunk = this.getChunkInCache(worldX >> 4, worldZ >> 4);
        return chunk != null && worldY >= 0 && worldY <= this.world.getWorldHeight() - 1 ? chunk.getBlockID(worldX & 15, worldY, worldZ & 15) : 0;
    }

    protected final int getLightLevel(int worldX, int worldY, int worldZ) {
        NibbleArray nibble = this.getNibbleFromCache(worldX >> 4, worldY >> 4, worldZ >> 4);
        if (nibble != null) {
            return nibble.get(worldX & 15, worldY & 15, worldZ & 15);
        } else if (this.skylightPropagator) {
            return worldY > this.world.getWorldHeight() - 1 ? 15 : (nibble == null ? 15 : Math.max(0, nibble.get(worldX & 15, 0, worldZ & 15) - worldY));
        } else {
            return 0;
        }
    }

    protected final void destroyCaches() {
        Arrays.fill(this.chunkCache, null);
    }

    protected final void setLightLevel(int worldX, int worldY, int worldZ, int level) {
        NibbleArray nibble = this.getNibbleFromCache(worldX >> 4, worldY >> 4, worldZ >> 4);
        if (nibble != null) {
            nibble.set(worldX & 15, worldY & 15, worldZ & 15, level);
        }
    }

    protected final void postLightUpdate(int worldX, int worldY, int worldZ) {
        this.world.markForUpdate(worldX, worldY, worldZ);
    }

    protected final void appendToIncreaseQueue(long value) {
        int idx = this.increaseQueueInitialLength++;
        long[] queue = this.increaseQueue;
        if (idx >= queue.length) {
            queue = this.resizeIncreaseQueue();
            queue[idx] = value;
        } else {
            queue[idx] = value;
        }
    }

    protected final void appendToDecreaseQueue(long value) {
        int idx = this.decreaseQueueInitialLength++;
        long[] queue = this.decreaseQueue;
        if (idx >= queue.length) {
            queue = this.resizeDecreaseQueue();
            queue[idx] = value;
        } else {
            queue[idx] = value;
        }
    }

    protected final void performLightIncrease(World world) {
        long[] queue = this.increaseQueue;
        int queueReadIndex = 0;
        int queueLength = this.increaseQueueInitialLength;
        this.increaseQueueInitialLength = 0;
        int decodeOffsetX = -this.encodeOffsetX;
        int decodeOffsetY = -this.encodeOffsetY;
        int decodeOffsetZ = -this.encodeOffsetZ;
        int encodeOffset = this.coordinateOffset;

        while (true) {
            int posX;
            int posZ;
            int posY;
            int propagatedLightLevel;
            StarlightEngine.AxisDirection[] checkDirections;
            while (true) {
                if (queueReadIndex >= queueLength) {
                    return;
                }

                long queueValue = queue[queueReadIndex++];
                posX = ((int)queueValue & 63) + decodeOffsetX;
                posZ = ((int)queueValue >>> 6 & 63) + decodeOffsetZ;
                posY = ((int)queueValue >>> 12 & 65535) + decodeOffsetY;
                propagatedLightLevel = (int)(queueValue >>> 28 & 15L);
                checkDirections = OLD_CHECK_DIRECTIONS[(int)(queueValue >>> 32 & 63L)];
                if ((queueValue & 4611686018427387904L) != 0L) {
                    if (this.getLightLevel(posX, posY, posZ) != propagatedLightLevel) {
                        continue;
                    }
                    break;
                }

                if ((queueValue & 2305843009213693952L) != 0L) {
                    this.setLightLevel(posX, posY, posZ, propagatedLightLevel);
                }
                break;
            }

            for (StarlightEngine.AxisDirection propagate : checkDirections) {
                int offX = posX + propagate.x;
                int offY = posY + propagate.y;
                int offZ = posZ + propagate.z;
                if (offY >= 0 && offY <= this.world.getWorldHeight() - 1) {
                    NibbleArray currentNibble = this.getNibbleFromCache(offX >> 4, offY >> 4, offZ >> 4);
                    int currentLevel;
                    if (currentNibble != null && (currentLevel = currentNibble.get(offX & 15, offY & 15, offZ & 15)) < propagatedLightLevel - 1) {
                        int blockState = this.getBlockState(offX, offY, offZ);
                        int opacityCached = Block.lightOpacity[blockState];
                        int targetLevel = propagatedLightLevel - Math.max(1, opacityCached);
                        if (targetLevel > currentLevel) {
                            currentNibble.set(offX & 15, offY & 15, offZ & 15, targetLevel);
                            this.postLightUpdate(offX, offY, offZ);
                            if (targetLevel > 1) {
                                if (queueLength >= queue.length) {
                                    queue = this.resizeIncreaseQueue();
                                }

                                queue[queueLength++] = offX + (offZ << 6) + (offY << 12) + encodeOffset & 268435455L
                                    | (targetLevel & 15L) << 28
                                    | propagate.everythingButTheOppositeDirection << 32;
                            }
                        }
                    }
                }
            }
        }
    }

    protected final void performLightDecrease(World world) {
        long[] queue = this.decreaseQueue;
        long[] increaseQueue = this.increaseQueue;
        int queueReadIndex = 0;
        int queueLength = this.decreaseQueueInitialLength;
        this.decreaseQueueInitialLength = 0;
        int increaseQueueLength = this.increaseQueueInitialLength;
        int decodeOffsetX = -this.encodeOffsetX;
        int decodeOffsetY = -this.encodeOffsetY;
        int decodeOffsetZ = -this.encodeOffsetZ;
        int encodeOffset = this.coordinateOffset;
        int emittedMask = this.emittedLightMask;

        while (queueReadIndex < queueLength) {
            long queueValue = queue[queueReadIndex++];
            int posX = ((int)queueValue & 63) + decodeOffsetX;
            int posZ = ((int)queueValue >>> 6 & 63) + decodeOffsetZ;
            int posY = ((int)queueValue >>> 12 & 65535) + decodeOffsetY;
            int propagatedLightLevel = (int)(queueValue >>> 28 & 15L);
            StarlightEngine.AxisDirection[] checkDirections = OLD_CHECK_DIRECTIONS[(int)(queueValue >>> 32 & 63L)];

            for (StarlightEngine.AxisDirection propagate : checkDirections) {
                int offX = posX + propagate.x;
                int offY = posY + propagate.y;
                int offZ = posZ + propagate.z;
                if (offY >= 0 && offY <= this.world.getWorldHeight() - 1) {
                    NibbleArray currentNibble = this.getNibbleFromCache(offX >> 4, offY >> 4, offZ >> 4);
                    int lightLevel;
                    if (currentNibble != null && (lightLevel = currentNibble.get(offX & 15, offY & 15, offZ & 15)) != 0) {
                        int blockState = this.getBlockState(offX, offY, offZ);
                        int opacityCached = Block.lightOpacity[blockState];
                        int targetLevel = Math.max(0, propagatedLightLevel - Math.max(1, opacityCached));
                        if (lightLevel > targetLevel) {
                            if (increaseQueueLength >= increaseQueue.length) {
                                increaseQueue = this.resizeIncreaseQueue();
                            }

                            increaseQueue[increaseQueueLength++] = offX + (offZ << 6) + (offY << 12) + encodeOffset & 268435455L
                                | (lightLevel & 15L) << 28
                                | 270582939648L
                                | 4611686018427387904L;
                        } else {
                            int emittedLight = Block.lightValue[blockState] & emittedMask;
                            if (emittedLight != 0) {
                                if (increaseQueueLength >= increaseQueue.length) {
                                    increaseQueue = this.resizeIncreaseQueue();
                                }

                                increaseQueue[increaseQueueLength++] = offX + (offZ << 6) + (offY << 12) + encodeOffset & 268435455L
                                    | (emittedLight & 15L) << 28
                                    | 270582939648L
                                    | 2305843009213693952L;
                            }

                            currentNibble.set(offX & 15, offY & 15, offZ & 15, 0);
                            this.postLightUpdate(offX, offY, offZ);
                            if (targetLevel > 0) {
                                if (queueLength >= queue.length) {
                                    queue = this.resizeDecreaseQueue();
                                }

                                queue[queueLength++] = offX + (offZ << 6) + (offY << 12) + encodeOffset & 268435455L
                                    | (targetLevel & 15L) << 28
                                    | propagate.everythingButTheOppositeDirection << 32;
                            }
                        }
                    }
                }
            }
        }

        this.increaseQueueInitialLength = increaseQueueLength;
        this.performLightIncrease(world);
    }

    public final void checkBlockEmittanceFixed(int worldX, int worldY, int worldZ, int emittedLevel) {
        this.setupCaches(this.world, worldX, worldY, worldZ, true);

        try {
            int encodeOffset = this.coordinateOffset;
            int emittedMask = this.emittedLightMask;
            int currentLevel = this.getLightLevel(worldX, worldY, worldZ);
            emittedLevel &= emittedMask;
            int chunkX = worldX >> 4;
            int chunkY = worldY >> 4;
            int chunkZ = worldZ >> 4;
            ChunkSection section = null;
            Chunk chunk = this.getChunkInCache(chunkX, chunkZ);
            if (chunk != null && chunkY >= 0 && chunkY < this.world.getWorldHeight() >> 4) {
                if (chunkY >= chunk.getSections().length) {
                    section = chunk.addSection(chunkY);
                } else {
                    section = chunk.getSections()[chunkY];
                }

                NibbleArray nibble = this.skylightPropagator ? section.getSkylightArray() : section.getBlocklightArray();
                nibble.set(worldX & 15, worldY & 15, worldZ & 15, emittedLevel);
            }

            if (emittedLevel != 0) {
                this.appendToIncreaseQueue(worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (emittedLevel & 15L) << 28 | 270582939648L);
            }

            this.appendToDecreaseQueue(worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (currentLevel & 15L) << 28 | 270582939648L);
            this.performLightDecrease(this.world);
        } catch (Exception var17) {
        } finally {
            this.destroyCaches();
        }
    }

    public final void checkBlockEmittance(int worldX, int worldY, int worldZ) {
        this.setupCaches(this.world, worldX, worldY, worldZ, true);

        try {
            int encodeOffset = this.coordinateOffset;
            int emittedMask = this.emittedLightMask;
            int currentLevel = this.getLightLevel(worldX, worldY, worldZ);
            int blockState = this.getBlockState(worldX, worldY, worldZ);
            int emittedLevel = Block.lightValue[blockState] & emittedMask;
            this.setLightLevel(worldX, worldY, worldZ, emittedLevel);
            if (emittedLevel != 0) {
                this.appendToIncreaseQueue(worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (emittedLevel & 15L) << 28 | 270582939648L);
            }

            this.appendToDecreaseQueue(worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (currentLevel & 15L) << 28 | 270582939648L);
            this.performLightDecrease(this.world);
        } catch (Exception var12) {
        } finally {
            this.destroyCaches();
        }
    }

    public final void checkSkyEmittance(int worldX, int worldY, int worldZ) {
        this.setupCaches(this.world, worldX, worldY, worldZ, true);

        try {
            int maxPropagationY = this.tryPropagateSkylight(this.world, worldX, worldY, worldZ);
            long propagateDirection = StarlightEngine.AxisDirection.POSITIVE_Y.everythingButThisDirection;
            int encodeOffset = this.coordinateOffset;
            if (this.getLightLevel(worldX, maxPropagationY, worldZ) == 15) {
                int minY = 0;

                for (int currY = maxPropagationY; currY >= minY; currY--) {
                    NibbleArray nibble = this.getNibbleFromCache(worldX >> 4, currY >> 4, worldZ >> 4);
                    if (nibble == null || nibble.get(worldX & 15, currY & 15, worldZ & 15) != 15) {
                        break;
                    }

                    this.appendToDecreaseQueue(worldX + (worldZ << 6) + (currY << 12) + encodeOffset & 268435455L | 4026531840L | propagateDirection << 32);
                    nibble.set(worldX & 15, currY & 15, worldZ & 15, 0);
                }
            }

            int currentLevel = this.getLightLevel(worldX, worldY, worldZ);
            if (currentLevel == 15) {
                this.appendToIncreaseQueue(
                    worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (currentLevel & 15L) << 28 | 270582939648L | Long.MIN_VALUE
                );
            } else {
                this.setLightLevel(worldX, worldY, worldZ, 0);
            }

            this.appendToDecreaseQueue(worldX + (worldZ << 6) + (worldY << 12) + encodeOffset & 268435455L | (currentLevel & 15L) << 28 | 270582939648L);
            this.performLightDecrease(this.world);
        } finally {
            this.destroyCaches();
        }
    }

    protected final int tryPropagateSkylight(World world, int worldX, int startY, int worldZ) {
        int encodeOffset = this.coordinateOffset;
        long propagateDirection = StarlightEngine.AxisDirection.POSITIVE_Y.everythingButThisDirection;
        if (this.getLightLevel(worldX, startY + 1, worldZ) != 15) {
            return startY;
        }

        for (int endY = 0; startY >= endY; startY--) {
            int current = this.getBlockState(worldX, startY, worldZ);
            int opacityIfCached = Block.lightOpacity[current];
            if (opacityIfCached != 0) {
                break;
            }

            this.appendToIncreaseQueue(worldX + (worldZ << 6) + (startY << 12) + encodeOffset & 268435455L | 4026531840L | propagateDirection << 32);
            this.setLightLevel(worldX, startY, worldZ, 15);
        }

        return startY;
    }

    public final void initSkylight(int chunkX, int chunkZ) {
        this.setupCaches(this.world, chunkX << 4 | 7, 64, chunkZ << 4 | 7, true);
        int maxY = this.world.getWorldHeight() - 1;

        try {
            int worldChunkX = chunkX << 4;
            int worldChunkZ = chunkZ << 4;

            for (int currZ = 0; currZ <= 15; currZ++) {
                for (int currX = 0; currX <= 15; currX++) {
                    int worldX = currX | worldChunkX;
                    int worldZ = currZ | worldChunkZ;
                    this.tryPropagateSkylight(this.world, worldX, maxY + 1, worldZ);
                }
            }

            this.propagateNeighbourLevels(chunkX, chunkZ);
            this.performLightIncrease(this.world);
        } finally {
            this.destroyCaches();
        }
    }

    public final void initBlockLight(int chunkX, int chunkZ) {
        this.setupCaches(this.world, chunkX << 4 | 7, 64, chunkZ << 4 | 7, true);

        try {
            if (this.world.settings.getType() == WorldType.CAVES || this.world.currDim instanceof HellDimension) {
                Chunk chunk = this.getChunkInCache(chunkX, chunkZ);
                int encodeOffset = this.coordinateOffset;

                for (int y = 0; y <= this.world.getWorldHeight() - 1; y++) {
                    NibbleArray nibble = this.getNibbleFromCache(chunkX, y >> 4, chunkZ);

                    for (int z = 0; z <= 15; z++) {
                        for (int x = 0; x <= 15; x++) {
                            int blockState = chunk.getBlockID(x, y, z);
                            if (blockState != 0) {
                                int emittedLight = Block.lightValue[blockState];
                                if (emittedLight != 0) {
                                    nibble.set(x, y & 15, z, emittedLight);
                                    this.appendToIncreaseQueue(
                                        (x | chunkX << 4) + ((z | chunkZ << 4) << 6) + (y << 12) + encodeOffset & 268435455L
                                            | (emittedLight & 15L) << 28
                                            | 270582939648L
                                            | Long.MIN_VALUE
                                    );
                                }
                            }
                        }
                    }
                }
            }

            this.propagateNeighbourLevels(chunkX, chunkZ);
            this.performLightIncrease(this.world);
        } catch (Exception var14) {
        } finally {
            this.destroyCaches();
        }
    }

    protected final void propagateNeighbourLevels(int chunkX, int chunkZ) {
        int maxLevel = 15;
        int minLevel = 0;

        for (int currSectionY = maxLevel; currSectionY >= minLevel; currSectionY--) {
            for (StarlightEngine.AxisDirection direction : ONLY_HORIZONTAL_DIRECTIONS) {
                int neighbourOffX = direction.x;
                int neighbourOffY = direction.y;
                int neighbourOffZ = direction.z;
                NibbleArray neighbourNibble = this.getNibbleFromCache(chunkX + neighbourOffX, currSectionY + neighbourOffY, chunkZ + neighbourOffZ);
                if (neighbourNibble != null) {
                    int incX;
                    int incZ;
                    int startX;
                    int startZ;
                    if (neighbourOffX != 0) {
                        incX = 0;
                        incZ = 1;
                        if (direction.x < 0) {
                            startX = (chunkX << 4) - 1;
                        } else {
                            startX = (chunkX << 4) + 16;
                        }

                        startZ = chunkZ << 4;
                    } else {
                        incX = 1;
                        incZ = 0;
                        if (neighbourOffZ < 0) {
                            startZ = (chunkZ << 4) - 1;
                        } else {
                            startZ = (chunkZ << 4) + 16;
                        }

                        startX = chunkX << 4;
                    }

                    long propagateDirection = 1L << direction.getOpposite().ordinal();
                    int encodeOffset = this.coordinateOffset;
                    int currY = currSectionY << 4;

                    for (int maxY = currY | 15; currY <= maxY; currY++) {
                        int i = 0;
                        int currX = startX;

                        for (int currZ = startZ; i < 16; currZ += incZ) {
                            int level = neighbourNibble.get(currX & 15, currY & 15, currZ & 15);
                            if (level > 1) {
                                this.appendToIncreaseQueue(
                                    currX + (currZ << 6) + (currY << 12) + encodeOffset & 268435455L
                                        | (level & 15L) << 28
                                        | propagateDirection << 32
                                        | Long.MIN_VALUE
                                );
                            }

                            i++;
                            currX += incX;
                        }
                    }
                }
            }
        }
    }

    static {
        for (int i = 0; i < OLD_CHECK_DIRECTIONS.length; i++) {
            List<StarlightEngine.AxisDirection> directions = new ArrayList<>();
            int bitset = i;
            int len = Integer.bitCount(i);

            for (int index = 0; index < len; bitset ^= -bitset & bitset) {
                directions.add(AXIS_DIRECTIONS[Integer.numberOfTrailingZeros(bitset)]);
                index++;
            }

            OLD_CHECK_DIRECTIONS[i] = directions.toArray(new StarlightEngine.AxisDirection[0]);
        }
    }

    protected enum AxisDirection {
        POSITIVE_X(1, 0, 0),
        NEGATIVE_X(-1, 0, 0),
        POSITIVE_Z(0, 0, 1),
        NEGATIVE_Z(0, 0, -1),
        POSITIVE_Y(0, 1, 0),
        NEGATIVE_Y(0, -1, 0);

        protected StarlightEngine.AxisDirection opposite;
        public final int x;
        public final int y;
        public final int z;
        public final long everythingButThisDirection;
        public final long everythingButTheOppositeDirection;

        AxisDirection(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.everythingButThisDirection = 63 ^ 1 << this.ordinal();
            this.everythingButTheOppositeDirection = 63 ^ 1 << (this.ordinal() ^ 1);
        }

        public StarlightEngine.AxisDirection getOpposite() {
            return this.opposite;
        }

        static {
            POSITIVE_X.opposite = NEGATIVE_X;
            NEGATIVE_X.opposite = POSITIVE_X;
            POSITIVE_Z.opposite = NEGATIVE_Z;
            NEGATIVE_Z.opposite = POSITIVE_Z;
            POSITIVE_Y.opposite = NEGATIVE_Y;
            NEGATIVE_Y.opposite = POSITIVE_Y;
        }
    }
}
