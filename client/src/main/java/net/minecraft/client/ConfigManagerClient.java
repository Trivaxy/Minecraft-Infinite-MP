package net.minecraft.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.player.ConfigManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.ChatPacket;

public class ConfigManagerClient implements ConfigManager {
    private Minecraft client;

    public ConfigManagerClient(Minecraft client) {
        this.client = client;
    }

    @Override
    public void banPlayer(String bannedBy, String name, String reason) {
    }

    @Override
    public void pardonPlayer(String name) {
    }

    @Override
    public void banIP(String bannedBy, String address, String reason) {
    }

    @Override
    public void pardonIP(String name) {
    }

    @Override
    public void opPlayer(String name) {
    }

    @Override
    public void deopPlayer(String name) {
    }

    @Override
    public boolean allowLogin(String name) {
        return true;
    }

    @Override
    public boolean isOp(String name) {
        return this.client.world.getSettings().getCheats();
    }

    @Override
    public Player getPlayer(String name) {
        return name.equals(this.client.player.username) ? this.client.player : null;
    }

    @Override
    public void sendPacketToAllExcluding(Packet packet, Player player) {
        this.showIfChat(packet);
    }

    @Override
    public void sendPacketToAllDimension(Packet packet, int dimension) {
        this.showIfChat(packet);
    }

    @Override
    public void sendPacketToAll(Packet packet) {
        this.showIfChat(packet);
    }

    @Override
    public void whisper(String name, String message) {
        if (name.equals(this.client.player.username)) {
            this.client.HUD.chatOverlay.addChatMessage(message);
        }
    }

    @Override
    public void sendPacketToAllCoord(double x, double y, double z, double range, int dimension, Packet packet) {
        this.showIfChat(packet);
    }

    @Override
    public void sendPacketToPlayerCoord(Player player, double x, double y, double z, double range, int dimension, Packet packet) {
        this.showIfChat(packet);
    }

    @Override
    public void sendMessageToOps(String message) {
        this.client.HUD.chatOverlay.addChatMessage(message);
    }

    @Override
    public boolean sendPacketToPlayer(String name, Packet packet) {
        if (name.equals(this.client.player.username)) {
            this.showIfChat(packet);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void whiteList(String name) {
    }

    @Override
    public void unWhiteList(String name) {
    }

    @Override
    public Set<String> getWhiteListedIPs() {
        return new HashSet<>();
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public void teleportPlayerTo(Player player, double x, double y, double z, float xr, float yr) {
        player.teleportTo(x, y, z, xr, yr);
    }

    @Override
    public void setGamemode(Player player, int gamemode) {
        this.client.setGamemode(gamemode);
    }

    @Override
    public String getPlayerList() {
        return this.client.player.username;
    }

    @Override
    public void kickPlayer(Player player, String reason) {
    }

    @Override
    public void savePlayerStates() {
    }

    @Override
    public void reloadWhiteList() {
    }

    @Override
    public Set<String> getPlayerNames() {
        return this.client.player == null ? Collections.<String>emptySet() : Collections.singleton(this.client.player.username);
    }

    private void showIfChat(Packet packet) {
        if (packet instanceof ChatPacket) {
            this.client.HUD.chatOverlay.addChatMessage(((ChatPacket)packet).message);
        }
    }
}
