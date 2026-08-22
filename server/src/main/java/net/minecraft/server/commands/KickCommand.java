package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.entity.player.Player;

public final class KickCommand {
    private KickCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("kick")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("player", PlayerArgument.player())
                        .executes(ctx -> kick(ctx.getSource(), PlayerArgument.getPlayer(ctx, "player")))
                )
        );
    }

    private static int kick(CommandSourceStack source, Player player) {
        source.sendSuccess(String.format(CommandMessages.translate("commands.kick.success"), player.username));
        source.getConfigManager().kickPlayer(player, CommandMessages.translate("commands.kick.reason"));
        return 1;
    }
}
