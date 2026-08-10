package net.minecraft.client.commands;

import net.minecraft.client.commands.client.ClearChatCommand;
import net.minecraft.client.commands.client.GamemodeCommand;
import net.minecraft.client.commands.client.ImagineCommand;
import net.minecraft.client.commands.client.TimerCommand;
import net.minecraft.commands.CommandsManager;

public class ClientCommands extends CommandsManager {
    public static ClientCommands instance;

    public static void init() {
        instance = new ClientCommands();
    }

    protected ClientCommands() {
        registerCommand(new GamemodeCommand());
        registerCommand(new ImagineCommand());
        registerCommand(new TimerCommand());
        registerCommand(new ClearChatCommand());
    }
}
