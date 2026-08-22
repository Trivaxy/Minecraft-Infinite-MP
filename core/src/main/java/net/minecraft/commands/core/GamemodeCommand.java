package net.minecraft.commands.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GamemodeArgument;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.entity.player.Player;

public final class GamemodeCommand {
    private GamemodeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("gamemode")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("mode", GamemodeArgument.gamemode())
                        .executes(ctx -> gamemode(ctx.getSource(), ctx.getSource().getPlayerOrException(), GamemodeArgument.getGamemode(ctx, "mode"), true))
                        .then(
                            Commands.argument("player", PlayerArgument.player())
                                .executes(
                                    ctx -> gamemode(
                                        ctx.getSource(), PlayerArgument.getPlayer(ctx, "player"), GamemodeArgument.getGamemode(ctx, "mode"), false
                                    )
                                )
                        )
                )
        );
    }

    private static int gamemode(CommandSourceStack source, Player player, int mode, boolean self) throws CommandSyntaxException {
        source.getConfigManager().setGamemode(player, mode);
        String name = GamemodeArgument.nameOf(mode);
        if (self) {
            source.sendSuccess(String.format(CommandMessages.translate("commands.gamemode.success"), name));
        } else {
            source.sendSuccess(String.format(CommandMessages.translate("commands.gamemode.other"), player.username, name));
        }

        return 1;
    }
}
