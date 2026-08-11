package net.minecraft.commands.core.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.Achievement;

public class AchievementCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        demandArgCountOf(1, args);
        boolean valid = false;
        boolean reset = false;
        if (args[0].equalsIgnoreCase("reset")) {
            for (int i = 0; i < Achievement.globalList.size(); i++) {
                Achievement achievement = Achievement.globalList.get(i);
                if (args[1].equalsIgnoreCase("all")) {
                    player.removeStat(achievement);
                    reset = true;
                    valid = true;
                } else if (args[1].equalsIgnoreCase(achievement.statName)) {
                    player.removeStat(achievement);
                    src.print(Translator.getInstance().translateKey("commands.achievement.reset") + " " + "§e" + achievement.statName);
                    valid = true;
                }
            }
        } else {
            for (int i = 0; i < Achievement.globalList.size(); i++) {
                Achievement achievement = Achievement.globalList.get(i);
                if (args[0].equalsIgnoreCase(achievement.statName) || args[0].equalsIgnoreCase("all")) {
                    player.triggerAchievement(achievement);
                    valid = true;
                }
            }
        }

        if (!valid) {
            src.print(Translator.getInstance().translateKey("commands.achievement.error"));
        }

        if (reset) {
            src.print(Translator.getInstance().translateKey("commands.achievement.reset.all"));
        }
    }

    @Override
    public String name() {
        return "achievement";
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
        return "<name | all> " + Translator.getInstance().translateKey("commands.achievement");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<reset> <name | all> " + Translator.getInstance().translateKey("commands.achievement.resets"));
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> achievements = new ArrayList<>();

        for (int i = 0; i < Achievement.globalList.size(); i++) {
            Achievement achievement = Achievement.globalList.get(i);
            achievements.add(achievement.statName);
        }

        List<String> achRem = achievements;
        achievements.add("reset");
        achRem.add("all");
        list.add(achievements.toArray(new String[0]));
        list.add(achRem.toArray(new String[0]));
        return list;
    }
}
