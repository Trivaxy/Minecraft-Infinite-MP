package net.minecraft.server.commands;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandBanIP extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        String ip = args[0];
        ca.getConfigManager().banIP(src, args);
        sendNoticeToOps(ca, src, "Banning ip " + ip);
    }

    @Override
    public String name() {
        return "ban-ip";
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
        return "<ip> bans the specified IP";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("banip");
    }
}
