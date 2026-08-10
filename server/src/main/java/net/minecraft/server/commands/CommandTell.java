package net.minecraft.server.commands;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.packet.ChatPacket;

public class CommandTell extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        demandArgCountOf(2, args);
        String sharts = joinArgArray(args, 1);
        String s = sharts;
        logger.info("[" + source.getUsername() + "->" + args[0] + "] " + s);
        s = "§7" + source.getUsername() + " whispers to you" + s;
        if (!ca.getConfigManager().sendPacketToPlayer(args[0], new ChatPacket(s))) {
            source.print("There's no player by that name online.");
        } else {
            source.print("You whisper to " + args[0] + ": " + sharts);
        }
    }

    @Override
    public String name() {
        return "tell";
    }

    @Override
    public String help() {
        return "<player> <message> sends a private message to a player";
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
    public List<String> getAliases() {
        return Arrays.asList("w", "whisper", "msg");
    }
}
