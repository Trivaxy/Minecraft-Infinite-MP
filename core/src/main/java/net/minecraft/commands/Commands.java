package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Commands {
    private static final Logger logger = Logger.getLogger("Minecraft");

    private static Commands active;

    private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<CommandSourceStack>();

    public Commands() {
        CoreCommands.register(this.dispatcher);
    }

    public static void setActive(Commands commands) {
        active = commands;
    }

    public static Commands active() {
        return active;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.dispatcher;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public int perform(String command, CommandSourceStack source) {
        String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }

        ParseResults<CommandSourceStack> parse = this.dispatcher.parse(trimmed, source);

        try {
            return this.dispatcher.execute(parse);
        } catch (CommandSyntaxException e) {
            if (this.blockedByPermission(trimmed, source, parse)) {
                source.sendFailure(CommandMessages.translate("commands.base.noperms"));
            } else {
                this.reportSyntaxError(e, source, parse);
            }

            return 0;
        } catch (Exception e) {
            source.sendFailure(CommandMessages.translate("commands.base.error.internal"));
            logger.log(Level.WARNING, "Uncaught exception running command: " + trimmed, e);
            return 0;
        }
    }

    private boolean blockedByPermission(String command, CommandSourceStack source, ParseResults<CommandSourceStack> parse) {
        if (source.hasPermission(CommandSourceStack.LEVEL_OWNER)) {
            return false;
        }

        CommandSourceStack elevated = source.withPermission(CommandSourceStack.LEVEL_OWNER);
        return this.dispatcher.parse(command, elevated).getReader().getCursor() > parse.getReader().getCursor();
    }

    public static String[] usage(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node, CommandSourceStack source) {
        String[] suffixes = dispatcher.getAllUsage(node, source, true);
        String[] lines = new String[suffixes.length];

        for (int i = 0; i < suffixes.length; i++) {
            lines[i] = suffixes[i].isEmpty() ? "/" + node.getName() : "/" + node.getName() + " " + suffixes[i];
        }

        return lines;
    }

    private void reportSyntaxError(CommandSyntaxException e, CommandSourceStack source, ParseResults<CommandSourceStack> parse) {
        if (isStructural(e)) {
            CommandNode<CommandSourceStack> command = commandOf(parse);
            if (command == null) {
                source.sendFailure(CommandMessages.translate("commands.base.unknown"));
            } else {
                this.reportUsage(command, source);
            }

            return;
        }

        source.sendFailure(e.getRawMessage().getString());

        String context = e.getContext();
        if (context != null) {
            source.sendFailure("§7" + context);
        }
    }

    private void reportUsage(CommandNode<CommandSourceStack> command, CommandSourceStack source) {
        String header = CommandMessages.translate("commands.base.incorrect");
        String[] lines = usage(this.dispatcher, command, source);

        if (lines.length == 1) {
            source.sendFailure(header + " " + lines[0]);
            return;
        }

        source.sendFailure(header);
        for (String line : lines) {
            source.sendFailure(line);
        }
    }

    private static boolean isStructural(CommandSyntaxException e) {
        return e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()
            || e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument();
    }

    private static CommandNode<CommandSourceStack> commandOf(ParseResults<CommandSourceStack> parse) {
        List<ParsedCommandNode<CommandSourceStack>> nodes = parse.getContext().getNodes();
        return nodes.isEmpty() ? null : nodes.get(0).getNode();
    }
}
