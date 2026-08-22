package net.minecraft.commands.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class HelpCommand {
    private HelpCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("help")
                .executes(ctx -> list(dispatcher, ctx.getSource()))
                .then(
                    Commands.argument("command", StringArgumentType.word())
                        .executes(ctx -> detail(dispatcher, ctx.getSource(), StringArgumentType.getString(ctx, "command")))
                )
        );
    }

    private static int list(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source) {
        List<CommandNode<CommandSourceStack>> visible = new ArrayList<CommandNode<CommandSourceStack>>();
        for (CommandNode<CommandSourceStack> node : dispatcher.getRoot().getChildren()) {
            if (node.canUse(source)) {
                visible.add(node);
            }
        }

        visible.sort(Comparator.comparing(CommandNode::getName));
        source.sendSuccess(CommandMessages.translate("commands.help.header"));

        for (CommandNode<CommandSourceStack> node : visible) {
            source.sendSuccess("§e/" + node.getName());
        }

        return visible.size();
    }

    private static int detail(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source, String name) throws CommandSyntaxException {
        CommandNode<CommandSourceStack> node = dispatcher.getRoot().getChild(name);
        if (node == null || !node.canUse(source)) {
            throw CommandMessages.UNKNOWN_COMMAND.create();
        }

        String[] usages = dispatcher.getAllUsage(node, source, true);
        for (String usage : usages) {
            source.sendSuccess("§e/" + (usage.isEmpty() ? name : name + " " + usage));
        }

        return usages.length;
    }
}
