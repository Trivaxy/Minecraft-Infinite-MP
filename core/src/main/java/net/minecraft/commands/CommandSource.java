package net.minecraft.commands;

public interface CommandSource {
    String getUsername();

    void print(String var1);

    Command.UserType getType();

    boolean canRunCommand(Command var1);

    default void printf(String text, Object... args) {
        this.print(String.format(text, args));
    }
}
