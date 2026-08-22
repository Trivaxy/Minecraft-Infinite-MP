package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;

public class NonNegativeIntArgument implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "500");

    public static NonNegativeIntArgument nonnegativeInt() {
        return new NonNegativeIntArgument();
    }

    public static int getInteger(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Integer.class);
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        int value = reader.readInt();
        if (value < 0) {
            reader.setCursor(cursor);
            throw CommandMessages.NEGATIVE_AMOUNT.createWithContext(reader, value);
        }

        return value;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
