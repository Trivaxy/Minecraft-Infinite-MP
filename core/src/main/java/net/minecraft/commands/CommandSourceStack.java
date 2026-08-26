package net.minecraft.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.Set;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.player.ConfigManager;
import net.minecraft.game.world.World;

public class CommandSourceStack {
    public static final int LEVEL_ALL = 0;
    public static final int LEVEL_OP = 2;
    public static final int LEVEL_OWNER = 4;

    private final CommandOutput output;
    private final String name;
    private final int permissionLevel;
    private final Player player;
    private final World world;
    private final Vector position;
    private final float yaw;
    private final float pitch;
    private final ConfigManager configManager;
    private final boolean server;

    public CommandSourceStack(
        CommandOutput output,
        String name,
        int permissionLevel,
        Player player,
        World world,
        Vector position,
        float yaw,
        float pitch,
        ConfigManager configManager,
        boolean server
    ) {
        this.output = output;
        this.name = name;
        this.permissionLevel = permissionLevel;
        this.player = player;
        this.world = world;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.configManager = configManager;
        this.server = server;
    }

    public static CommandSourceStack of(CommandOutput output, Player player, int permissionLevel, ConfigManager configManager, boolean server) {
        return new CommandSourceStack(
            output,
            player.username,
            permissionLevel,
            player,
            player.world,
            Vector.createVector(player.posX, player.posY, player.posZ),
            player.yaw,
            player.pitch,
            configManager,
            server
        );
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPermission(int level) {
        return this.permissionLevel >= level;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean isServer() {
        return this.server;
    }

    public World getWorld() {
        return this.world;
    }

    public Vector getPosition() {
        return this.position;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Player getPlayerOrException() throws CommandSyntaxException {
        if (this.player == null) {
            throw CommandMessages.NOT_A_PLAYER.create();
        }

        return this.player;
    }

    public Set<String> getPlayerNames() {
        return this.configManager == null ? Collections.<String>emptySet() : this.configManager.getPlayerNames();
    }

    public void sendSuccess(String message) {
        this.output.sendCommandMessage(message);
    }

    public void sendFailure(String message) {
        this.output.sendCommandMessage("§c" + message);
    }

    public void broadcastToOps(String message) {
        if (this.configManager != null) {
            this.configManager.sendMessageToOps(this.name + ": " + message);
        }
    }

    public CommandSourceStack withPosition(Vector newPosition) {
        return new CommandSourceStack(
            this.output, this.name, this.permissionLevel, this.player, this.world, newPosition, this.yaw, this.pitch, this.configManager, this.server
        );
    }

    public CommandSourceStack withPlayer(Player newPlayer) {
        return new CommandSourceStack(
            this.output, this.name, this.permissionLevel, newPlayer, this.world, this.position, this.yaw, this.pitch, this.configManager, this.server
        );
    }

    public CommandSourceStack withPermission(int level) {
        return new CommandSourceStack(
            this.output, this.name, level, this.player, this.world, this.position, this.yaw, this.pitch, this.configManager, this.server
        );
    }
}
