package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.entity.player.Player;

public class PlayerArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Steve", "Alice");

    public static PlayerArgument player() {
        return new PlayerArgument();
    }

    public static Player getPlayer(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        String username = context.getArgument(name, String.class);
        Player player = context.getSource().getConfigManager() == null ? null : context.getSource().getConfigManager().getPlayer(username);
        if (player == null) {
            throw CommandMessages.UNKNOWN_PLAYER.create();
        }

        return player;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Object source = context.getSource();
        if (source instanceof CommandSourceStack) {
            String remaining = builder.getRemainingLowerCase();

            for (String name : ((CommandSourceStack)source).getPlayerNames()) {
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
