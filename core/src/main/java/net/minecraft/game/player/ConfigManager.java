package net.minecraft.game.player;

import java.util.Set;
import net.minecraft.game.entity.player.Player;
import net.minecraft.network.packet.Packet;

public interface ConfigManager {
    void banPlayer(String bannedBy, String name, String reason);

    void pardonPlayer(String var1);

    void banIP(String bannedBy, String address, String reason);

    void pardonIP(String var1);

    void opPlayer(String var1);

    void deopPlayer(String var1);

    boolean allowLogin(String var1);

    boolean isOp(String var1);

    Player getPlayer(String var1);

    void sendPacketToAllExcluding(Packet var1, Player var2);

    void sendPacketToAllDimension(Packet var1, int var2);

    void sendPacketToAll(Packet var1);

    void whisper(String var1, String var2);

    void sendPacketToAllCoord(double var1, double var3, double var5, double var7, int var9, Packet var10);

    void sendPacketToPlayerCoord(Player var1, double var2, double var4, double var6, double var8, int var10, Packet var11);

    void sendMessageToOps(String var1);

    boolean sendPacketToPlayer(String var1, Packet var2);

    void whiteList(String var1);

    void unWhiteList(String var1);

    Set<String> getWhiteListedIPs();

    int getMaxPlayers();

    void teleportPlayerTo(Player var1, double var2, double var4, double var6, float var8, float var9);

    void setGamemode(Player var1, int var2);

    String getPlayerList();

    void kickPlayer(Player var1, String var2);

    void savePlayerStates();

    void reloadWhiteList();

    Set<String> getPlayerNames();
}
