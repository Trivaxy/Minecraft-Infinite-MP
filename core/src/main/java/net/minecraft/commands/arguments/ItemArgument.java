package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;

public class ItemArgument implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("4096", "4102");

    public static ItemArgument item() {
        return new ItemArgument();
    }

    public static Item getItem(CommandContext<CommandSourceStack> context, String name) {
        return ItemList.items[context.getArgument(name, Integer.class)];
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        int id = reader.readInt();
        if (id < 0 || id >= ItemList.items.length || ItemList.items[id] == null) {
            reader.setCursor(cursor);
            throw CommandMessages.UNKNOWN_ITEM.createWithContext(reader, id);
        }

        return id;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
