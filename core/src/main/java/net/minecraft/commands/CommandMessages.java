package net.minecraft.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import net.minecraft.localization.Translator;

public final class CommandMessages {
    public static final SimpleCommandExceptionType NOT_A_PLAYER = simple("commands.base.error.player");
    public static final SimpleCommandExceptionType NO_PERMISSION = simple("commands.base.noperms");
    public static final SimpleCommandExceptionType UNKNOWN_COMMAND = simple("commands.base.unknown");
    public static final DynamicCommandExceptionType UNKNOWN_PLAYER = dynamic("commands.base.error.unknownplayer");
    public static final SimpleCommandExceptionType INCOMPLETE_POSITION = simple("commands.base.error.position");

    private CommandMessages() {
    }

    public static String translate(String key) {
        return Translator.getInstance().translateKey(key);
    }

    public static Message lazy(final String key) {
        return new Message() {
            @Override
            public String getString() {
                return Translator.getInstance().translateKey(key);
            }
        };
    }

    private static SimpleCommandExceptionType simple(String key) {
        return new SimpleCommandExceptionType(lazy(key));
    }

    private static DynamicCommandExceptionType dynamic(final String key) {
        return new DynamicCommandExceptionType(new Function<Object, Message>() {
            @Override
            public Message apply(final Object argument) {
                return new Message() {
                    @Override
                    public String getString() {
                        return Translator.getInstance().translateKey(key) + " " + argument;
                    }
                };
            }
        });
    }
}
