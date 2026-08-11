package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandsManager;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.util.property.PropertySource;

public class CommandBlockEntity extends TileEntity implements CommandAccessor {
    public String command = "";
    public int lineBeingEdited = -1;
    private boolean editable = true;
    public boolean suppressOutput = false;

    @Override
    public void writeToNBT(CompoundTag c) {
        super.writeToNBT(c);
        c.setString("Command", this.command);
        c.setBoolean("SuppressOutput", this.suppressOutput);
    }

    @Override
    public void readFromNBT(CompoundTag c) {
        super.readFromNBT(c);
        this.command = c.getString("Command");
        this.suppressOutput = c.getBoolean("SuppressOutput");
    }

    public void setEditable(boolean e) {
        this.editable = e;
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public Block getBlockType() {
        return BlockList.blocks[this.world.getBlockId(this.x, this.y, this.z)];
    }

    @Override
    public int getWorldCount() {
        return 1;
    }

    @Override
    public World getWorld(int dimension) {
        return this.world;
    }

    @Override
    public ConfigManager getConfigManager() {
        return new CommandBlockEntity.CoreConfigManager(this.world.getClosestPlayer(this.x, this.y, this.z, 16.0), !this.suppressOutput);
    }

    @Override
    public PropertySource getProperties() {
        return new PropertySource.Empty();
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public void initiateShutdown() {
    }

    public static class CommandsManagerCore extends CommandsManager {
        public static CommandBlockEntity.CommandsManagerCore instance;

        public static void init() {
            instance = new CommandBlockEntity.CommandsManagerCore();
        }

        protected CommandsManagerCore() {
        }
    }

    public class CoreConfigManager implements ConfigManager {
        Player thePlayer;
        boolean enableoutput;

        public CoreConfigManager(Player player, boolean b) {
            this.thePlayer = player;
            this.enableoutput = b;
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
            return true;
        }

        @Override
        public Player getPlayer(String name) {
            return this.thePlayer;
        }

        @Override
        public void sendPacketToAllExcluding(Packet packet, Player player) {
            if (packet instanceof ChatPacket) {
                ChatPacket pkt = (ChatPacket)packet;
                player.addChatMessage(pkt.message);
            }
        }

        @Override
        public void sendPacketToAllDimension(Packet packet, int dimension) {
        }

        @Override
        public void sendPacketToAll(Packet packet) {
        }

        @Override
        public void whisper(String name, String message) {
            this.thePlayer.addChatMessage(message);
        }

        @Override
        public void sendPacketToAllCoord(double x, double y, double z, double range, int dimension, Packet packet) {
        }

        @Override
        public void sendPacketToPlayerCoord(Player player, double x, double y, double z, double range, int dimension, Packet packet) {
        }

        @Override
        public void sendMessageToOps(String message) {
            if (this.enableoutput) {
                this.thePlayer.addChatMessage(message);
            }
        }

        @Override
        public boolean sendPacketToPlayer(String name, Packet packet) {
            return false;
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
            return "";
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
}
