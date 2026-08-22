package net.minecraft.commands.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NonNegativeIntArgument;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.entity.player.Player;

public final class ScoreCommand {
    private ScoreCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("score")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.literal("add").then(
                        playerThenAmount(ctx -> add(
                            ctx.getSource(), PlayerArgument.getPlayer(ctx, "player"), NonNegativeIntArgument.getInteger(ctx, "amount")
                        ))
                    )
                )
                .then(
                    Commands.literal("remove").then(
                        playerThenAmount(ctx -> remove(
                            ctx.getSource(), PlayerArgument.getPlayer(ctx, "player"), NonNegativeIntArgument.getInteger(ctx, "amount")
                        ))
                    )
                )
                .then(
                    Commands.literal("set").then(
                        playerThenAmount(ctx -> set(
                            ctx.getSource(), PlayerArgument.getPlayer(ctx, "player"), NonNegativeIntArgument.getInteger(ctx, "amount")
                        ))
                    )
                )
        );
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> playerThenAmount(Command<CommandSourceStack> executor) {
        return Commands.argument("player", PlayerArgument.player())
            .then(Commands.argument("amount", NonNegativeIntArgument.nonnegativeInt()).executes(executor));
    }

    private static int add(CommandSourceStack source, Player player, int amount) {
        player.score += amount;
        source.sendSuccess(String.format(CommandMessages.translate("commands.score.add"), amount, player.username, player.score));
        return 1;
    }

    private static int remove(CommandSourceStack source, Player player, int amount) {
        player.score = Math.max(0, player.score - amount);
        source.sendSuccess(String.format(CommandMessages.translate("commands.score.remove"), amount, player.username, player.score));
        return 1;
    }

    private static int set(CommandSourceStack source, Player player, int amount) {
        player.score = amount;
        source.sendSuccess(String.format(CommandMessages.translate("commands.score.set"), player.username, amount));
        return 1;
    }
}
