package net.minecraft.game.world.chunk;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.LightLayer;

public class EmptyChunk extends Chunk {
    public EmptyChunk(World world, int x, int z) {
        super(world, x, z);
        this.neverSave = true;
    }

    @Override
    public boolean isAtLocation(int x, int z) {
        return x == this.xPosition && z == this.zPosition;
    }

    @Override
    public int getHeightmap(int x, int z) {
        return 0;
    }

    @Override
    public void generateHeightMap() {
    }

    @Override
    public int getBlockID(int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean setBlockAndState(int x, int y, int z, int id, int meta) {
        return true;
    }

    @Override
    public boolean setBlockID(int x, int y, int z, int id) {
        return true;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockMetadata(int x, int y, int z, int meta) {
    }

    @Override
    public int getLightValue(LightLayer light, int x, int y, int z) {
        return 0;
    }

    @Override
    public void setLightValue(LightLayer light, int x, int y, int z, int i) {
    }

    @Override
    public int getBlockLightValue(int x, int y, int z, int id) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity, int i) {
    }

    @Override
    public boolean isBlockExposed(int x, int y, int z) {
        return false;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public void addTileEntity(TileEntity tileEntity) {
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
    }

    @Override
    public void removeTileEntity(int x, int y, int z) {
    }

    @Override
    public void onChunkLoad() {
    }

    @Override
    public void onChunkUnload() {
    }

    @Override
    public void setModified() {
    }

    @Override
    public void getEntitiesInBB(Entity entity, BoundingBox bb, List<Entity> list) {
    }

    @Override
    public void getSpecificEntitiesInBB(Class<?> targetClass, BoundingBox aabb, ArrayList<Entity> list) {
    }

    @Override
    public boolean save(boolean condition) {
        return false;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void populateChunk(ChunkLoader chunkProvider, ChunkLoader loader, int x, int z) {
    }

    @Override
    public boolean layerEmpty(int x, int z) {
        return true;
    }
}
