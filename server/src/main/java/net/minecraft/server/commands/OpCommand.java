package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.player.ConfigManager;

public final class OpCommand {
    private static final SimpleCommandExceptionType ALREADY_OP = new SimpleCommandExceptionType(CommandMessages.lazy("commands.op.already"));

    private OpCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("op")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("player", PlayerArgument.player())
                        .executes(ctx -> op(ctx.getSource(), ctx.getArgument("player", String.class)))
                )
        );
    }

    private static int op(CommandSourceStack source, String username) throws CommandSyntaxException {
        ConfigManager configManager = source.getConfigManager();
        if (configManager.isOp(username)) {
            throw ALREADY_OP.create();
        }

        configManager.opPlayer(username);
        source.sendSuccess(String.format(CommandMessages.translate("commands.op.success"), username));
        return 1;
    }
}
