package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;

public class DimensionCommand extends Command {
    private static HashMap<String, Integer> dimension = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("overworld", 0);
            this.put("crimson", 1);
            this.put("astral", 2);
            this.put("void", 3);
        }
    };

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        ca.demandPlayer(src);
        demandArgCountOf(1, args);
        Player player = ca.getPlayer(src);

        try {
            player.switchDimension(dimension.get(args[0].toLowerCase()), true);
        } catch (Exception ex) {
            int id = parseInt(args[0]);
            if (id >= 4) {
                src.print(Translator.getInstance().translateKey("commands.dimension.error"));
            } else {
                player.switchDimension(id, true);
            }
        }
    }

    @Override
    public String name() {
        return "dimension";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("dim");
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
        return "<id | name> " + Translator.getInstance().translateKey("commands.dimension");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"overworld", "crimson", "astral", "void"});
        return list;
    }
}
