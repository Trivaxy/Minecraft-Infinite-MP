package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class EventCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        ca.demandPlayer(src);
        demandArgCountOf(1, args);
        Player player = ca.getPlayer(src);
        World world = ca.getWorld(player.dimension);
        if (args[0].equalsIgnoreCase("list")) {
            src.print(Translator.getInstance().translateKey("commands.event.list") + ":");

            for (int i = 0; i < EventCommand.Event.values().length; i++) {
                src.print(EventCommand.Event.fromId(i).name());
            }
        } else {
            try {
                world.setEvent(EventCommand.Event.fromName(args[0].toLowerCase()).id);
                if (args[0].equalsIgnoreCase("clear")) {
                    src.print(Translator.getInstance().translateKey("commands.event.clear"));
                } else {
                    src.print(Translator.getInstance().translateKey("commands.event.set") + " " + args[0]);
                }
            } catch (Exception ex) {
                int id = parseInt(args[0]);
                if (id >= 6) {
                    src.print(Translator.getInstance().translateKey("commands.event.error"));
                } else {
                    if (id == 0) {
                        src.print(Translator.getInstance().translateKey("commands.event.clear"));
                    } else {
                        src.print(Translator.getInstance().translateKey("commands.event.set") + " " + EventCommand.Event.fromId(id));
                    }

                    world.setEvent(id);
                }
            }
        }
    }

    @Override
    public String name() {
        return "event";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.CLIENT;
    }

    @Override
    public String help() {
        return "<id | name> " + Translator.getInstance().translateKey("commands.event");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<list> " + Translator.getInstance().translateKey("commands.event.list"));
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"clear", "badsun", "bloodmoon", "bluemoon", "list"});
        return list;
    }

    public enum Event {
        CLEAR(0),
        BADSUN(1),
        BLOODMOON(2),
        BLUEMOON(3);

        public final int id;

        Event(int id) {
            this.id = id;
        }

        public static EventCommand.Event fromId(int id) {
            for (EventCommand.Event e : values()) {
                if (e.id == id) {
                    return e;
                }
            }

            return null;
        }

        public static EventCommand.Event fromName(String name) {
            for (EventCommand.Event event : values()) {
                if (event.name().equalsIgnoreCase(name)) {
                    return event;
                }
            }

            return null;
        }
    }
}
