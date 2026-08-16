package net.minecraft.server.world;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.WorldListener;
import net.minecraft.network.packet.world.BlockEventPacket;
import net.minecraft.network.packet.world.SoundPacket;
import net.minecraft.network.packet.misc.MusicPacket;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements WorldListener {
    private MinecraftServer mcServer;
    public WorldServer serverWorld;
    // Every WorldServer registers one of these as its listener, so this class is where entity
    // spawns and block changes leave the world and become packets. Hardcoding dimension 0 here
    // meant all five worlds funnelled into the Overworld's tracker and chunk manager.
    private final int dimension;

    public WorldManager(MinecraftServer minecraftServer1, WorldServer worldServer2) {
        this.mcServer = minecraftServer1;
        this.serverWorld = worldServer2;
        this.dimension = worldServer2.currDim.dimension;
    }

    @Override
    public void obtainEntitySkin(Entity entity1) {
        this.mcServer.getEntityTracker(this.dimension).trackEntity(entity1);
    }

    @Override
    public void releaseEntitySkin(Entity entity1) {
        this.mcServer.getEntityTracker(this.dimension).untrackEntity(entity1);
    }

    @Override
    public void playSound(String string1, double d2, double d4, double d6, float f8, float f9) {
        this.playSound(null, string1, d2, d4, d6, f8, f9);
    }

    @Override
    public void playSound(Player player, String string1, double d2, double d4, double d6, float f8, float f9) {
        double radius = f8 > 1.0F ? (16.0F * f8) : 16.0;
        this.mcServer.configManager.sendPacketToPlayerCoord(player, d2, d4, d6, radius, this.dimension,
            new SoundPacket(string1, d2, d4, d6, f8, f9, SoundPacket.TYPE_SOUND));
    }

    @Override
    public void markBlocksDirty(int i1, int i2, int i3, int i4, int i5, int i6) {
    }

    @Override
    public void updateAllRenderers() {
    }

    @Override
    public void changeBlocks(int i1, int i2, int i3) {
        this.mcServer.configManager.markBlockNeedsUpdate(i1, i2, i3, this.dimension);
    }

    @Override
    public void playRecord(String string1, int i2, int i3, int i4, boolean stop) {
        this.mcServer.configManager.sendPacketToAllCoord(i2, i3, i4, 64.0, this.dimension, new MusicPacket(string1, i2, i3, i4, stop));
    }

    @Override
    public void sendTileEntity(int i1, int i2, int i3, TileEntity tileEntity4) {
        this.mcServer.configManager.sentTileEntityToPlayer(i1, i2, i3, tileEntity4);
    }

    @Override
    public void worldEvent(Player entityPlayer1, int i2, int i3, int i4, int i5, int i6) {
        this.mcServer.configManager.sendPacketToPlayerCoord(entityPlayer1, i3, i4, i5, 64.0, this.dimension, new BlockEventPacket(i2, i3, i4, i5, i6));
    }

    @Override
    public void playMobSound(String string2, int i, double posX, double f, double posZ, float f3, float f4) {
        double radius = f3 > 1.0F ? (16.0F * f3) : 16.0;
        this.mcServer.configManager.sendPacketToPlayerCoord(null, posX, f, posZ, radius, this.dimension,
            new SoundPacket(string2, posX, f, posZ, f3, f4, SoundPacket.TYPE_MOB));
    }

    @Override
    public void playBlockSound(String string2, double posX, double f, double posZ, float f3, float f4) {
        this.playBlockSound(null, string2, posX, f, posZ, f3, f4);
    }

    @Override
    public void playBlockSound(Player player, String string2, double posX, double f, double posZ, float f3, float f4) {
        double radius = f3 > 1.0F ? (16.0F * f3) : 16.0;
        this.mcServer.configManager.sendPacketToPlayerCoord(player, posX, f, posZ, radius, this.dimension,
            new SoundPacket(string2, posX, f, posZ, f3, f4, SoundPacket.TYPE_BLOCK));
    }

    @Override
    public void playAmbienceSound(String soundName, float volume, float pitch) {
    }

    @Override
    public void spawnParticle(ParticleType pt, double d2, double d4, double d6, double d8, double d10, double d12) {
    }

    @Override
    public void showString(String s) {
        this.mcServer.configManager.sendMessageToOps(s);
    }
}
