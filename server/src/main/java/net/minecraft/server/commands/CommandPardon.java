package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;

public class CommandPardon extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        ConfigManager c = ca.getConfigManager();
        String target = args[0];
        c.pardonPlayer(target);
        sendNoticeToOps(ca, src, "Pardoning " + target);
    }

    @Override
    public String name() {
        return "pardon";
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
        return "<player> allows the player to join the server again";
    }
}
