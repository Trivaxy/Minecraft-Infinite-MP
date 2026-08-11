package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.world.PrepChunkPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateChunkPacket;
import net.minecraft.network.packet.world.UpdateSectionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.EntityPlayerMP;

public class ServerChunkDistance {
    public List<EntityPlayerMP> players = new ArrayList<>();
    private final Long2ObjectOpenHashMap<ServerChunkDistance.LoadedChunk> playerInstances = new Long2ObjectOpenHashMap<>();
    private final List<ServerChunkDistance.LoadedChunk> chonksToUpdate = new ArrayList<>();
    private final MinecraftServer mcServer;
    private final int dimension;
    private final int playerViewRadius;
    private final int[][] facing = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public ServerChunkDistance(MinecraftServer server, int dimension, int viewRadius) {
        if (viewRadius > 15) {
            throw new IllegalArgumentException("Too big view radius!");
        }

        if (viewRadius < 3) {
            throw new IllegalArgumentException("Too small view radius!");
        }

        this.playerViewRadius = viewRadius;
        this.mcServer = server;
        this.dimension = dimension;
    }

    public WorldServer getWorld() {
        return this.mcServer.getWorldManager(this.dimension);
    }

    public void tick() {
        for (ServerChunkDistance.LoadedChunk playerInstance : this.chonksToUpdate) {
            playerInstance.tick();
        }

        this.chonksToUpdate.clear();
    }

    private ServerChunkDistance.LoadedChunk get(int chunkX, int chunkZ, boolean put) {
        long pos = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        ServerChunkDistance.LoadedChunk inst = this.playerInstances.get(pos);
        if (inst == null && put) {
            inst = new ServerChunkDistance.LoadedChunk(chunkX, chunkZ);
            this.playerInstances.put(pos, inst);
        }

        return inst;
    }

    public void markBlockNeedsUpdate(int x, int y, int z) {
        int cx = x >> 4;
        int cz = z >> 4;
        ServerChunkDistance.LoadedChunk inst = this.get(cx, cz, false);
        if (inst != null) {
            inst.markBlockNeedsUpdate(x & 15, y, z & 15);
        }
    }

    public void addPlayer(EntityPlayerMP player) {
        int playerCX = (int)player.posX >> 4;
        int playerCZ = (int)player.posZ >> 4;
        player.serverPosX = player.posX;
        player.serverPosZ = player.posZ;
        int i4 = 0;
        int i5 = this.playerViewRadius;
        int i6 = 0;
        int i7 = 0;
        this.get(playerCX, playerCZ, true).addPlayer(player);

        for (int i8 = 1; i8 <= i5 * 2; i8++) {
            for (int i9 = 0; i9 < 2; i9++) {
                int[] i10 = this.facing[i4++ % 4];

                for (int i11 = 0; i11 < i8; i11++) {
                    i6 += i10[0];
                    i7 += i10[1];
                    this.get(playerCX + i6, playerCZ + i7, true).addPlayer(player);
                }
            }
        }

        i4 %= 4;

        for (int var13 = 0; var13 < i5 * 2; var13++) {
            i6 += this.facing[i4][0];
            i7 += this.facing[i4][1];
            this.get(playerCX + i6, playerCZ + i7, true).addPlayer(player);
        }

        this.players.add(player);
    }

    public void removePlayer(EntityPlayerMP player) {
        int i2 = (int)player.serverPosX >> 4;
        int i3 = (int)player.serverPosZ >> 4;

        for (int i4 = i2 - this.playerViewRadius; i4 <= i2 + this.playerViewRadius; i4++) {
            for (int i5 = i3 - this.playerViewRadius; i5 <= i3 + this.playerViewRadius; i5++) {
                ServerChunkDistance.LoadedChunk inst = this.get(i4, i5, false);
                if (inst != null) {
                    inst.removePlayer(player);
                }
            }
        }

        this.players.remove(player);
    }

