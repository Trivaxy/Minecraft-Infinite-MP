package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.core.GiveCommand;
import net.minecraft.commands.core.HelpCommand;

public final class CoreCommands {
    private CoreCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        HelpCommand.register(dispatcher);
        GiveCommand.register(dispatcher);
    }
}
