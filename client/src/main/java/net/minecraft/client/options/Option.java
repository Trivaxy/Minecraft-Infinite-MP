package net.minecraft.client.options;

import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.localization.Translator;

public class Option<T> {
    final String optionName;
    private final Option.Type type;
    private final Option.Category category;
    private static int pointer = 0;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final IntFunction<String> label;

    public Option(String name, Option.Type type, Option.Category category, Supplier<T> getter, Consumer<T> setter, IntFunction<String> label) {
        this.optionName = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.label = label;
        this.category = category;
        OptionsList.options[pointer++] = this;
    }

    public T getValue() {
        return this.getter.get();
    }

    public void setValue(T value) {
        this.setter.accept(value);
    }

    public Option.Type getType() {
        return this.type;
    }

    public Option.Category getCategory() {
        return this.category;
    }

    public String getOptionName() {
        return Translator.getInstance().translateKey("options." + this.optionName);
    }

    public String getOptionDesc() {
        return "desc." + this.optionName;
    }

    public String getOptionLabel(int optionMenuValue) {
        return optionMenuValue >= 0 && optionMenuValue < OptionsList.options.length ? this.label.apply(optionMenuValue) : "";
    }

    public enum Category {
        GENERAL,
        GRAPHICS,
        AUDIO;
    }

    public enum Type {
        TOGGLE,
        SLIDER,
        CHOICE;
    }
}
