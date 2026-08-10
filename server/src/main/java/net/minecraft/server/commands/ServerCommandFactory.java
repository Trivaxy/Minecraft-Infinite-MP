package net.minecraft.server.commands;

import net.minecraft.commands.CommandsManager;

public class ServerCommandFactory extends CommandsManager {
    public static CommandsManager instance;

    public static void init() {
        instance = new ServerCommandFactory();
    }

    public ServerCommandFactory() {
        registerCommand(new CommandList());
        registerCommand(new CommandStop());
        registerCommand(new CommandSaveAll());
        registerCommand(new CommandSaveOff());
        registerCommand(new CommandSaveOn());
        registerCommand(new CommandOp());
        registerCommand(new CommandDeop());
        registerCommand(new CommandBanIP());
        registerCommand(new CommandPardonIP());
        registerCommand(new CommandBan());
        registerCommand(new CommandPardon());
        registerCommand(new CommandKick());
        registerCommand(new CommandSay());
        registerCommand(new CommandWhitelist());
        registerCommand(new CommandTell());
        registerCommand(new CommandFS());
        registerCommand(new UsedMemory());
        registerCommand(new CommandInvSee());
    }
}
