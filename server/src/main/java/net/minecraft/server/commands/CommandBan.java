package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.player.Player;

public class CommandBan extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        ConfigManager c = ca.getConfigManager();
        String target = args[0];
        c.banPlayer(src, args);
        sendNoticeToOps(ca, src, "Banning " + target);
        Player player = c.getPlayer(target);
        if (player != null) {
            c.kickPlayer(player, "Banned by admin");
        }
    }

    @Override
    public String name() {
        return "ban";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "<player> bans the specified player";
    }
}
