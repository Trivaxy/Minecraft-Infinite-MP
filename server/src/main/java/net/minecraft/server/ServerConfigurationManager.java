package net.minecraft.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.chunk.loader.handler.PlayerFileData;
import net.minecraft.game.world.util.Position;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.network.packet.OpPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PlayerInfoPacket;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.network.packet.UpdateTimePacket;
import net.minecraft.network.packet.WorldEventPacket;
import net.minecraft.server.network.NetLoginHandler;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.server.player.ItemInWorldManager;
import net.minecraft.server.player.bans.BanEntry;
import net.minecraft.server.player.bans.BanList;
import net.minecraft.server.world.ServerChunkDistance;
import net.minecraft.server.world.WorldServer;

public class ServerConfigurationManager implements ConfigManager {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    public static Logger logger = Logger.getLogger("Minecraft");
    public List<EntityPlayerMP> playerEntities = new ArrayList<>();
    private final MinecraftServer mcServer;
    private final ServerChunkDistance[] playerManagerObj = new ServerChunkDistance[MinecraftServer.DIMENSION_COUNT];
    private final int maxPlayers;
    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));
    private final Set<String> ops = new HashSet<>();
    private final Set<String> whiteListedIPs = new HashSet<>();
    private final File opFile;
    private final File whitelistPlayersFile;
    private PlayerFileData playerNBTManagerObj;
    private final boolean whiteListEnforced;
    private int playerUseTime = 0;

    public ServerConfigurationManager(MinecraftServer minecraftServer1) {
        this.mcServer = minecraftServer1;
        this.opFile = minecraftServer1.getFile("ops.txt");
        this.whitelistPlayersFile = minecraftServer1.getFile("white-list.txt");
        int i2 = minecraftServer1.properties.get("view-distance", 10);

        for (int d = 0; d < this.playerManagerObj.length; d++) {
            this.playerManagerObj[d] = new ServerChunkDistance(minecraftServer1, d, i2);
        }

        this.maxPlayers = minecraftServer1.properties.get("max-players", 20);
        this.whiteListEnforced = minecraftServer1.properties.get("white-list", false);
        this.bannedPlayers.loadBanList();
        this.bannedPlayers.saveToFileWithHeader();
        this.bannedIPs.loadBanList();
        this.bannedIPs.saveToFileWithHeader();
        this.loadOps();
        this.loadWhiteList();
        this.saveOps();
        this.saveWhiteList();
    }

    // Named for what it actually does. Player .dat files live in <world>/players/ and are
    // dimension-independent, so dimension 0's storage is the storage for everyone — this never
    // had anything to do with the per-dimension chunk managers.
    public void setPlayerNbtStorage(WorldServer[] ws) {
        this.playerNBTManagerObj = ws[0].getPlayerStorage();
    }

    // Drops the player from every dimension's chunk manager, then re-registers them with the
    // one for the dimension they're actually in. Indexed [0] and [1] explicitly before, which
    // ran off the end of a length-1 array; removePlayer is a no-op for a manager the player
    // isn't registered with, so sweeping all of them is safe and survives the array growing.
    public void removePlayersFromList(EntityPlayerMP entityPlayerMP1) {
        for (ServerChunkDistance manager : this.playerManagerObj) {
            manager.removePlayer(entityPlayerMP1);
        }

        this.getPlayerManager(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);
        WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
        worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);
    }

    public int getMaxTrackingDistance() {
        return this.playerManagerObj[0].getMaxTrackingDistance();
    }

    // Was `i1 == -1 ? playerManagerObj[1] : playerManagerObj[0]` — -1 is vanilla's nether id,
    // but this mod numbers Hell as 1, so the branch was both wrong and an out-of-bounds read
    // on a length-1 array. Clamping keeps a corrupt dimension id from killing the server
    // thread; it degrades to the Overworld instead.
    private ServerChunkDistance getPlayerManager(int dimension) {
        return this.playerManagerObj[dimension >= 0 && dimension < this.playerManagerObj.length ? dimension : 0];
    }

    public void readPlayerDataFromFile(EntityPlayerMP entityPlayerMP1) {
        this.playerNBTManagerObj.readPlayerData(entityPlayerMP1);
    }

    public void playerLoggedIn(EntityPlayerMP entityPlayerMP1) {
        entityPlayerMP1.op = this.isOp(entityPlayerMP1.username);
        this.sendPacketToAll(new PlayerInfoPacket(entityPlayerMP1.username, true, 1000));
        this.playerEntities.add(entityPlayerMP1);
        WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
        worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);

        while (worldServer2.getBoundingBoxes(entityPlayerMP1, entityPlayerMP1.boundingBox).size() != 0) {
            entityPlayerMP1.setPosition(entityPlayerMP1.posX, entityPlayerMP1.posY + 1.0, entityPlayerMP1.posZ);
        }

        worldServer2.spawnEntity(entityPlayerMP1);
        this.getPlayerManager(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            entityPlayerMP1.playerNetServerHandler.sendPacket(new PlayerInfoPacket(entityplayermp.username, true, entityplayermp.ping));
        }
    }

    public void setPositionUponLogin(EntityPlayerMP entityPlayerMP1) {
        this.getPlayerManager(entityPlayerMP1.dimension).setPlayerPosition(entityPlayerMP1);
    }

    public void playerLoggedOut(EntityPlayerMP entityPlayerMP1) {
        this.playerNBTManagerObj.writePlayerData(entityPlayerMP1);
        this.mcServer.getWorldManager(entityPlayerMP1.dimension).kill(entityPlayerMP1);
        this.playerEntities.remove(entityPlayerMP1);
        this.getPlayerManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
        this.sendPacketToAll(new PlayerInfoPacket(entityPlayerMP1.username, false, 9999));
    }

    public EntityPlayerMP login(NetLoginHandler netLoginHandler1, String string2) {
        if (this.bannedPlayers.isBanned(string2)) {
            BanEntry var6 = this.bannedPlayers.getBannedList().get(string2);
            String var7 = "You are banned from this server!\nReason: " + var6.getBanReason();
            if (var6.getBanEndDate() != null) {
                var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
            }

            netLoginHandler1.kickUser(var7);
            return null;
        } else {
            if (!this.allowLogin(string2)) {
                netLoginHandler1.kickUser("You are not white-listed on this server!");
                return null;
            }

            String string3 = netLoginHandler1.netManager.getRemoteAddress().toString();
            string3 = string3.substring(string3.indexOf("/") + 1);
            string3 = string3.substring(0, string3.indexOf(":"));
            if (this.bannedIPs.isBanned(string3)) {
                BanEntry var4 = this.bannedIPs.getBannedList().get(string3);
                String var5 = "Your IP address is banned from this server!\nReason: " + var4.getBanReason();
                if (var4.getBanEndDate() != null) {
                    var5 = var5 + "\nYour ban will be removed on " + dateFormat.format(var4.getBanEndDate());
                }

                netLoginHandler1.kickUser(var5);
                return null;
            } else {
                if (this.playerEntities.size() >= this.maxPlayers) {
                    netLoginHandler1.kickUser("The server is full!");
                    return null;
                }

                for (int i4 = 0; i4 < this.playerEntities.size(); i4++) {
                    EntityPlayerMP entityPlayerMP5 = this.playerEntities.get(i4);
                    if (entityPlayerMP5.username.equalsIgnoreCase(string2)) {
                        entityPlayerMP5.playerNetServerHandler.kickPlayer("You logged in from another location");
                    }
                }

                // Dimension 0 is correct here, not a leftover: the player's real dimension lives
                // in their NBT, which can't be read until the entity exists. NetLoginHandler
                // reads it immediately after and calls setWorld(), which rebuilds the
                // ItemInWorldManager against the right world.
                return new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(0), string2, new ItemInWorldManager(this.mcServer.getWorldManager(0)));
            }
        }
    }

    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP entityPlayerMP1, int i2) {
        this.mcServer.getEntityTracker(entityPlayerMP1.dimension).removeTrackedPlayerSymmetric(entityPlayerMP1);
        this.mcServer.getEntityTracker(entityPlayerMP1.dimension).untrackEntity(entityPlayerMP1);
        this.getPlayerManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
        this.playerEntities.remove(entityPlayerMP1);
        this.mcServer.getWorldManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
        Position chunkCoordinates3 = entityPlayerMP1.getSpawn(entityPlayerMP1.dimension);
        entityPlayerMP1.dimension = i2;
        EntityPlayerMP entityPlayerMP4 = new EntityPlayerMP(
            this.mcServer,
            this.mcServer.getWorldManager(entityPlayerMP1.dimension),
            entityPlayerMP1.username,
            new ItemInWorldManager(this.mcServer.getWorldManager(entityPlayerMP1.dimension))
        );
        entityPlayerMP4.entityId = entityPlayerMP1.entityId;
        entityPlayerMP4.playerNetServerHandler = entityPlayerMP1.playerNetServerHandler;
        WorldServer worldServer5 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
        if (chunkCoordinates3 != null) {
            Position chunkCoordinates6 = Player.wakeUpPlayer(
                this.mcServer.getWorldManager(entityPlayerMP1.dimension), chunkCoordinates3, entityPlayerMP1.dimension
            );
            if (chunkCoordinates6 != null) {
                entityPlayerMP4.setPosAndRot(chunkCoordinates6.x + 0.5F, chunkCoordinates6.y + 0.1F, chunkCoordinates6.z + 0.5F, 0.0F, 0.0F);
                entityPlayerMP4.setSpawn(chunkCoordinates3, entityPlayerMP4.dimension, entityPlayerMP4.forceSpawn);
            } else {
                entityPlayerMP4.playerNetServerHandler.sendPacket(new WorldEventPacket(0));
            }
        }

        worldServer5.chunkProviderServer.loadChunk((int)entityPlayerMP4.posX >> 4, (int)entityPlayerMP4.posZ >> 4);

        while (worldServer5.getBoundingBoxes(entityPlayerMP4, entityPlayerMP4.boundingBox).size() != 0) {
            entityPlayerMP4.setPosition(entityPlayerMP4.posX, entityPlayerMP4.posY + 1.0, entityPlayerMP4.posZ);
        }

        entityPlayerMP4.playerNetServerHandler.sendPacket(new RespawnPacket((byte)entityPlayerMP4.dimension));
        entityPlayerMP4.playerNetServerHandler
            .teleportTo(entityPlayerMP4.posX, entityPlayerMP4.posY, entityPlayerMP4.posZ, entityPlayerMP4.yaw, entityPlayerMP4.pitch);
        this.updateWorldTimeForPlayers(entityPlayerMP4, worldServer5);
        this.getPlayerManager(entityPlayerMP4.dimension).addPlayer(entityPlayerMP4);
        worldServer5.spawnEntity(entityPlayerMP4);
        this.playerEntities.add(entityPlayerMP4);
        entityPlayerMP4.onGuiOpened();
        entityPlayerMP4.respawn();
        this.sendPacketToAll(new OpPacket(entityPlayerMP4.entityId, this.isOp(entityPlayerMP4.username)));
        return entityPlayerMP4;
    }

    public void onTick() {
        if (++this.playerUseTime > 200) {
            this.playerUseTime = 0;
        }

        if (this.playerUseTime < this.playerEntities.size()) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(this.playerUseTime);
            this.sendPacketToAll(new PlayerInfoPacket(entityplayermp.username, true, entityplayermp.ping));
        }

        for (int i1 = 0; i1 < this.playerManagerObj.length; i1++) {
            this.playerManagerObj[i1].tick();
        }
    }

    public void markBlockNeedsUpdate(int i1, int i2, int i3, int i4) {
        this.getPlayerManager(i4).markBlockNeedsUpdate(i1, i2, i3);
    }

    @Override
    public void sendPacketToAll(Packet packet1) {
        for (int i2 = 0; i2 < this.playerEntities.size(); i2++) {
            EntityPlayerMP entityPlayerMP3 = this.playerEntities.get(i2);
            entityPlayerMP3.playerNetServerHandler.sendPacket(packet1);
        }
    }

    @Override
    public void sendPacketToAllDimension(Packet packet1, int i2) {
        for (int i3 = 0; i3 < this.playerEntities.size(); i3++) {
            EntityPlayerMP entityPlayerMP4 = this.playerEntities.get(i3);
            if (entityPlayerMP4.dimension == i2) {
                entityPlayerMP4.playerNetServerHandler.sendPacket(packet1);
            }
        }
    }

    @Override
    public void kickPlayer(Player player, String reason) {
        EntityPlayerMP serverplayer = (EntityPlayerMP)player;
        serverplayer.playerNetServerHandler.kickPlayer(reason);
    }

    @Override
    public void teleportPlayerTo(Player player, double x, double y, double z, float xr, float yr) {
        EntityPlayerMP serverplayer = (EntityPlayerMP)player;
        serverplayer.playerNetServerHandler.teleportTo(x, y, z, xr, yr);
    }

    @Override
    public String getPlayerList() {
        String string1 = "";

        for (int i2 = 0; i2 < this.playerEntities.size(); i2++) {
            if (i2 > 0) {
                string1 = string1 + ", ";
            }

            string1 = string1 + this.playerEntities.get(i2).username;
        }

        return string1;
    }

    @Override
    public void banPlayer(CommandSource src, String[] string1) {
        BanEntry var4 = new BanEntry(string1[0]);
        var4.setBannedBy(src.getUsername());
        if (string1.length >= 2) {
            var4.setBanReason(Command.joinArgArray(string1, 1));
        }

        this.bannedPlayers.put(var4);
    }

    @Override
    public void pardonPlayer(String string1) {
        this.bannedPlayers.remove(string1);
    }

    @Override
    public void banIP(CommandSource src, String[] string1) {
        BanEntry var4 = new BanEntry(string1[0]);
        var4.setBannedBy(src.getUsername());
        if (string1.length >= 2) {
            var4.setBanReason(Command.joinArgArray(string1, 1));
        }

        this.bannedIPs.put(var4);

        for (EntityPlayerMP player : this.getPlayersWithIP(string1[0])) {
            player.playerNetServerHandler.kickPlayer("You have been IP banned.");
        }
    }

    @Override
    public void pardonIP(String string1) {
        this.bannedIPs.remove(string1);
    }

    @Override
    public void opPlayer(String string1) {
        this.ops.add(string1.toLowerCase());
        this.saveOps();
        Player player = this.getPlayer(string1);
        if (player != null) {
            player.op = true;
            this.sendPacketToAll(new OpPacket(player.entityId, player.op));
        }
    }

    @Override
    public void deopPlayer(String string1) {
        this.ops.remove(string1.toLowerCase());
        this.saveOps();
        Player player = this.getPlayer(string1);
        if (player != null) {
            player.op = false;
            this.sendPacketToAll(new OpPacket(player.entityId, player.op));
        }
    }

    private void loadOps() {
        try {
            this.ops.clear();
            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.opFile));
            String string2 = "";

            while ((string2 = bufferedReader1.readLine()) != null) {
                this.ops.add(string2.trim().toLowerCase());
            }

            bufferedReader1.close();
        } catch (Exception exception3) {
            logger.warning("Failed to load ip ban list: " + exception3);
        }
    }

    private void saveOps() {
        try {
            PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.opFile, false));

            for (String string3 : this.ops) {
                printWriter1.println(string3);
            }

            printWriter1.close();
        } catch (Exception exception4) {
            logger.warning("Failed to save ip ban list: " + exception4);
        }
    }

    private void loadWhiteList() {
        try {
            this.whiteListedIPs.clear();
            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.whitelistPlayersFile));
            String string2 = "";

            while ((string2 = bufferedReader1.readLine()) != null) {
                this.whiteListedIPs.add(string2.trim().toLowerCase());
            }

            bufferedReader1.close();
        } catch (Exception exception3) {
            logger.warning("Failed to load white-list: " + exception3);
        }
    }

    private void saveWhiteList() {
        try {
            PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.whitelistPlayersFile, false));

            for (String string3 : this.whiteListedIPs) {
                printWriter1.println(string3);
            }

            printWriter1.close();
        } catch (Exception exception4) {
            logger.warning("Failed to save white-list: " + exception4);
        }
    }

    @Override
    public boolean allowLogin(String string1) {
        string1 = string1.trim().toLowerCase();
        return !this.whiteListEnforced || this.ops.contains(string1) || this.whiteListedIPs.contains(string1);
    }

    @Override
    public boolean isOp(String string1) {
        return this.ops.contains(string1.trim().toLowerCase());
    }

    public EntityPlayerMP getPlayer(String string1) {
        for (int i2 = 0; i2 < this.playerEntities.size(); i2++) {
            EntityPlayerMP entityPlayerMP3 = this.playerEntities.get(i2);
            if (entityPlayerMP3.username.equalsIgnoreCase(string1)) {
                return entityPlayerMP3;
            }
        }

        return null;
    }

    @Override
    public void whisper(String string1, String string2) {
        EntityPlayerMP entityPlayerMP3 = this.getPlayer(string1);
        if (entityPlayerMP3 != null) {
            entityPlayerMP3.playerNetServerHandler.sendPacket(new ChatPacket(string2));
        }
    }

    @Override
    public void sendPacketToAllCoord(double x, double y, double z, double rang, int dim, Packet packet) {
        this.sendPacketToPlayerCoord(null, x, y, z, rang, dim, packet);
    }

    @Override
    public void sendMessageToOps(String string1) {
        ChatPacket packet3Chat2 = new ChatPacket(string1);

        for (int i3 = 0; i3 < this.playerEntities.size(); i3++) {
            EntityPlayerMP entityPlayerMP4 = this.playerEntities.get(i3);
            if (this.isOp(entityPlayerMP4.username)) {
                entityPlayerMP4.playerNetServerHandler.sendPacket(packet3Chat2);
            }
        }
    }

    @Override
    public boolean sendPacketToPlayer(String string1, Packet packet2) {
        EntityPlayerMP entityPlayerMP3 = this.getPlayer(string1);
        if (entityPlayerMP3 != null) {
            entityPlayerMP3.playerNetServerHandler.sendPacket(packet2);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void savePlayerStates() {
        for (int i1 = 0; i1 < this.playerEntities.size(); i1++) {
            this.playerNBTManagerObj.writePlayerData(this.playerEntities.get(i1));
        }
    }

    public void sentTileEntityToPlayer(int i1, int i2, int i3, TileEntity tileEntity4) {
    }

    @Override
    public void whiteList(String string1) {
        this.whiteListedIPs.add(string1);
        this.saveWhiteList();
    }

    @Override
    public void unWhiteList(String string1) {
        this.whiteListedIPs.remove(string1);
        this.saveWhiteList();
    }

    @Override
    public Set<String> getWhiteListedIPs() {
        return this.whiteListedIPs;
    }

    @Override
    public void reloadWhiteList() {
        this.loadWhiteList();
    }

    public void updateWorldTimeForPlayers(EntityPlayerMP entityPlayerMP1, WorldServer worldServer2) {
        entityPlayerMP1.playerNetServerHandler.sendPacket(new UpdateTimePacket(worldServer2.getWorldTime()));
    }

    public void updateCrafting(EntityPlayerMP entityPlayerMP1) {
        entityPlayerMP1.updateCrafting(entityPlayerMP1.container);
        entityPlayerMP1.setHealth();
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void sendPacketToAllExcluding(Packet packet, Player player) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityPlayerMP = this.playerEntities.get(i);
            if (entityPlayerMP != player) {
                entityPlayerMP.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacketToPlayerCoord(Player player, double x, double y, double z, double range, int dimension, Packet packet) {
        for (int i12 = 0; i12 < this.playerEntities.size(); i12++) {
            EntityPlayerMP player2 = this.playerEntities.get(i12);
            if (player2 != player && player2.dimension == dimension) {
                double d14 = x - player2.posX;
                double d16 = y - player2.posY;
                double d18 = z - player2.posZ;
                if (d14 * d14 + d16 * d16 + d18 * d18 < range * range) {
                    player2.playerNetServerHandler.sendPacket(packet);
                }
            }
        }
    }

    public BanList getBannedPlayers() {
        return this.bannedPlayers;
    }

    public List<EntityPlayerMP> getPlayersWithIP(String ip) {
        ArrayList<EntityPlayerMP> var2 = new ArrayList<>();

        for (EntityPlayerMP var4 : this.playerEntities) {
            if (var4.getPlayerIP().equals(ip)) {
                var2.add(var4);
            }
        }

        return var2;
    }
}
