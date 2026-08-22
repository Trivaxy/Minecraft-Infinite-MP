package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.core.GamemodeCommand;
import net.minecraft.commands.core.GiveCommand;
import net.minecraft.commands.core.HelpCommand;
import net.minecraft.commands.core.ScoreCommand;
import net.minecraft.commands.core.TpCommand;

public final class CoreCommands {
    private CoreCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        HelpCommand.register(dispatcher);
        GiveCommand.register(dispatcher);
        GamemodeCommand.register(dispatcher);
        ScoreCommand.register(dispatcher);
        TpCommand.register(dispatcher);
    }
}
