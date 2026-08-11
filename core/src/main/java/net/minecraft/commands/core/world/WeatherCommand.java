package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class WeatherCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        World level = ca.getWorld(0);
        if ("rain".equalsIgnoreCase(args[0])) {
            level.settings.setIsRaining(true);
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.weather.rain"));
        } else if ("thunder".equalsIgnoreCase(args[0])) {
            level.settings.setIsThundering(true);
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.weather.thunder"));
        } else if ("clear".equalsIgnoreCase(args[0])) {
            level.stopAllWeather();
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.weather.clear"));
        } else if ("freeze".equalsIgnoreCase(args[0])) {
            level.settings.setWeather(false);
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.weather.freeze"));
        } else if ("unfreeze".equalsIgnoreCase(args[0])) {
            level.settings.setWeather(true);
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.weather.unfreeze"));
        } else {
            src.print(Translator.getInstance().translateKey("commands.weather.error"));
        }
    }

    @Override
    public String name() {
        return "weather";
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
        return "<rain | thunder | clear> " + Translator.getInstance().translateKey("commands.weather");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"rain", "thunder", "clear", "freeze", "unfreeze"});
        return list;
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<freeze | unfreeze> " + Translator.getInstance().translateKey("commands.weather.state"));
    }
}
