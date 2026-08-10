package net.minecraft.server.commands;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.player.Player;

public class CommandFS extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource cs) {
        ConfigManager c = ca.getConfigManager();
        Player player = c.getPlayer(cs.getUsername());
        if (player != null) {
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            float xrot = player.yaw;
            float yrot = player.pitch;
            c.teleportPlayerTo(player, x, y, z, xrot, yrot);
            cs.print("Fixed position sync.");
        } else {
            cs.print("Player not found.");
        }
    }

    @Override
    public String name() {
        return "fixsync";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("fs");
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "teleports you to your last synced position";
    }
}