    private boolean isPlayerInRange(int i1, int i2, int i3, int i4) {
        int i5 = i1 - i3;
        int i6 = i2 - i4;
        return i5 >= -this.playerViewRadius && i5 <= this.playerViewRadius && i6 >= -this.playerViewRadius && i6 <= this.playerViewRadius;
    }

    public void setPlayerPosition(EntityPlayerMP player) {
        int playerCX = (int)player.posX >> 4;
        int playerCZ = (int)player.posZ >> 4;
        double d4 = player.serverPosX - player.posX;
        double d6 = player.serverPosZ - player.posZ;
        double d8 = d4 * d4 + d6 * d6;
        if (d8 >= 64.0) {
            int i10 = (int)player.serverPosX >> 4;
            int i11 = (int)player.serverPosZ >> 4;
            int i12 = playerCX - i10;
            int i13 = playerCZ - i11;
            if (i12 != 0 || i13 != 0) {
                for (int i14 = playerCX - this.playerViewRadius; i14 <= playerCX + this.playerViewRadius; i14++) {
                    for (int i15 = playerCZ - this.playerViewRadius; i15 <= playerCZ + this.playerViewRadius; i15++) {
                        if (!this.isPlayerInRange(i14, i15, i10, i11)) {
                            this.get(i14, i15, true).addPlayer(player);
                        }

                        if (!this.isPlayerInRange(i14 - i12, i15 - i13, playerCX, playerCZ)) {
                            ServerChunkDistance.LoadedChunk playerInstance16 = this.get(i14 - i12, i15 - i13, false);
                            if (playerInstance16 != null) {
                                playerInstance16.removePlayer(player);
                            }
                        }
                    }
                }

                player.serverPosX = player.posX;
                player.serverPosZ = player.posZ;
            }
        }
    }

    public int getMaxTrackingDistance() {
        return this.playerViewRadius * 16 - 16;
    }

    class LoadedChunk {
        private final List<EntityPlayerMP> players = new ArrayList<>();
        private final int chunkX;
        private final int chunkZ;
        private final long currentChunk;
        private final int[] blocksToUpdate = new int[64];
        private int numBlocksToUpdate = 0;
        private int distModifier;
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;
        private int minZ;
        private int maxZ;

