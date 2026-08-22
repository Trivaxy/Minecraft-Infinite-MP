package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public final class StopCommand {
    private StopCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, MinecraftServer server) {
        dispatcher.register(
            Commands.literal("stop")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    source.sendSuccess(CommandMessages.translate("commands.stop.stopping"));
                    server.initiateShutdown();
                    return 1;
                })
        );
    }
}
