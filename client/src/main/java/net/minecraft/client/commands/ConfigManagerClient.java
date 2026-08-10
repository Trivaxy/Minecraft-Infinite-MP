package net.minecraft.client.commands;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.player.Player;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.ChatPacket;

public class ConfigManagerClient implements ConfigManager {
    private Minecraft client;

    public ConfigManagerClient(Minecraft client) {
        this.client = client;
    }

    @Override
    public void banPlayer(CommandSource src, String[] name) {
    }

    @Override
    public void pardonPlayer(String name) {
    }

    @Override
    public void banIP(CommandSource src, String[] name) {
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
        if (packet instanceof ChatPacket) {
            ChatPacket pkt = (ChatPacket)packet;
            this.client.HUD.chatOverlay.addChatMessage(pkt.message);
        }
    }

    @Override
    public void sendPacketToAllDimension(Packet packet, int dimension) {
        if (packet instanceof ChatPacket) {
            ChatPacket pkt = (ChatPacket)packet;
            this.client.HUD.chatOverlay.addChatMessage(pkt.message);
        }
    }

    @Override
    public void sendPacketToAll(Packet packet) {
        if (packet instanceof ChatPacket) {
            ChatPacket pkt = (ChatPacket)packet;
            this.client.HUD.chatOverlay.addChatMessage(pkt.message);
        }
    }

    @Override
    public void whisper(String name, String message) {
        if (name.equals(this.client.player.username)) {
            this.client.HUD.chatOverlay.addChatMessage(message);
        }
    }

    @Override
    public void sendPacketToAllCoord(double x, double y, double z, double range, int dimension, Packet packet) {
        if (packet instanceof ChatPacket) {
            ChatPacket pkt = (ChatPacket)packet;
            this.client.HUD.chatOverlay.addChatMessage(pkt.message);
        }
    }

    @Override
    public void sendPacketToPlayerCoord(Player player, double x, double y, double z, double range, int dimension, Packet packet) {
        if (packet instanceof ChatPacket) {
            ChatPacket pkt = (ChatPacket)packet;
            this.client.HUD.chatOverlay.addChatMessage(pkt.message);
        }
    }

    @Override
    public void sendMessageToOps(String message) {
        this.client.HUD.chatOverlay.addChatMessage(message);
    }

    @Override
    public boolean sendPacketToPlayer(String name, Packet packet) {
        if (name.equals(this.client.player.username)) {
            if (packet instanceof ChatPacket) {
                ChatPacket pkt = (ChatPacket)packet;
                this.client.HUD.chatOverlay.addChatMessage(pkt.message);
            }

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
}
