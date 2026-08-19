package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.core.GiveCommand;

public final class CoreCommands {
    private CoreCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        GiveCommand.register(dispatcher);
    }
}
