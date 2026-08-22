package net.minecraft.commands.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ItemArgument;
import net.minecraft.commands.arguments.PlayerArgument;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;

public final class GiveCommand {
    private static final SimpleCommandExceptionType MISSING_ITEM = new SimpleCommandExceptionType(CommandMessages.lazy("commands.give.usage"));

    private GiveCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("give")
                .requires(source -> source.hasPermission(CommandSourceStack.LEVEL_OP))
                .then(
                    Commands.argument("item", ItemArgument.item())
                        .executes(ctx -> give(ctx.getSource(), ctx.getSource().getPlayerOrException(), ItemArgument.getItem(ctx, "item")))
                )
                .then(
                    Commands.argument("player", PlayerArgument.player())
                        .executes(ctx -> {
                            throw missingItem(ctx.getArgument("player", String.class));
                        })
                        .then(
                            Commands.argument("item", ItemArgument.item())
                                .executes(ctx -> give(ctx.getSource(), PlayerArgument.getPlayer(ctx, "player"), ItemArgument.getItem(ctx, "item")))
                        )
                )
        );
    }

    private static CommandSyntaxException missingItem(String token) {
        try {
            return CommandMessages.UNKNOWN_ITEM.create(Integer.parseInt(token));
        } catch (NumberFormatException e) {
            return MISSING_ITEM.create();
        }
    }

    private static int give(CommandSourceStack source, Player player, Item item) {
        int count = item.getStackLimit();
        ItemStack stack = new ItemStack(item, count);
        if (!player.inventory.addItemToInventory(stack)) {
            player.dropPlayerItem(stack);
        }

        source.sendSuccess(String.format(CommandMessages.translate("commands.give.success"), count, item.id, player.username));
        return count;
    }
}
