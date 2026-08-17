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
import net.minecraft.game.physics.Vector;

public class Vec3Argument implements ArgumentType<Coordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0.5 64 0.5", "~ ~ ~", "~0.5 ~1 ~-0.5");

    public static Vec3Argument vec3() {
        return new Vec3Argument();
    }

    public static Vector getVec3(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Coordinates.class).getPosition(context.getSource());
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
