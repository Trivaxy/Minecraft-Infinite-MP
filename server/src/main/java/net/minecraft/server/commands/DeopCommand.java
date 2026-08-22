package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.player.ConfigManager;

public final class DeopCommand {
    private static final SimpleCommandExceptionType NOT_OP = new SimpleCommandExceptionType(CommandMessages.lazy("commands.deop.notop"));

    private DeopCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("deop")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("player", PlayerArgument.player())
                        .executes(ctx -> deop(ctx.getSource(), ctx.getArgument("player", String.class)))
                )
        );
    }

    private static int deop(CommandSourceStack source, String username) throws CommandSyntaxException {
        ConfigManager configManager = source.getConfigManager();
        if (!configManager.isOp(username)) {
            throw NOT_OP.create();
        }

        configManager.deopPlayer(username);
        source.sendSuccess(String.format(CommandMessages.translate("commands.deop.success"), username));
        return 1;
    }
}
