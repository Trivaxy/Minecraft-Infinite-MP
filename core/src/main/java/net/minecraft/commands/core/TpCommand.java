package net.minecraft.commands.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.Vec3Argument;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;

public final class TpCommand {
    private TpCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("tp")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("position", Vec3Argument.vec3())
                        .executes(ctx -> tp(ctx.getSource(), Vec3Argument.getVec3(ctx, "position")))
                )
        );
    }

    private static int tp(CommandSourceStack source, Vector position) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();
        if (source.getConfigManager() == null) {
            player.teleportTo(position.xCoord, position.yCoord, position.zCoord, source.getYaw(), source.getPitch());
        } else {
            source.getConfigManager()
                .teleportPlayerTo(player, position.xCoord, position.yCoord, position.zCoord, source.getYaw(), source.getPitch());
        }

        source.sendSuccess(
            String.format(CommandMessages.translate("commands.tp.success"), format(position.xCoord), format(position.yCoord), format(position.zCoord))
        );
        return 1;
    }

    private static String format(double coordinate) {
        return String.valueOf(Math.round(coordinate * 100.0) / 100.0);
    }
}
