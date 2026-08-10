package net.minecraft.client.commands.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.localization.Translator;

public class TimerCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        if (args.length == 0) {
            src.print(Translator.getInstance().translateKey("commands.timer.value") + " " + ca.clientGetTimer());
        } else {
            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
                float tm = 20.0F;
                ca.clientSetTimer(tm);
                src.print(Translator.getInstance().translateKey("commands.timer.set") + " " + tm);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                float time = parseFloat(args[1]);
                if (time == 0.0F || Float.isNaN(time) || Float.isInfinite(time)) {
                    src.print(Translator.getInstance().translateKey("commands.timer.invalid"));
                    return;
                }

                ca.clientSetTimer(time);
                src.print(Translator.getInstance().translateKey("commands.timer.set") + " " + time);
            } else {
                src.print(Translator.getInstance().translateKey("commands.timer.unknown"));
            }
        }
    }

    @Override
    public String name() {
        return "timer";
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
    public List<String> additionalHelp() {
        return Arrays.asList("<reset> " + Translator.getInstance().translateKey("commands.timer.reset"));
    }

    @Override
    public String help() {
        return "<set> <float> " + Translator.getInstance().translateKey("commands.timer");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"reset", "set"});
        return list;
    }
}
