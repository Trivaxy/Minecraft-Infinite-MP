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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.world.chunk.BlockPosition;

public class BlockPosArgument implements ArgumentType<Coordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 64 0", "~ ~ ~", "~ ~2 ~");

    public static BlockPosArgument blockPos() {
        return new BlockPosArgument();
    }

    public static BlockPosition getBlockPos(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Coordinates.class).getBlockPos(context.getSource());
    }

    @Override
    public Coordinates parse(StringReader reader) throws CommandSyntaxException {
        return WorldCoordinates.parse(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest("~");
            builder.suggest("~ ~ ~");
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
