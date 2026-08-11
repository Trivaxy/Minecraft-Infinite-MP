package net.minecraft.server.commands;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.packet.misc.ChatPacket;

public class CommandSay extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        demandArgCountOf(1, args);
        String msg = joinArgArray(args, 0);
        ca.getConfigManager().sendPacketToAll(new ChatPacket("§d[Server] " + msg));
    }

    @Override
    public String name() {
        return "say";
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
        return "<message> broadcasts a message to all players";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("broadcast");
    }
}
