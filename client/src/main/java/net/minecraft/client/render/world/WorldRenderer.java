package net.minecraft.client.render.world;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.minecraft.Profiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEmitter;
import net.minecraft.client.particle.effect.CritParticle;
import net.minecraft.client.particle.effect.GlowParticle;
import net.minecraft.client.particle.effect.HeartParticle;
import net.minecraft.client.particle.effect.MarkerParticle;
import net.minecraft.client.particle.effect.NoteParticle;
import net.minecraft.client.particle.effect.SoulParticle;
import net.minecraft.client.particle.effect.SpellParticle;
import net.minecraft.client.particle.effect.SwipeParticle;
import net.minecraft.client.particle.environment.AshParticle;
import net.minecraft.client.particle.environment.BubbleParticle;
import net.minecraft.client.particle.environment.DropletParticle;
import net.minecraft.client.particle.environment.DustParticle;
import net.minecraft.client.particle.environment.ExplosionParticle;
import net.minecraft.client.particle.environment.FireflyParticle;
import net.minecraft.client.particle.environment.FishParticle;
import net.minecraft.client.particle.environment.FlameParticle;
import net.minecraft.client.particle.environment.FootstepParticle;
import net.minecraft.client.particle.environment.GustParticle;
import net.minecraft.client.particle.environment.LavaBlobParticle;
import net.minecraft.client.particle.environment.LeafParticle;
import net.minecraft.client.particle.environment.RaindropParticle;
import net.minecraft.client.particle.environment.SmokeParticle;
import net.minecraft.client.particle.environment.SplashParticle;
import net.minecraft.client.particle.special.BlockParticle;
import net.minecraft.client.particle.special.BlockParticle3D;
import net.minecraft.client.particle.special.ItemParticle;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.camera.Camera;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.ParticleRenderer;
import net.minecraft.client.render.entity.tileentity.TileEntityRenderManager;
import net.minecraft.client.render.environment.SkyRenderer;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.ParticleContext;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldListener;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class WorldRenderer implements WorldListener {
    private Minecraft mc;
    private World world;
    private TextureLoader texLoader;
    public SkyRenderer skyRenderer;
    public BordersRenderer bordersRenderer;
    public List<TileEntity> tileEntities = new ArrayList<>();
    private List<ChunkRenderer> queuedChunks = new ArrayList<>();
    private List<ChunkRenderer> chunkRenderers = new ArrayList<>();
    private ChunkRenderer[] sortedChunks;
    private ChunkRenderer[] loadedChunks;
    private WorldRenderer.RenderList[] renderLists = new WorldRenderer.RenderList[]{
        new WorldRenderer.RenderList(), new WorldRenderer.RenderList(), new WorldRenderer.RenderList(), new WorldRenderer.RenderList()
    };
    private int chunksWidth;
    private int chunksHeight;
    private int chunksDepth;
    private int chunkDisplayListID;
    private int minBlockX;
    private int minBlockY;
    private int minBlockZ;
    private int maxBlockX;
    private int maxBlockY;
    private int maxBlockZ;
    private double prevRenderSortX = -9999.0;
    private double prevRenderSortY = -9999.0;
    private double prevRenderSortZ = -9999.0;
    private double prevSortX = -9999.0;
    private double prevSortY = -9999.0;
    private double prevSortZ = -9999.0;
    private double prevReposX;
    private double prevReposY;
    private double prevReposZ;
    private int prevChunkSortX = -999;
    private int prevChunkSortY = -999;
    private int prevChunkSortZ = -999;
    private int entityCount;
    private int visibleEntityCount;
    private int chunksCount;
    private int chunksLoaded;
    private int chunksClipped;
    private int chunksVisible;
    private int chunksSkipped;
    private int chunksSorted = 0;
    private int renderDistance = -1;
    private int vertexResortCounter = 30;

    public WorldRenderer(Minecraft minecraft, TextureLoader loader) {
        this.mc = minecraft;
        this.texLoader = loader;
        this.bordersRenderer = new BordersRenderer(minecraft);
        this.chunkDisplayListID = Renderer.generateDisplayLists(786432);
    }

    public void changeWorld(World world) {
        if (this.world != null) {
            this.world.removeListener(this);
        }

        this.prevSortX = -9999.0;
        this.prevSortY = -9999.0;
        this.prevSortZ = -9999.0;
        this.prevChunkSortX = -9999;
        this.prevChunkSortY = -9999;
        this.prevChunkSortZ = -9999;
        this.prevRenderSortX = -9999.0;
        this.prevRenderSortY = -9999.0;
        this.prevRenderSortZ = -9999.0;
        EntityRenderManager.instance.world = world;
        this.world = world;
        if (world != null) {
            world.addListener(this);
            this.reloadChunks();
        }
    }

    public void reloadChunks() {
        BlockList.leaves.setGraphicsLevel(OptionValues.fancyTextures);
        BlockList.leaves.setLightingLevel(OptionValues.smoothLighting);
        this.renderDistance = OptionValues.renderDistance;
        if (this.loadedChunks != null) {
            for (int dist = 0; dist < this.loadedChunks.length; dist++) {
                this.loadedChunks[dist].dispose();
            }
        }

        int dist = 32 * this.renderDistance;
        this.prevReposX = -9999.0;
        this.prevReposY = -9999.0;
        this.prevReposZ = -9999.0;
        this.chunksWidth = dist / 16 + 1;
        this.chunksHeight = this.world.getMaxSections();
        this.chunksDepth = dist / 16 + 1;
        this.loadedChunks = new ChunkRenderer[this.chunksWidth * this.chunksHeight * this.chunksDepth];
        this.sortedChunks = new ChunkRenderer[this.chunksWidth * this.chunksHeight * this.chunksDepth];
        this.chunksSorted = 0;
        int chunksInQueue = 0;
        int loadedChunks = 0;
        this.minBlockX = 0;
        this.minBlockY = 0;
        this.minBlockZ = 0;
        this.maxBlockX = this.chunksWidth;
        this.maxBlockY = this.chunksHeight;
        this.maxBlockZ = this.chunksDepth;

        for (int index = 0; index < this.queuedChunks.size(); index++) {
            if (this.queuedChunks.get(index) != null) {
                this.queuedChunks.get(index).needsRefresh = false;
            }
        }

        this.queuedChunks.clear();
        this.tileEntities.clear();

        for (int x = 0; x < this.chunksWidth; x++) {
            for (int y = 0; y < this.chunksHeight; y++) {
                for (int z = 0; z < this.chunksDepth; z++) {
                    int chunkIndex = (z * this.chunksHeight + y) * this.chunksWidth + x;
                    this.loadedChunks[chunkIndex] = new ChunkRenderer(
                        this.world, this.tileEntities, x * 16, y * 16, z * 16, 16, this.chunkDisplayListID + chunksInQueue
                    );
                    this.loadedChunks[chunkIndex].isOccluded = false;
                    this.loadedChunks[chunkIndex].isVisible = true;
                    this.loadedChunks[chunkIndex].isInFrustum = false;
                    this.loadedChunks[chunkIndex].index = loadedChunks++;
                    this.loadedChunks[chunkIndex].markDirty();
                    this.queuedChunks.add(this.loadedChunks[chunkIndex]);
                    chunksInQueue += 3;
                }
            }
        }

        if (this.world != null) {
            Mob player = this.mc.gameRenderer.cameraMob;
            if (player != null) {
                this.repositionChunks(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
                WorldRenderer.EntitySorter sorter = new WorldRenderer.EntitySorter(player);
                sorter.prepareToSort(this.sortedChunks, this.chunksSorted);
                Arrays.sort(this.sortedChunks, 0, this.chunksSorted, sorter);
            }
        }
    }

    public void renderEntities(Vector playerPosition, Camera camera, float tick) {
        Profiler.startSection("prepare");
        TileEntityRenderManager.instance.cacheInfo(this.texLoader, this.mc.fontRenderer, this.mc.gameRenderer.cameraMob, tick);
        EntityRenderManager.instance.cacheInfo(this.world, this.texLoader, this.mc.fontRenderer, this.mc.gameRenderer.cameraMob, this.mc.options, tick);
        this.entityCount = 0;
        this.visibleEntityCount = 0;
        Mob player = this.mc.gameRenderer.cameraMob;
        EntityRenderManager.renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * tick;
        EntityRenderManager.renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * tick;
        EntityRenderManager.renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * tick;
        TileEntityRenderManager.staticPlayerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * tick;
        TileEntityRenderManager.staticPlayerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * tick;
        TileEntityRenderManager.staticPlayerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * tick;
        this.mc.gameRenderer.lightmap.enable();
        List<Entity> entityList = this.world.getEntityList();
        this.entityCount = entityList.size();
        Profiler.endStartSection("weatherFX");

        for (int index = 0; index < this.world.weatherEffects.size(); index++) {
            Entity entityToRender = this.world.weatherEffects.get(index);
            this.visibleEntityCount++;
            if (entityToRender.inRange(playerPosition)) {
                EntityRenderManager.instance.renderEntity(entityToRender, tick);
            }
        }

        Profiler.endStartSection("entities");

        for (int var23 = 0; var23 < entityList.size(); var23++) {
            Entity entityToRender = entityList.get(var23);
            boolean renderChecks = this.mc.gameRenderer.cameraMob.mount != null && this.mc.gameRenderer.cameraMob.mount == entityToRender
                || entityToRender.alwaysVisible()
                || entityToRender.inRange(playerPosition)
                    && camera.isInFrustum(
                        entityToRender.boundingBox.minX,
                        entityToRender.boundingBox.minY,
                        entityToRender.boundingBox.minZ,
                        entityToRender.boundingBox.maxX,
                        entityToRender.boundingBox.maxY,
                        entityToRender.boundingBox.maxZ
                    );
            if (entityToRender == this.mc.gameRenderer.cameraMob) {
                renderChecks = !OptionValues.showArm && this.mc.gameRenderer.cameraMob instanceof Player
                    || OptionValues.cameraMode >= 1
                    || this.mc.currentScreen instanceof PhotoMode;
            }

            if (renderChecks) {
                this.visibleEntityCount++;
                EntityRenderManager.instance.renderEntity(entityToRender, tick);
                entityToRender.visible = true;
            } else {
                entityToRender.visible = false;
            }
        }

        Profiler.endStartSection("tileentities");

        for (int var24 = 0; var24 < this.tileEntities.size(); var24++) {
            TileEntity te = this.tileEntities.get(var24);
            if (te != null) {
                double minX = te.x;
                double minY = te.y;
                double minZ = te.z;
                double maxX = te.x;
                double maxY = te.y;
                double maxZ = te.z;
                if (te.getBlockType() != null) {
                    Block block = te.getBlockType();
                    minX = te.x + block.minX;
                    minY = te.y + block.minY;
                    minZ = te.z + block.minZ;
                    maxX = te.x + block.maxX;
                    maxY = te.y + block.maxY;
                    maxZ = te.z + block.maxZ;
                    if (te instanceof ChestEntity) {
                        ChestEntity chest = (ChestEntity)te;
                        if (chest.chestBack != null) {
                            minX = te.x + block.minX - 1.0;
                        }

                        if (chest.chestFront != null) {
                            maxX = te.x + block.maxX + 1.0;
                        }

                        if (chest.chestLeft != null) {
                            minZ = te.z + block.minZ - 1.0;
                        }

                        if (chest.chestRight != null) {
                            maxZ = te.z + block.maxZ + 1.0;
                        }
                    }
                }

                if (camera.isInFrustum(minX, minY, minZ, maxX, maxY, maxZ) || this.tileEntities.get(var24).dontCull()) {
                    TileEntityRenderManager.instance.renderTileEntity(this.tileEntities.get(var24), tick);
                }
            }
        }

        this.mc.gameRenderer.lightmap.disable();
        Profiler.endSection();
    }

    public String chunkRenderersInfo() {
        return "C: " + this.chunksVisible + "/" + this.chunksLoaded + ". F: " + this.chunksClipped + ", R: " + this.chunksSkipped;
    }

    public String entityRenderersInfo() {
        return "E: "
            + this.visibleEntityCount
            + "/"
            + this.entityCount
            + ", LT: "
            + this.world.tileEntities.size()
            + ", P: "
            + this.mc.particleRenderer.getTotalCount();
    }

    private void repositionChunks(int x, int y, int z) {
        x -= 8;
        y -= 8;
        z -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int blocksWide = this.chunksWidth * 16;
        int blocksWideHalved = blocksWide / 2;

        for (int ix = 0; ix < this.chunksWidth; ix++) {
            int blockX = ix * 16;
            int blockXAbs = blockX + blocksWideHalved - x;
            if (blockXAbs < 0) {
                blockXAbs -= blocksWide - 1;
            }

            blockXAbs /= blocksWide;
            blockX -= blockXAbs * blocksWide;
            if (blockX < this.minBlockX) {
                this.minBlockX = blockX;
            }

            if (blockX > this.maxBlockX) {
                this.maxBlockX = blockX;
            }

            for (int iz = 0; iz < this.chunksDepth; iz++) {
                int blockZ = iz * 16;
                int blockZAbs = blockZ + blocksWideHalved - z;
                if (blockZAbs < 0) {
                    blockZAbs -= blocksWide - 1;
                }

                blockZAbs /= blocksWide;
                blockZ -= blockZAbs * blocksWide;
                if (blockZ < this.minBlockZ) {
                    this.minBlockZ = blockZ;
                }

                if (blockZ > this.maxBlockZ) {
                    this.maxBlockZ = blockZ;
                }

                for (int iy = 0; iy < this.chunksHeight; iy++) {
                    int blockY = iy * 16;
                    if (blockY < this.minBlockY) {
                        this.minBlockY = blockY;
                    }

                    if (blockY > this.maxBlockY) {
                        this.maxBlockY = blockY;
                    }

                    ChunkRenderer chunkRenderer = this.loadedChunks[(iz * this.chunksHeight + iy) * this.chunksWidth + ix];
                    boolean needsUpdate = chunkRenderer.needsRefresh;
                    chunkRenderer.setPosition(blockX, blockY, blockZ);
                    if (!needsUpdate && chunkRenderer.needsRefresh) {
                        this.queuedChunks.add(chunkRenderer);
                    }
                }
            }
        }
    }

    public int sortChunks(Mob player, int layer, double tick) {
        Profiler.startSection("sortchunks");
        byte amountToSort = 10;
        if (this.queuedChunks.size() < amountToSort) {
            for (int num = 0; num < amountToSort; num++) {
                this.chunksCount = (this.chunksCount + 1) % this.loadedChunks.length;
                ChunkRenderer chunk = this.loadedChunks[this.chunksCount];
                if (chunk.needsRefresh && !this.queuedChunks.contains(chunk)) {
                    this.queuedChunks.add(chunk);
                }
            }
        }

        if (OptionValues.renderDistance != this.renderDistance) {
            this.reloadChunks();
        }

        if (layer == 0) {
            this.chunksLoaded = 0;
            this.chunksClipped = 0;
            this.chunksVisible = 0;
            this.chunksSkipped = 0;
        }

        boolean hasPlayerMoved = this.prevChunkSortX != player.chunkX || this.prevChunkSortY != player.chunkY || this.prevChunkSortZ != player.chunkZ;
        double partialX = 0.0;
        double partialY = 0.0;
        double partialZ = 0.0;
        if (!hasPlayerMoved) {
            partialX = player.posX - this.prevSortX;
            partialY = player.posY - this.prevSortY;
            partialZ = player.posZ - this.prevSortZ;
            double moveLength = partialX * partialX + partialY * partialY + partialZ * partialZ;
            hasPlayerMoved = moveLength > 16.0;
        }

        if (hasPlayerMoved) {
            this.prevChunkSortX = player.chunkX;
            this.prevChunkSortY = player.chunkY;
            this.prevChunkSortZ = player.chunkZ;
            this.prevSortX = player.posX;
            this.prevSortY = player.posY;
            this.prevSortZ = player.posZ;
            double dReposX = player.posX - this.prevReposX;
            double dReposY = player.posY - this.prevReposY;
            double dReposZ = player.posZ - this.prevReposZ;
            double countResort = dReposX * dReposX + dReposY * dReposY + dReposZ * dReposZ;
            if (countResort > 16.0) {
                this.prevReposX = player.posX;
                this.prevReposY = player.posY;
                this.prevReposZ = player.posZ;
                this.repositionChunks(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
            }

            WorldRenderer.EntitySorter lastIndex = new WorldRenderer.EntitySorter(player);
            lastIndex.prepareToSort(this.sortedChunks, this.chunksSorted);
            Arrays.sort(this.sortedChunks, 0, this.chunksSorted, lastIndex);
        }

        partialX = player.posX - this.prevRenderSortX;
        partialY = player.posY - this.prevRenderSortY;
        partialZ = player.posZ - this.prevRenderSortZ;
        int sortedChunksCount = this.chunksSorted;
        if (partialX * partialX + partialY * partialY + partialZ * partialZ > 1.0) {
            this.prevRenderSortX = player.posX;
            this.prevRenderSortY = player.posY;
            this.prevRenderSortZ = player.posZ;

            for (int chunk = 0; chunk < sortedChunksCount; chunk++) {
                ChunkRenderer firstIndex = this.sortedChunks[chunk];
                if (firstIndex.vertexState != null) {
                    firstIndex.needResort = true;
                    if (this.vertexResortCounter > chunk) {
                        this.vertexResortCounter = chunk;
                    }
                }
            }
        }

        while (this.vertexResortCounter < sortedChunksCount) {
            ChunkRenderer firstIndex = this.sortedChunks[this.vertexResortCounter];
            this.vertexResortCounter++;
            if (firstIndex.needResort) {
                firstIndex.sortChunk(player);
                break;
            }
        }

        Profiler.endStartSection("render");
        int rendererCount = this.renderSortedChunks(this.chunksSorted, layer, tick);
        Profiler.endSection();
        return rendererCount;
    }

    public int renderAllSortedChunks(int renderPass, double partialTicks) {
        return this.renderSortedChunks(this.chunksSorted, renderPass, partialTicks);
    }

    private int renderSortedChunks(int chunksSorted, int renderPass, double partialTicks) {
        this.chunkRenderers.clear();
        int activerRenderers = 0;
        int sortedRenderers = 0;
        int totalSorted = chunksSorted;
        byte renderOffset = 1;
        if (renderPass == 1) {
            sortedRenderers = this.chunksSorted - 1;
            totalSorted = this.chunksSorted - 1 - chunksSorted;
            renderOffset = -1;
        }

        for (int chunkIndex = sortedRenderers; chunkIndex != totalSorted; chunkIndex += renderOffset) {
            if (renderPass == 0) {
                this.chunksLoaded++;
                if (this.sortedChunks[chunkIndex].skipLayer[renderPass]) {
                    this.chunksSkipped++;
                } else if (!this.sortedChunks[chunkIndex].isInFrustum) {
                    this.chunksClipped++;
                } else {
                    this.chunksVisible++;
                }
            }

            if (!this.sortedChunks[chunkIndex].skipLayer[renderPass] && this.sortedChunks[chunkIndex].isInFrustum && this.sortedChunks[chunkIndex].isVisible) {
                int layer = this.sortedChunks[chunkIndex].getLayer(renderPass);
                if (layer >= 0) {
                    this.chunkRenderers.add(this.sortedChunks[chunkIndex]);
                    activerRenderers++;
                }
            }
        }

        if (activerRenderers == 0) {
            return 0;
        }

        Mob player = this.mc.gameRenderer.cameraMob;
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        int upperLimit = 0;

        for (int chunkIndex = 0; chunkIndex < this.renderLists.length; chunkIndex++) {
            this.renderLists[chunkIndex].reset();
        }

        for (int var24 = 0; var24 < this.chunkRenderers.size(); var24++) {
            ChunkRenderer chunkRenderer = this.chunkRenderers.get(var24);
            int lastRendererIndex = -1;

            for (int index = 0; index < upperLimit; index++) {
                if (this.renderLists[index].isRenderedAt(chunkRenderer.negX, chunkRenderer.negY, chunkRenderer.negZ)) {
                    lastRendererIndex = index;
                }
            }

            if (lastRendererIndex < 0) {
                lastRendererIndex = upperLimit++;
                this.renderLists[lastRendererIndex].setLocation(chunkRenderer.negX, chunkRenderer.negY, chunkRenderer.negZ, playerX, playerY, playerZ);
            }

            this.renderLists[lastRendererIndex].render(chunkRenderer.getLayer(renderPass));
        }

        int posX = MathHelper.floor_double(playerX);
        int posZ = MathHelper.floor_double(playerZ);
        int chunkX = posX - (posX & 1023);
        int chunkZ = posZ - (posZ & 1023);
        Arrays.sort(this.renderLists, new WorldRenderer.ChunkSorter(chunkX, chunkZ));
        this.renderAllRenderLists(renderPass, partialTicks);
        return activerRenderers;
    }

    public void renderAllRenderLists(int pass, double tick) {
        this.mc.gameRenderer.lightmap.enable();

        for (int index = 0; index < this.renderLists.length; index++) {
            this.renderLists[index].render();
        }

        this.mc.gameRenderer.lightmap.disable();
    }

    public boolean updateChunks(Mob player, boolean flag) {
        if (Minecraft.isDebugScreenFull()) {
            this.bordersRenderer.render(this.mc.timer.renderPartialTicks, this.mc.systemTime);
        }

        if (this.queuedChunks.isEmpty()) {
            return false;
        }

        int num = 0;
        int maxNum = 1;
        byte frustumMul = 4;
        int numValid = 0;
        ChunkRenderer chunkRendererBest = null;
        float distSqBest = Float.MAX_VALUE;
        int index = -1;

        for (int maxDiffDistSq = 0; maxDiffDistSq < this.queuedChunks.size(); maxDiffDistSq++) {
            ChunkRenderer chunkRenderer = this.queuedChunks.get(maxDiffDistSq);
            if (chunkRenderer != null) {
                numValid++;
                if (!chunkRenderer.needsRefresh) {
                    this.queuedChunks.set(maxDiffDistSq, null);
                } else {
                    float distToPlayer = chunkRenderer.distanceToEntity(player);
                    if (distToPlayer <= 256.0F) {
                        chunkRenderer.refreshChunk();
                        chunkRenderer.needsRefresh = false;
                        this.queuedChunks.set(maxDiffDistSq, null);
                        num++;
                    } else {
                        if (distToPlayer > 256.0F && num >= maxNum) {
                            break;
                        }

                        if (!chunkRenderer.isInFrustum) {
                            distToPlayer *= frustumMul;
                        }

                        if (chunkRendererBest == null) {
                            chunkRendererBest = chunkRenderer;
                            distSqBest = distToPlayer;
                            index = maxDiffDistSq;
                        } else if (distToPlayer < distSqBest) {
                            chunkRendererBest = chunkRenderer;
                            distSqBest = distToPlayer;
                            index = maxDiffDistSq;
                        }
                    }
                }
            }
        }

        if (chunkRendererBest != null) {
            chunkRendererBest.refreshChunk();
            chunkRendererBest.needsRefresh = false;
            this.queuedChunks.set(index, null);
            num++;
            float distDiv = distSqBest / 5.0F;

            for (int chunk = 0; chunk < this.queuedChunks.size() && num < maxNum; chunk++) {
                ChunkRenderer chunkRenderer = this.queuedChunks.get(chunk);
                if (chunkRenderer != null) {
                    float distSq = chunkRenderer.distanceToEntity(player);
                    if (!chunkRenderer.isInFrustum) {
                        distSq *= frustumMul;
                    }

                    float diffDistSq = Math.abs(distSq - distSqBest);
                    if (diffDistSq < distDiv) {
                        chunkRenderer.refreshChunk();
                        chunkRenderer.needsRefresh = false;
                        this.queuedChunks.set(chunk, null);
                        num++;
                    }
                }
            }
        }

        if (numValid == 0) {
            this.queuedChunks.clear();
        }

        return true;
    }

    public void clipRenderers(Camera camera, float tick) {
        double renderDistSq = this.renderDistance * 16 * this.renderDistance * 16;
        if (this.renderDistance < 4 || this.mc.currentScreen != null && this.mc.currentScreen instanceof PhotoMode) {
            renderDistSq *= this.renderDistance * 16;
        }

        for (int chunk = 0; chunk < this.chunksSorted; chunk++) {
            ChunkRenderer chunkRenderer = this.sortedChunks[chunk];
            if (!chunkRenderer.skipAllLayers()) {
                if (chunkRenderer.entityDist > renderDistSq) {
                    chunkRenderer.isInFrustum = false;
                } else {
                    chunkRenderer.updateVisibility(camera);
                }
            }
        }
    }

    public void addToSortedChunks(ChunkRenderer chunk) {
        if (!chunk.isSorted) {
            int pos = this.chunksSorted;
            chunk.updateSortDistance(this.mc.gameRenderer.cameraMob);
            float distSq = chunk.sortDist;
            if (this.chunksSorted > 0) {
                int countGreater = 0;
                int high = this.chunksSorted - 1;
                int mid = (countGreater + high) / 2;

                while (countGreater <= high) {
                    mid = (countGreater + high) / 2;
                    ChunkRenderer wrMid = this.sortedChunks[mid];
                    if (distSq < wrMid.sortDist) {
                        high = mid - 1;
                    } else {
                        countGreater = mid + 1;
                    }
                }

                if (countGreater > mid) {
                    pos = mid + 1;
                } else {
                    pos = mid;
                }
            }

            int countGreater = this.chunksSorted - pos;
            if (countGreater > 0) {
                System.arraycopy(this.sortedChunks, pos, this.sortedChunks, pos + 1, countGreater);
            }

            this.sortedChunks[pos] = chunk;
            chunk.isSorted = true;
            this.chunksSorted++;
        }
    }

    public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int chunkStartX = MathHelper.bucketInt(minX, 16);
        int chunkStartY = MathHelper.bucketInt(minY, 16);
        int chunkStartZ = MathHelper.bucketInt(minZ, 16);
        int chunkEndX = MathHelper.bucketInt(maxX, 16);
        int chunkEndY = MathHelper.bucketInt(maxY, 16);
        int chunkEndZ = MathHelper.bucketInt(maxZ, 16);

        for (int x = chunkStartX; x <= chunkEndX; x++) {
            int chunkX = x % this.chunksWidth;
            if (chunkX < 0) {
                chunkX += this.chunksWidth;
            }

            for (int y = chunkStartY; y <= chunkEndY; y++) {
                int chunkY = y % this.chunksHeight;
                if (chunkY < 0) {
                    chunkY += this.chunksHeight;
                }

                for (int z = chunkStartZ; z <= chunkEndZ; z++) {
                    int chunkZ = z % this.chunksDepth;
                    if (chunkZ < 0) {
                        chunkZ += this.chunksDepth;
                    }

                    int chunk = (chunkZ * this.chunksHeight + chunkY) * this.chunksWidth + chunkX;
                    ChunkRenderer chunkRenderer = this.loadedChunks[chunk];
                    if (chunkRenderer != null && !chunkRenderer.needsRefresh) {
                        this.queuedChunks.add(chunkRenderer);
                        chunkRenderer.markDirty();
                    }
                }
            }
        }
    }

    @Override
    public void changeBlocks(int x, int y, int z) {
        this.markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void markBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.markBlocksForUpdate(minX - 1, minY - 1, minZ - 1, maxX + 1, maxY + 1, maxZ + 1);
    }

    @Override
    public void updateAllRenderers() {
        for (int renderer = 0; renderer < this.loadedChunks.length; renderer++) {
            if (this.loadedChunks[renderer].isLit) {
                if (!this.loadedChunks[renderer].needsRefresh) {
                    this.queuedChunks.add(this.loadedChunks[renderer]);
                }

                this.loadedChunks[renderer].markDirty();
            }
        }
    }

    @Override
    public void playSound(String soundName, double xCoord, double yCoord, double zCoord, float volume, float pitch) {
        float maxVol = 16.0F;
        if (volume > 1.0F) {
            maxVol *= volume;
        }

        double soundX = xCoord - this.mc.gameRenderer.cameraMob.posX;
        double soundY = yCoord - this.mc.gameRenderer.cameraMob.posY;
        double soundZ = zCoord - this.mc.gameRenderer.cameraMob.posZ;
        if (soundX * soundX + soundY * soundY + soundZ * soundZ < maxVol * maxVol) {
            this.mc.sndManager.playSound(soundName, (float)xCoord, (float)yCoord, (float)zCoord, volume, pitch);
        }
    }

    @Override
    public void playMobSound(String soundName, int id, double xCoord, double yCoord, double zCoord, float volume, float pitch) {
        float maxVol = 16.0F;
        if (volume > 1.0F) {
            maxVol *= volume;
        }

        double soundX = xCoord - this.mc.gameRenderer.cameraMob.posX;
        double soundY = yCoord - this.mc.gameRenderer.cameraMob.posY;
        double soundZ = zCoord - this.mc.gameRenderer.cameraMob.posZ;
        if (soundX * soundX + soundY * soundY + soundZ * soundZ < maxVol * maxVol) {
            this.mc.sndManager.playMobSound(soundName, id, (float)xCoord, (float)yCoord, (float)zCoord, volume, pitch);
        }
    }

    @Override
    public void playBlockSound(String soundName, double posX, double posY, double posZ, float volume, float pitch) {
        this.mc.sndManager.playBlockSound(soundName, (float)posX, (float)posY, (float)posZ, volume, pitch);
    }

    @Override
    public void playAmbienceSound(String soundName, float volume, float pitch) {
        this.mc.sndManager.playAmbience(soundName, volume, pitch);
    }

    @Override
    public void obtainEntitySkin(Entity entity) {
        if (entity.skinUrl != null) {
            this.texLoader.obtainData(entity.skinUrl, new TextureLoader.ImageDownloader(), true);
        }

        if (entity.cloakUrl != null) {
            this.texLoader.obtainData(entity.cloakUrl, new TextureLoader.ImageDownloader(), false);
        }
    }

    @Override
    public void releaseEntitySkin(Entity entity) {
        if (entity.skinUrl != null) {
            this.texLoader.releaseData(entity.skinUrl);
        }

        if (entity.cloakUrl != null) {
            this.texLoader.releaseData(entity.cloakUrl);
        }
    }

    @Override
    public void spawnParticle(
        ParticleType particle, double xCoordBlock, double yCoordBlock, double zCoordBlock, double xPosition, double yPosition, double zPosition
    ) {
        if (this.mc.gameRenderer.cameraMob != null && OptionValues.particleCount < 1) {
            double xOff = this.mc.gameRenderer.cameraMob.posX - xCoordBlock;
            double yOff = this.mc.gameRenderer.cameraMob.posY - yCoordBlock;
            double zOff = this.mc.gameRenderer.cameraMob.posZ - zCoordBlock;
            if (xOff * xOff + yOff * yOff + zOff * zOff <= 256.0) {
                ParticleRenderer renderer = this.mc.particleRenderer;
                switch (particle) {
                    case BUBBLE:
                        renderer.addParticle(new BubbleParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, 8.0, 0.002));
                        break;
                    case BUBBLECOLUMN:
                        renderer.addParticle(new BubbleParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, 64.0, 0.02));
                        break;
                    case DRIPWATER:
                        renderer.addParticle(new DropletParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, MaterialList.water));
                        break;
                    case DRIPLAVA:
                        renderer.addParticle(new DropletParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, MaterialList.lava));
                        break;
                    case DUST:
                        renderer.addParticle(new DustParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case EXPLODE:
                        renderer.addParticle(new ExplosionParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case GUST:
                        renderer.addParticle(
                            new GustParticle(this.texLoader, this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition)
                        );
                        break;
                    case HUGEEXPLODE:
                        renderer.addParticle(
                            new ParticleEmitter(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, ParticleType.GUST)
                        );
                        break;
                    case FOAM:
                        ExplosionParticle foam = new ExplosionParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        foam.goUp = false;
                        renderer.addParticle(foam);
                        break;
                    case SWEEP:
                        renderer.addParticle(new SwipeParticle(this.texLoader, this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition));
                        break;
                    case FIREFLY: {
                        FireflyParticle firefly = new FireflyParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        firefly.setParticleColors(1.0F, 1.0F, 0.3F);
                        renderer.addParticle(firefly);
                        break;
                    }
                    case FIREFLYBLUE: {
                        FireflyParticle firefly = new FireflyParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        firefly.setParticleColors(0.6F, 0.9F, 1.0F);
                        renderer.addParticle(firefly);
                        break;
                    }
                    case FIREFLYPINK: {
                        FireflyParticle firefly = new FireflyParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        firefly.setParticleColors(1.0F, 0.7F, 1.0F);
                        renderer.addParticle(firefly);
                        break;
                    }
                    case LEAVES:
                        renderer.addParticle(new LeafParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case FLAME:
                        renderer.addParticle(new FlameParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, 48));
                        break;
                    case BLUEFLAME:
                        renderer.addParticle(new FlameParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, 132));
                        break;
                    case FIRE:
                        renderer.addParticle(new FlameParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case FOOTSTEP:
                        renderer.addParticle(new FootstepParticle(this.mc.textureLoader, this.world, xCoordBlock, yCoordBlock, zCoordBlock, MaterialList.sand));
                        break;
                    case FOOTSTEPSNOW:
                        renderer.addParticle(new FootstepParticle(this.mc.textureLoader, this.world, xCoordBlock, yCoordBlock, zCoordBlock, MaterialList.snow));
                        break;
                    case LARGESMOKE:
                        renderer.addParticle(new SmokeParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 2.5F));
                        break;
                    case LAVA:
                        renderer.addParticle(new LavaBlobParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock));
                        break;
                    case NOTE:
                        renderer.addParticle(new NoteParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, (float)xPosition, 2.0F));
                        break;
                    case SLIMEBALLPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.slimeBall));
                        break;
                    case SNOWBALLPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.snowball));
                        break;
                    case SANDBALLPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.sandball));
                        break;
                    case PEBBLEPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.rock));
                        break;
                    case BULLETPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.musketRound));
                        break;
                    case EGGPOOF:
                        renderer.addParticle(new ItemParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, ItemList.egg));
                        break;
                    case SMOKE:
                        renderer.addParticle(new SmokeParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 1.0F));
                        break;
                    case SPLASH:
                        renderer.addParticle(new SplashParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case RAINDROP:
                        renderer.addParticle(new RaindropParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock));
                        break;
                    case SUSPENDED:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock));
                        break;
                    case CRIT:
                        renderer.addParticle(new CritParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case SOUL:
                        renderer.addParticle(new SoulParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case MAGIC:
                        CritParticle crit = new CritParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        crit.setTextureIndex(133);
                        crit.setParticleColors(0.8F, 0.25F, 0.25F);
                        renderer.addParticle(crit);
                        break;
                    case DAMAGE:
                        CritParticle dmg = new CritParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        dmg.setTextureIndex(134);
                        dmg.forceMaxAge(10 + this.world.rand.nextInt(10));
                        renderer.addParticle(dmg);
                        break;
                    case TILECRACK:
                        if (OptionValues.fancyParticles) {
                            renderer.addParticle(
                                new BlockParticle3D(
                                    this.world,
                                    xCoordBlock,
                                    yCoordBlock,
                                    zCoordBlock,
                                    xPosition,
                                    yPosition,
                                    zPosition,
                                    BlockList.blocks[ParticleContext.id],
                                    ParticleContext.meta,
                                    1
                                )
                            );
                        } else {
                            renderer.addParticle(
                                new BlockParticle(
                                    this.world,
                                    xCoordBlock,
                                    yCoordBlock,
                                    zCoordBlock,
                                    xPosition,
                                    yPosition,
                                    zPosition,
                                    BlockList.blocks[ParticleContext.id],
                                    ParticleContext.meta,
                                    1
                                )
                            );
                        }

                        ParticleContext.resetContext();
                        break;
                    case ICONCRACK:
                        renderer.addParticle(
                            new ItemParticle(
                                this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition, ItemList.items[ParticleContext.id]
                            )
                        );
                        ParticleContext.resetContext();
                        break;
                    case POTION:
                        renderer.addParticle(
                            new ItemParticle(
                                this.world,
                                xCoordBlock,
                                yCoordBlock,
                                zCoordBlock,
                                xPosition,
                                yPosition,
                                zPosition,
                                ItemList.items[ParticleContext.id],
                                ParticleContext.meta
                            )
                        );
                        ParticleContext.resetContext();
                        break;
                    case TOWNAURA:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case SPORES:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 4.6, -4.6, 4.6, 1.0F, 0.3F, 0.3F));
                        break;
                    case SPORESCYAN:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 4.6, -4.6, 4.6, 0.3F, 0.9F, 1.0F));
                        break;
                    case SPORESGOLD:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 6.6, 6.6, 6.6, 0.9F, 0.3F, 0.7F));
                        break;
                    case SPORESPURPLE:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 6.6, 6.6, 6.6, 0.7F, 0.9F, 0.3F));
                        break;
                    case SPORESBLUE:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 0.19, 0.19, 0.19, 0.4F, 0.8F, 0.4F));
                        break;
                    case SPORESPINK:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 0.19, 0.19, 0.19, 0.6F, 0.8F, 0.4F));
                        break;
                    case ASH:
                        renderer.addParticle(new AshParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 22.6, -22.6, 22.6, 0.3F, 0.3F, 0.3F));
                        break;
                    case HEART:
                        renderer.addParticle(new HeartParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case GROW:
                        GlowParticle glow = new GlowParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
                        glow.setParticleColors(0.25F, 0.75F, 0.15F);
                        renderer.addParticle(glow);
                        break;
                    case SNOWSHOVEL:
                        renderer.addParticle(new DustParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 1.0, 1.0, 1.0));
                        break;
                    case FISH:
                        renderer.addParticle(new FishParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition));
                        break;
                    case BARRIER:
                        renderer.addParticle(new MarkerParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, BlockList.barrier, 0));
                        break;
                    case LIGHT:
                        renderer.addParticle(new MarkerParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, BlockList.light, 0));
                        break;
                    case SPELL:
                        Particle spell = new SpellParticle(this.world, xCoordBlock, yCoordBlock, zCoordBlock, 0.0, 0.0, 0.0);
                        renderer.addParticle(spell);
                        spell.setParticleColors((float)xPosition, (float)yPosition, (float)zPosition);
                        break;
                    default:
                        Minecraft.logger.warning("Particle type not defined!");
                }
            }
        }
    }

    @Override
    public void worldEvent(Player player, int event, int x, int y, int z, int meta) {
        Random randomizer = this.world.rand;
        switch (event) {
            case 2001:
                int index = meta & 4095;
                if (index > 0) {
                    Block block = BlockList.blocks[index];
                    StepSound snd = block.getStepSound(meta >> 12 & 0xFF);
                    this.mc
                        .sndManager
                        .playBlockSound(snd.getBreakSound(), x + 0.5F, y + 0.5F, z + 0.5F, (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
                }

                if (OptionValues.particleCount < 2) {
                    this.mc.particleRenderer.spawnBreakBlockParticles(x, y, z, meta & 4095, meta >> 12 & 0xFF);
                }
                break;
            case 2002:
                double xPos = x;
                double yPos = y;
                double zPos = z;
                if (meta > 19) {
                    meta %= 19;
                }

                for (int amount = 0; amount < 8; amount++) {
                    this.spawnParticle(
                        ParticleContext.setIconContext(ParticleType.POTION, ItemList.potion.id, meta),
                        xPos,
                        yPos,
                        zPos,
                        randomizer.nextGaussian() * 0.15,
                        randomizer.nextDouble() * 0.2,
                        randomizer.nextGaussian() * 0.15
                    );
                }

                int color = EffectList.effects[meta].getLiquidColor();
                float red = (color >> 16 & 0xFF) / 255.0F;
                float green = (color >> 8 & 0xFF) / 255.0F;
                float blue = (color >> 0 & 0xFF) / 255.0F;

                for (int attempt = 0; attempt < 100; attempt++) {
                    double velocity = randomizer.nextDouble() * 4.0;
                    double multiplier = randomizer.nextDouble() * Math.PI * 2.0;
                    double xOff = Math.cos(multiplier) * velocity;
                    double yOff = 0.01 + randomizer.nextDouble() * 0.5;
                    double zOff = Math.sin(multiplier) * velocity;
                    Particle particle = new SpellParticle(this.world, xPos + xOff * 0.1, yPos + 0.3, zPos + zOff * 0.1, xOff, yOff, zOff);
                    if (particle != null) {
                        this.mc.particleRenderer.addParticle(particle);
                        float opacity = 0.75F + randomizer.nextFloat() * 0.25F;
                        particle.setParticleColors(red * opacity, green * opacity, blue * opacity);
                        particle.multiplyVelocity((float)velocity);
                    }
                }

                this.world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.glass", 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);
                break;
            case 2003:
                if (OptionValues.particleCount < 2) {
                    this.mc.particleRenderer.spawnBreakBlockParticles(x, y, z, meta & 4095, meta >> 12 & 0xFF);
                }
        }
    }

    @Override
    public void playRecord(String recordName, int x, int y, int z, boolean stop) {
        if (!this.mc.sndManager.sndSystem.playing("streaming")) {
            if (recordName != null) {
                if (recordName.equals("Magnetic Circuit")) {
                    this.mc.HUD.nowPlaying.setTrack("Notch - " + recordName);
                } else {
                    this.mc.HUD.nowPlaying.setTrack("Soybean_56 - " + recordName);
                }

                float dst = 4.0F;
                if (recordName.equals("Endgame")) {
                    dst = 16.0F;
                }

                this.mc.sndManager.playStreaming(recordName, x, y, z, 1.0F, 1.0F, dst);
                this.mc.sndManager.sndSystem.setVolume("streaming", OptionValues.streamingVol);
            }
        } else if (stop) {
            this.mc.sndManager.sndSystem.stop("streaming");
        }
    }

    @Override
    public void showString(String msg) {
        this.mc.HUD.chatOverlay.addChatMessage(msg);
    }

    @Override
    public void sendTileEntity(int x, int y, int z, TileEntity tileEntity) {
    }

    public class ChunkSorter implements Comparator<Object> {
        int x;
        int z;

        public ChunkSorter(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int compare(WorldRenderer.RenderList list1, WorldRenderer.RenderList list2) {
            int x1 = list1.posX - this.x;
            int z1 = list1.posZ - this.z;
            int x2 = list2.posX - this.x;
            int z2 = list2.posZ - this.z;
            int coords1 = x1 * x1 + z1 * z1;
            int coords2 = x2 * x2 + z2 * z2;
            return Integer.compare(coords2, coords1);
        }

        @Override
        public int compare(Object obj1, Object obj2) {
            return this.compare((WorldRenderer.RenderList)obj1, (WorldRenderer.RenderList)obj2);
        }
    }

    public class EntitySorter implements Comparator<Object> {
        private Entity entity;

        public EntitySorter(Entity entity) {
            this.entity = entity;
        }

        public void prepareToSort(ChunkRenderer[] renderers, int countRenderers) {
            for (int index = 0; index < countRenderers; index++) {
                ChunkRenderer chunk = renderers[index];
                chunk.updateSortDistance(this.entity);
            }
        }

        public int compare(ChunkRenderer chunk1, ChunkRenderer chunk2) {
            float dist1 = chunk1.sortDist;
            float dist2 = chunk2.sortDist;
            return dist1 == dist2 ? 0 : (dist1 > dist2 ? 1 : -1);
        }

        @Override
        public int compare(Object obj1, Object obj2) {
            return this.compare((ChunkRenderer)obj1, (ChunkRenderer)obj2);
        }
    }

    public class RenderList {
        private IntBuffer buffer = Renderer.createIntBuffer(65536);
        public int posX;
        public int posY;
        public int posZ;
        private double playerX;
        private double playerY;
        private double playerZ;
        private boolean render = false;
        private boolean cached = false;

        public void setLocation(int posX, int posY, int posZ, double playerX, double playerY, double playerZ) {
            this.render = true;
            ((Buffer)this.buffer).clear();
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.playerX = playerX;
            this.playerY = playerY;
            this.playerZ = playerZ;
        }

        public boolean isRenderedAt(int x, int y, int z) {
            return !this.render ? false : x == this.posX && y == this.posY && z == this.posZ;
        }

        public void render(int slot) {
            this.buffer.put(slot);
            if (this.buffer.remaining() == 0) {
                this.render();
            }
        }

        public void render() {
            if (this.render) {
                if (!this.cached) {
                    ((Buffer)this.buffer).flip();
                    this.cached = true;
                }

                if (this.buffer.remaining() > 0) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(this.posX - this.playerX, this.posY - this.playerY, this.posZ - this.playerZ);
                    GL11.glCallLists(this.buffer);
                    GL11.glPopMatrix();
                }
            }
        }

        public void reset() {
            this.render = false;
            this.cached = false;
        }
    }
}
