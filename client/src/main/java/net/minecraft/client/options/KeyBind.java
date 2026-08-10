package net.minecraft.client.options;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.localization.Translator;

public class KeyBind {
    public static List<KeyBind> bindingList = new ArrayList<>();
    private static Int2ObjectOpenHashMap<KeyBind> bindingHash = new Int2ObjectOpenHashMap<>();
    public KeyBind.Category category;
    public String name;
    private static int pointer = 0;
    public int key;
    public int defaultKey;
    public int pressTime;
    public boolean pressed;

    public KeyBind(String name, int key, KeyBind.Category cat) {
        this.name = name;
        this.key = key;
        this.defaultKey = key;
        OptionsList.allKeys[pointer++] = this;
        bindingHash.put(key, this);
        bindingList.add(this);
        this.category = cat;
    }

    public static void tick(int key) {
        KeyBind bind = bindingHash.get(key);
        if (bind != null) {
            bind.pressTime++;
        }
    }

    public static void setState(int key, boolean state) {
        KeyBind bind = bindingHash.get(key);
        if (bind != null) {
            bind.pressed = state;
        }
    }

    public static void unpressAll() {
        for (int key = 0; key < OptionsList.allKeys.length; key++) {
            OptionsList.allKeys[key].unpress();
        }
    }

    public boolean pressed() {
        if (this.pressTime == 0) {
            return false;
        }

        this.pressTime--;
        return true;
    }

    public void unpress() {
        this.pressTime = 0;
        this.pressed = false;
    }

    public static void refreshBinds() {
        bindingHash.clear();

        for (KeyBind bind : bindingList) {
            bindingHash.put(bind.key, bind);
        }
    }

    public enum Category {
        MOVEMENT("options.movement"),
        GAMEPLAY("options.gameplay"),
        INVENTORY("options.inventory"),
        GENERAL("options.misc"),
        DEBUG("options.debug");

        String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return Translator.getInstance().translateKey(this.name);
        }
    }
}
