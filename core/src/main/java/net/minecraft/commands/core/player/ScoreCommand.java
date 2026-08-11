package net.minecraft.commands.core.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;

public class ScoreCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        String mode = null;
        String amount = null;
        int points = 0;
        Player player = null;
        if (args.length == 1) {
            mode = "set";
            amount = args[0];
            player = ca.demandPlayer(src);
        }

        if (args.length == 2) {
            mode = args[0];
            amount = args[1];
            player = ca.demandPlayer(src);
        }

        if (args.length == 3) {
            player = ca.getPlayer(args[0]);
            mode = args[1];
            amount = args[2];
        }

        if (player != null) {
            points = parseInt(amount);
            if ("add".equalsIgnoreCase(mode)) {
                player.score += points;
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.score.add"), points, player.username));
            }

            if ("set".equalsIgnoreCase(mode)) {
                player.score = points;
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.score.set"), player.username, points));
            }

            if ("remove".equalsIgnoreCase(mode)) {
                player.score -= points;
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.score.remove"), points, player.username));
            }
        } else {
            src.print(Translator.getInstance().translateKey("commands.score.error"));
        }
    }

    @Override
    public String name() {
        return "score";
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
        return "<add | set | remove> <amount> " + Translator.getInstance().translateKey("commands.score");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<playername> <add | set | remove> <amount> " + Translator.getInstance().translateKey("commands.score.player"),
            "<amount> " + Translator.getInstance().translateKey("commands.score.amount")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"add", "set", "remove"});
        return list;
    }
}
