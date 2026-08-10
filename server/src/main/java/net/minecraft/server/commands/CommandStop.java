package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandStop extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        sendNoticeToOps(ca, source, "Stopping the server...");
        ca.initiateShutdown();
    }

    @Override
    public String name() {
        return "stop";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public String help() {
        return "stops the server";
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }
}
