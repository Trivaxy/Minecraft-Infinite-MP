package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class TimeCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        World w = ca.getWorld(0);
        if (args.length == 0) {
            src.print(Translator.getInstance().translateKey("commands.time.current") + ": " + w.getWorldTime());
        } else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("freeze")) {
                    ca.getWorld(0).setPermaday(true);
                    sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.time.freeze"));
                    return;
                }

                if (args[0].equalsIgnoreCase("unfreeze")) {
                    ca.getWorld(0).setPermaday(false);
                    sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.time.unfreeze"));
                    return;
                }

                int dayLength = (int)w.getSettings().dayLength;
                Map<String, Integer> times = new HashMap<>();
                times.put("day", dayLength / 20);
                times.put("night", dayLength / 2 + dayLength / 20);
                times.put("noon", dayLength / 4);
                times.put("midnight", dayLength / 4 * 3);
                times.put("dawn", dayLength - dayLength / 20);
                times.put("dusk", dayLength / 2);
                String key = args[0].toLowerCase();
                if (times.containsKey(key)) {
                    w.setTime(times.get(key).intValue());
                    sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.time." + key));
                    return;
                }
            }

            demandArgCountOf(2, args);
            int time = parseInt(args[1]);
            if ("add".equalsIgnoreCase(args[0])) {
                w.setTime(w.getWorldTime() + time);
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.time.add"), time));
            } else if ("set".equalsIgnoreCase(args[0])) {
                w.setTime(time);
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.time.set"), time));
            } else {
                src.print(Translator.getInstance().translateKey("commands.time.error"));
            }
        }
    }

    @Override
    public String name() {
        return "time";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return "<set | add> " + Translator.getInstance().translateKey("commands.time");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"set", "add", "day", "night", "noon", "midnight", "dawn", "dusk", "freeze", "unfreeze"});
        return list;
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<freeze | unfreeze> " + Translator.getInstance().translateKey("commands.time.state"),
            "<day | night | noon | midnight | sunrise | sunset> " + Translator.getInstance().translateKey("commands.time.exact")
        );
    }
}
