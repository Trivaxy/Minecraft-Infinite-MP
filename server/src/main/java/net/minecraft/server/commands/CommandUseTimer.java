package net.minecraft.server.commands;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandUseTimer {
    public static HashMap<String, Integer> wooded = new HashMap<>();

    public static void tick() {
        ArrayList<String> timeouts = new ArrayList<>();

        for (String key : wooded.keySet()) {
            int value = wooded.get(key);
            if (value > 0) {
                wooded.put(key, value - 1);
            } else {
                timeouts.add(key);
            }
        }

        for (int i = 0; i < timeouts.size(); i++) {
            wooded.remove(timeouts.get(i));
        }
    }
}