        public LoadedChunk(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.currentChunk = ChunkUtils.getPackedCoords(chunkX, chunkZ);
            ServerChunkDistance.this.getWorld().chunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP entityPlayerMP1) {
            if (this.players.contains(entityPlayerMP1)) {
                throw new IllegalStateException("Failed to add player. " + entityPlayerMP1 + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
            }

            entityPlayerMP1.loadedChunksMP.add(this.currentChunk);
            entityPlayerMP1.playerNetServerHandler
                .sendPacket(new PrepChunkPacket(ChunkUtils.getChunkX(this.currentChunk), ChunkUtils.getChunkZ(this.currentChunk), true));
            this.players.add(entityPlayerMP1);
            entityPlayerMP1.loadedChunks.add(this.currentChunk);
        }

        public void removePlayer(EntityPlayerMP entityPlayerMP1) {
            if (this.players.contains(entityPlayerMP1)) {
                this.players.remove(entityPlayerMP1);
                if (this.players.isEmpty()) {
                    long j2 = ChunkUtils.getPackedCoords(this.chunkX, this.chunkZ);
                    ServerChunkDistance.this.playerInstances.remove(j2);
                    if (this.numBlocksToUpdate > 0) {
                        ServerChunkDistance.this.chonksToUpdate.remove(this);
                    }

                    ServerChunkDistance.this.getWorld().chunkProviderServer.addChunk(this.chunkX, this.chunkZ);
                }

                entityPlayerMP1.loadedChunks.remove(this.currentChunk);
                if (entityPlayerMP1.loadedChunksMP.contains(this.currentChunk)) {
                    entityPlayerMP1.playerNetServerHandler.sendPacket(new PrepChunkPacket(this.chunkX, this.chunkZ, false));
                }
            }
        }

        public void markBlockNeedsUpdate(int x, int y, int z) {
            if (this.numBlocksToUpdate == 0) {
                ServerChunkDistance.this.chonksToUpdate.add(this);
                this.minX = this.maxX = x;
                this.minY = this.maxY = y;
                this.minZ = this.maxZ = z;
            }

            if (this.minX > x) {
                this.minX = x;
            }

            if (this.maxX < x) {
                this.maxX = x;
            }

            if (this.minY > y) {
                this.minY = y;
            }

            if (this.maxY < y) {
                this.maxY = y;
            }

            if (this.minZ > z) {
                this.minZ = z;
            }

            if (this.maxZ < z) {
                this.maxZ = z;
            }

            this.distModifier |= 1 << (y >> 4);
            if (this.numBlocksToUpdate < 64) {
                // Packed coordinate layout: x in bits 20-23, z in bits 16-19,
                // full world height in the low 16 bits.
                int s4 = x << 20 | z << 16 | y;

                for (int i5 = 0; i5 < this.numBlocksToUpdate; i5++) {
                    if (this.blocksToUpdate[i5] == s4) {
                        return;
                    }
                }

                this.blocksToUpdate[this.numBlocksToUpdate++] = s4;
            }
        }

        public void sendPacketToPlayersInInstance(Packet packet1) {
            for (EntityPlayerMP player : this.players) {
                if (player.loadedChunksMP.contains(this.currentChunk)) {
                    player.playerNetServerHandler.sendPacket(packet1);
                }
            }
        }

        public void tick() {
            WorldServer worldServer1 = ServerChunkDistance.this.getWorld();
            if (this.numBlocksToUpdate != 0) {
                if (this.numBlocksToUpdate == 1) {
                    int i2 = this.chunkX * 16 + this.minX;
                    int i3 = this.minY;
                    int i4 = this.chunkZ * 16 + this.minZ;
                    this.sendPacketToPlayersInInstance(new UpdateBlockPacket(i2, i3, i4, worldServer1));
                    if (Block.isContainer.get(worldServer1.getBlockId(i2, i3, i4))) {
                        this.updateTileEntity(worldServer1.getTileEntity(i2, i3, i4));
                    }
                } else if (this.numBlocksToUpdate != 64) {
                    this.sendPacketToPlayersInInstance(
                        new UpdateSectionPacket(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, worldServer1)
                    );

                    for (int i2 = 0; i2 < this.numBlocksToUpdate; i2++) {
                        int i3 = this.chunkX * 16 + (this.numBlocksToUpdate >> 20 & 15);
                        int i4 = this.numBlocksToUpdate & 0xFFFF;
                        int i5 = this.chunkZ * 16 + (this.numBlocksToUpdate >> 16 & 15);
                        if (Block.isContainer.get(worldServer1.getBlockId(i3, i4, i5))) {
                            this.updateTileEntity(worldServer1.getTileEntity(i3, i4, i5));
                        }
                    }
                } else {
                    this.minY = this.minY / 2 * 2;
                    this.maxY = (this.maxY / 2 + 1) * 2;
                    int j = this.chunkX * 16;
                    int i1 = this.chunkZ * 16;
                    this.sendPacketToPlayersInInstance(
                        new UpdateChunkPacket(worldServer1.getChunk(this.chunkX, this.chunkZ), false, this.distModifier)
                    );

                    for (int l1 = 0; l1 < worldServer1.getMaxSections(); l1++) {
                        if ((this.distModifier & 1 << l1) != 0) {
                            int j2 = l1 << 4;

                            for (TileEntity te : worldServer1.getTileEntityList(j, j2, i1, j + 16, j2 + 16, i1 + 16)) {
                                this.updateTileEntity(te);
                            }
                        }
                    }
                }

                this.numBlocksToUpdate = 0;
            }
        }

        private void updateTileEntity(TileEntity tileEntity1) {
            if (tileEntity1 != null) {
                Packet packet2 = tileEntity1.getPacket();
                if (packet2 != null) {
                    this.sendPacketToPlayersInInstance(packet2);
                }
            }
        }
    }
}
