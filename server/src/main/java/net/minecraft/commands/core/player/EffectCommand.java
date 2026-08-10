package net.minecraft.commands.core.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.localization.Translator;

public class EffectCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length == 1) {
                Player player = ca.demandPlayer(src);
                player.clearEffects();
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.effect.clear"), player.username));
                return;
            }

            Player player;
            if (args.length == 2 && (player = ca.getPlayer(args[1])) != null) {
                player.clearEffects();
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.effect.clear"), player.username));
                return;
            }
        }

        int max = EffectList.effects.length;
        if (args[0].equalsIgnoreCase("list")) {
            for (int i = 0; i < max; i++) {
                if (EffectList.effects[i] != null) {
                    src.print(EffectList.effects[i].getPotionName().replaceAll("potion.", ""));
                }
            }
        } else {
            demandArgCountOf(3, args);
            int id = 0;
            int lvl = parseInt(args[1]);
            int durasmell = parseInt(args[2]);

            for (int i = 0; i < max; i++) {
                if (EffectList.effects[i] != null && args[0].equalsIgnoreCase(EffectList.effects[i].getPotionName().replaceAll("potion.", ""))) {
                    id = EffectList.effects[i].id;
                }
            }

            if (id == 0) {
                id = parseInt(args[0]);
            }

            Player player;
            if (args.length > 3) {
                player = ca.getPlayer(args[3]);
            } else {
                player = ca.demandPlayer(src);
            }

            if (id >= 0 && id < max && EffectList.effects[id] != null) {
                EffectData pe = new EffectData(id, durasmell, lvl);
                player.addEffect(pe);
                sendNoticeToOps(
                    ca,
                    src,
                    String.format(
                        Translator.getInstance().translateKey("commands.effect.apply"),
                        Translator.getInstance().translateKey(pe.getPotionName()) + " " + pe.getPotionAmplifier(),
                        player.username,
                        pe.getPotionDuration()
                    )
                );
            } else {
                throw new CommandException(Translator.getInstance().translateKey("commands.effect.error.id"));
            }
        }
    }

    @Override
    public String name() {
        return "effect";
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
        return "<id> <amplifier> <duration> " + Translator.getInstance().translateKey("commands.effect");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<id> <amplifier> <duration> <player> " + Translator.getInstance().translateKey("commands.effect.player"),
            "<clear> " + Translator.getInstance().translateKey("commands.effect.clear"),
            "<clear> <player> " + Translator.getInstance().translateKey("commands.effect.clear.player")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> potions = new ArrayList<>();

        for (int i = 0; i < EffectList.effects.length; i++) {
            if (EffectList.effects[i] != null) {
                potions.add(EffectList.effects[i].getPotionName().replaceAll("potion.", ""));
            }
        }

        potions.add("clear");
        potions.add("list");
        String[] effects = potions.toArray(new String[0]);
        list.add(effects);
        list.add(new String[]{"1", "2", "3", "4", "5"});
        return list;
    }
}
