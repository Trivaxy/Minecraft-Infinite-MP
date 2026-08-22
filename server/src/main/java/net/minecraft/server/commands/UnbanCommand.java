package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.ServerConfigurationManager;

public final class UnbanCommand {
    private static final SimpleCommandExceptionType NOT_BANNED = new SimpleCommandExceptionType(CommandMessages.lazy("commands.unban.notbanned"));

    private UnbanCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("unban")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> unban(ctx.getSource(), StringArgumentType.getString(ctx, "player")))
                )
        );
    }

    private static int unban(CommandSourceStack source, String username) throws CommandSyntaxException {
        ServerConfigurationManager configManager = (ServerConfigurationManager)source.getConfigManager();
        if (!configManager.getBannedPlayers().isBanned(username)) {
            throw NOT_BANNED.create();
        }

        configManager.pardonPlayer(username);
        source.sendSuccess(String.format(CommandMessages.translate("commands.unban.success"), username));
        return 1;
    }
}
