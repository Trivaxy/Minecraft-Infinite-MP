package net.minecraft.client.render.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.camera.Camera;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.tileentity.TileEntityRenderManager;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkCache;
import org.lwjgl.opengl.GL11;

public class ChunkRenderer {
    private static final Tessellator tessellator = Tessellator.instance;
    private static BlocksRenderer blocksRenderer;
    public static int chunksUpdated = 0;
    protected Tessellator.VertexState vertexState;
    public World world;
    public List<TileEntity> tileEntityRenderers = new ArrayList<>();
    private final List<TileEntity> tileEntities;
    public int x;
    public int y;
    public int z;
    public int posX;
    public int posY;
    public int posZ;
    public int negX;
    public int negY;
    public int negZ;
    public int clipX;
    public int clipY;
    public int clipZ;
    public int width;
    public int height;
    public int depth;
    public int index;
    final int glRenderList;
    public double rendererMinX;
    public double rendererMinY;
    public double rendererMinZ;
    public double rendererMaxX;
    public double rendererMaxY;
    public double rendererMaxZ;
    public double entityDist;
    public float sortDist;
    public boolean[] skipLayer = new boolean[2];
    public boolean isLit;
    public boolean isInitialized = false;
    public boolean isVisible = true;
    public boolean isInFrustum = false;
    public boolean isOccluded;
    public boolean needsRefresh;
    public boolean isSorted;
    public boolean skipAllLayers;
    public boolean needResort;

    public ChunkRenderer(World world, List<TileEntity> teList, int x, int y, int z, int size, int layer) {
        this.world = world;
        this.tileEntities = teList;
        this.width = this.height = this.depth = size;
        this.glRenderList = layer;
        this.x = -999;
        this.setPosition(x, y, z);
        this.needsRefresh = false;
        this.skipAllLayers = true;
        this.vertexState = null;
        blocksRenderer = new BlocksRenderer(world);
    }

    public void setPosition(int x, int y, int z) {
        if (x != this.x || y != this.y || z != this.z) {
            this.stop();
            this.x = x;
            this.y = y;
            this.z = z;
            this.posX = x + this.width / 2;
            this.posY = y + this.height / 2;
            this.posZ = z + this.depth / 2;
            this.clipX = x & 1023;
            this.clipY = y;
            this.clipZ = z & 1023;
            this.negX = x - this.clipX;
            this.negY = y - this.clipY;
            this.negZ = z - this.clipZ;
            this.rendererMinX = x;
            this.rendererMinY = y;
            this.rendererMinZ = z;
            this.rendererMaxX = x + this.width;
            this.rendererMaxY = y + this.height;
            this.rendererMaxZ = z + this.depth;
            GL11.glNewList(this.glRenderList + 2, 4864);
            EntityRenderer.renderAABB(BoundingBox.getBoundingBox(this.clipX, this.clipY, this.clipZ, this.clipX + 16, this.clipY + 16, this.clipZ + 16));
            GL11.glEndList();
            this.markDirty();
        }
    }

