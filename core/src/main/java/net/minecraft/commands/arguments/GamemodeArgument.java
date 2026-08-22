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

public class GamemodeArgument implements ArgumentType<Integer> {
    public static final String[] NAMES = {"survival", "creative", "adventure", "spectator"};

    private static final Collection<String> EXAMPLES = Arrays.asList("creative", "0", "s");

    public static GamemodeArgument gamemode() {
        return new GamemodeArgument();
    }

    public static int getGamemode(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Integer.class);
    }

    public static String nameOf(int gamemode) {
        return gamemode >= 0 && gamemode < NAMES.length ? NAMES[gamemode] : String.valueOf(gamemode);
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String token = reader.readUnquotedString().toLowerCase();

        for (int mode = 0; mode < NAMES.length; mode++) {
            if (NAMES[mode].equals(token) || NAMES[mode].substring(0, 1).equals(token)) {
                return mode;
            }
        }

        if (token.length() == 1 && token.charAt(0) >= '0' && token.charAt(0) <= '3') {
            return token.charAt(0) - '0';
        }

        reader.setCursor(cursor);
        throw CommandMessages.UNKNOWN_GAMEMODE.createWithContext(reader, token);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();

        for (String name : NAMES) {
            if (name.startsWith(remaining)) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
