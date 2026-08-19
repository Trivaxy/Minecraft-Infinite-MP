package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
                reportSyntaxError(e, source);
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

    private static void reportSyntaxError(CommandSyntaxException e, CommandSourceStack source) {
        if (e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()) {
            source.sendFailure(CommandMessages.translate("commands.base.unknown"));
            return;
        }

        source.sendFailure(e.getRawMessage().getString());

        String context = e.getContext();
        if (context != null) {
            source.sendFailure("§7" + context);
        }
    }
}