    public void refreshChunk() {
        if (this.needsRefresh) {
            int startX = this.x;
            int startY = this.y;
            int startZ = this.z;
            int endX = this.x + this.width;
            int endY = this.y + this.height;
            int endZ = this.z + this.depth;

            for (int layer = 0; layer < 2; layer++) {
                this.skipLayer[layer] = true;
            }

            this.skipAllLayers = true;
            Chunk.isLit = false;
            HashSet<TileEntity> teMap = new HashSet<>(this.tileEntityRenderers);
            this.tileEntityRenderers.clear();
            ChunkCache cache = new ChunkCache(this.world, startX - 1, startY - 1, startZ - 1, endX + 1, endY + 1, endZ + 1);
            if (!cache.areChunksEmpty()) {
                chunksUpdated++;
                this.vertexState = null;

                for (int layer = 0; layer < 2; layer++) {
                    boolean reRenderLayer = false;
                    boolean skipLayer = false;
                    boolean renderBlocks = false;

                    for (int y = startY; y < endY; y++) {
                        for (int z = startZ; z < endZ; z++) {
                            for (int x = startX; x < endX; x++) {
                                int blockID = cache.getBlockId(x, y, z);
                                if (blockID > 0) {
                                    if (!renderBlocks) {
                                        renderBlocks = true;
                                        this.prepare(layer);
                                    }

                                    Block block = BlockList.blocks[blockID];
                                    if (block != null) {
                                        if (layer == 0 && block instanceof ContainerBlock) {
                                            TileEntity tileEntity = cache.getTileEntity(x, y, z);
                                            if (TileEntityRenderManager.instance.hasRenderer(tileEntity)) {
                                                this.tileEntityRenderers.add(tileEntity);
                                            }
                                        }

                                        int blockLayer = block.renderPass();
                                        if (blockLayer > layer) {
                                            reRenderLayer = true;
                                        }

                                        boolean canRender = blockLayer == layer;
                                        if (canRender) {
                                            skipLayer |= blocksRenderer.renderBlock(block, x, y, z);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (skipLayer) {
                        this.skipLayer[layer] = false;
                    }

                    if (renderBlocks) {
                        this.render(layer, Minecraft.getMinecraft().gameRenderer.cameraMob);
                    } else {
                        skipLayer = false;
                    }

                    if (!reRenderLayer) {
                        break;
                    }
                }
            }

            HashSet<TileEntity> teList = new HashSet<>(this.tileEntityRenderers);
            teList.removeAll(teMap);
            this.tileEntities.addAll(teList);
            this.tileEntityRenderers.forEach(teMap::remove);
            this.tileEntities.removeAll(teMap);
            this.isLit = Chunk.isLit;
            this.isInitialized = true;
            this.skipAllLayers = this.skipLayer[0] && this.skipLayer[1];
            this.finish();
        }
    }

    protected void prepare(int layer) {
        GL11.glNewList(this.glRenderList + layer, 4864);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.clipX, this.clipY, this.clipZ);
        GL11.glTranslatef(-this.width / 2.0F, -this.height / 2.0F, -this.depth / 2.0F);
        GL11.glTranslatef(this.width / 2.0F, this.height / 2.0F, this.depth / 2.0F);
        tessellator.begin();
        tessellator.setTranslation(-this.x, -this.y, -this.z);
    }

    protected void render(int layer, Mob entity) {
        if (layer == 1 && !this.skipLayer[layer]) {
            this.vertexState = tessellator.getVertexState((float)entity.posX, (float)entity.posY, (float)entity.posZ);
            GL11.glDepthMask(true);
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEndList();
        tessellator.setTranslation(0.0, 0.0, 0.0);
    }

    public void sortChunk(Mob entity) {
        if (this.vertexState != null && !this.skipLayer[1]) {
            this.prepare(1);
            tessellator.setVertexState(this.vertexState);
            this.render(1, entity);
        }
    }

    protected void finish() {
        if (!this.skipAllLayers && !this.isSorted) {
            Minecraft.getMinecraft().worldRenderer.addToSortedChunks(this);
        }
    }

    public void stop() {
        for (int layer = 0; layer < 2; layer++) {
            this.skipLayer[layer] = true;
        }

        this.skipAllLayers = true;
        this.isInFrustum = false;
        this.isInitialized = false;
        this.vertexState = null;
    }

    public void dispose() {
        this.stop();
        this.world = null;
    }

    public float distanceToEntity(Entity entity) {
        float x = (float)(entity.posX - this.posX);
        float y = (float)(entity.posY - this.posY);
        float z = (float)(entity.posZ - this.posZ);
        return x * x + y * y + z * z;
    }

    public void updateSortDistance(Entity entity) {
        double dx = entity.posX - this.posX;
        double dy = entity.posY - this.posY;
        double dz = entity.posZ - this.posZ;
        double dXzSq = dx * dx + dz * dz;
        this.entityDist = dXzSq;
        this.sortDist = (float)(dXzSq + dy * dy);
    }

    public int getLayer(int layer) {
        return this.glRenderList + layer;
    }

    public void updateVisibility(Camera cam) {
        this.isInFrustum = cam.isInFrustum(this.rendererMinX, this.rendererMinY, this.rendererMinZ, this.rendererMaxX, this.rendererMaxY, this.rendererMaxZ);
    }

    public boolean skipAllLayers() {
        return this.skipAllLayers;
    }

    public void markDirty() {
        this.needsRefresh = true;
    }
}
