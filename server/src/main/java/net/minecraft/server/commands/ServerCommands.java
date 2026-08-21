package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.server.MinecraftServer;

public final class ServerCommands {
    private ServerCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, MinecraftServer server) {
        StopCommand.register(dispatcher, server);
        KickCommand.register(dispatcher);
        BanCommand.register(dispatcher);
        UnbanCommand.register(dispatcher);
        OpCommand.register(dispatcher);
        DeopCommand.register(dispatcher);
    }
}
