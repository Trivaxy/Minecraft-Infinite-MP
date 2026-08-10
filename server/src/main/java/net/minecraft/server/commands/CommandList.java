package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandList extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        source.print("Connected players: " + ca.getConfigManager().getPlayerList());
    }

    @Override
    public String name() {
        return "list";
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
        return "lists players online";
    }
}
