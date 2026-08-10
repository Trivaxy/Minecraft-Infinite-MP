package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandPardonIP extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        String ip = args[0];
        ca.getConfigManager().pardonIP(ip);
        sendNoticeToOps(ca, src, "Pardoning ip " + ip);
    }

    @Override
    public String name() {
        return "pardon-ip";
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
        return "<ip> allows the specified IP to join again";
    }
}
