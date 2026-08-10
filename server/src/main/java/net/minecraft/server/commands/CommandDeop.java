package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;

public class CommandDeop extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        ConfigManager cm = ca.getConfigManager();
        cm.deopPlayer(args[0]);
        sendNoticeToOps(ca, src, "De-opping " + args[0]);
        cm.whisper(args[0], "§eYou are no longer op!");
    }

    @Override
    public String name() {
        return "deop";
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
        return "<player> makes the specified player no longer a server operator";
    }
}
