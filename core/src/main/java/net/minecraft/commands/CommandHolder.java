package net.minecraft.commands;

public class CommandHolder {
    public final String command;
    public final CommandSource source;

    public CommandHolder(String command, CommandSource source) {
        this.command = command;
        this.source = source;
    }
}
