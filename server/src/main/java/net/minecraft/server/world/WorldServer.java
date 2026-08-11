package net.minecraft.server.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.chunk.loader.handler.PlayerFileData;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.dimension.Dimension;
import net.minecraft.game.world.util.Explosion;
import net.minecraft.network.packet.entity.EntityStatePacket;
import net.minecraft.network.packet.world.ExplosionPacket;
import net.minecraft.network.packet.container.PlayNotePacket;
import net.minecraft.server.MinecraftServer;

public class WorldServer extends World {
    public ChunkProviderServer chunkProviderServer;
    public boolean blockBreaking = false;
    public boolean levelSaving;
    private MinecraftServer mcServer;
    private Int2ObjectOpenHashMap<Entity> hashMap = new Int2ObjectOpenHashMap<>();

    public WorldServer(MinecraftServer minecraftServer1, SaveHandler var2, String string3, int i4, long j5, WorldSettings ws) {
        super(var2, string3, j5, ws, Dimension.getDimension(i4));
        this.mcServer = minecraftServer1;
        this.saveInterval = this.mcServer.autosaveinterval;
    }

    @Override
    public void tickEntity(Entity entity1, boolean z2) {
        if (!this.mcServer.spawnPeacefulMobs && entity1 instanceof Animal) {
            entity1.setDead();
        }

        super.tickEntity(entity1, z2);
    }

    public void moveEntity(Entity entity1, boolean z2) {
        super.tickEntity(entity1, z2);
    }

    @Override
    public ChunkLoader getChunkProvider() {
        FileLoader var1 = this.saveHandler.getChunkLoader(this.currDim);
        this.chunkProviderServer = new ChunkProviderServer(this, var1, this.currDim.getChunkProvider());
        return this.chunkProviderServer;
    }

    public List<TileEntity> getTileEntityList(int i1, int i2, int i3, int i4, int i5, int i6) {
        ArrayList<TileEntity> arrayList7 = new ArrayList<>();

        for (int i8 = 0; i8 < this.tileEntities.size(); i8++) {
            TileEntity tileEntity9 = this.tileEntities.get(i8);
            if (tileEntity9.x >= i1 && tileEntity9.y >= i2 && tileEntity9.z >= i3 && tileEntity9.x < i4 && tileEntity9.y < i5 && tileEntity9.z < i6) {
                arrayList7.add(tileEntity9);
            }
        }

        return arrayList7;
    }

    @Override
    public void obtainEntitySkin(Entity e) {
        super.obtainEntitySkin(e);
        this.addEntityToHashmap(e);
    }

    @Override
    protected void releaseEntitySkin(Entity entity1) {
        super.releaseEntitySkin(entity1);
        this.removeEntityFromHashmap(entity1);
    }

    public Entity findEntityBasedOnId(int i1) {
        return this.hashMap.get(i1);
    }

    @Override
    public void addEntityToHashmap(Entity e) {
        if (this.hashMap == null) {
            this.hashMap = new Int2ObjectOpenHashMap<>();
        }

        this.hashMap.put(e.entityId, e);
    }

    @Override
    public void removeEntityFromHashmap(Entity e) {
        this.hashMap.remove(e.entityId);
    }

    @Override
    public void updateServerEntity(Entity entity1, byte b2) {
        EntityStatePacket packet38EntityStatus3 = new EntityStatePacket(entity1.entityId, b2);
        this.mcServer.getEntityTracker(this.currDim.dimension).sendPacketToTrackedPlayersAndTrackedEntity(entity1, packet38EntityStatus3);
    }

    @Override
    public Explosion createExplosion(Entity entity1, double d2, double d4, double d6, float f8) {
        Explosion explosion10 = new Explosion(this, entity1, d2, d4, d6, f8);
        explosion10.explode();
        explosion10.addParticles(false);
        this.mcServer.configManager
            .sendPacketToAllCoord(d2, d4, d6, 64.0, this.currDim.dimension, new ExplosionPacket(d2, d4, d6, f8, explosion10.destroyedBlocks));
        return explosion10;
    }

    @Override
    public void blockEvent(int i1, int i2, int i3, int i4, int i5) {
        super.blockEvent(i1, i2, i3, i4, i5);
        this.mcServer.configManager.sendPacketToAllCoord(i1, i2, i3, 64.0, this.currDim.dimension, new PlayNotePacket(i1, i2, i3, i4, i5));
    }

    public void removePlayer(Entity entity) {
        entity.setDead();
        if (entity instanceof Player) {
            this.players.remove(entity);
            this.wakePlayers();
        }

        int x = entity.chunkX;
        int z = entity.chunkZ;
        if (entity.addedToChunk && this.chunkExists(x, z)) {
            this.getChunk(x, z).removeEntity(entity);
        }

        this.entities.remove(entity);
        this.releaseEntitySkin(entity);
    }

    // level.dat is one file shared by all five dimensions, and World.saveData() writes the
    // player list into it. Letting every world write means five racing rewrites with five
    // different player lists, so only the Overworld owns the file. The other dimensions still
    // do the rest of the work — the session-lock check and their map data.
    @Override
    public void saveData() {
        if (this.currDim.dimension == 0) {
            super.saveData();
        } else {
            this.checkSessionLock();
            this.mapStorage.saveAllData();
        }
    }

    @Override
    public void setSaving(boolean state) {
        this.levelSaving = state;
    }

    public PlayerFileData getPlayerStorage() {
        return this.saveHandler.getPlayerStorage();
    }

    @Override
    public void postSaveCleanup() {
        this.saveHandler.cleanupFileCache();
    }
}
