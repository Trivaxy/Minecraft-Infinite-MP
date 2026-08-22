package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.player.ConfigManager;

public final class BanCommand {
    private BanCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ban")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("player", PlayerArgument.player())
                        .executes(ctx -> ban(ctx.getSource(), ctx.getArgument("player", String.class), null))
                        .then(
                            Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(
                                    ctx -> ban(ctx.getSource(), ctx.getArgument("player", String.class), StringArgumentType.getString(ctx, "reason"))
                                )
                        )
                )
        );
    }

    private static int ban(CommandSourceStack source, String username, String reason) {
        ConfigManager configManager = source.getConfigManager();
        configManager.banPlayer(source.getName(), username, reason);
        source.sendSuccess(String.format(CommandMessages.translate("commands.ban.success"), username));

        Player player = configManager.getPlayer(username);
        if (player != null) {
            configManager.kickPlayer(player, kickMessage(reason));
        }

        return 1;
    }

    private static String kickMessage(String reason) {
        String banned = CommandMessages.translate("commands.ban.banned");
        return reason == null ? banned : banned + "\n" + String.format(CommandMessages.translate("commands.ban.reason"), reason);
    }
}
