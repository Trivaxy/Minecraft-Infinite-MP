package net.minecraft.client.commands.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.localization.Translator;

public class GamemodeCommand extends Command {
    private static HashMap<String, Integer> gametype = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("survival", 0);
            this.put("creative", 1);
            this.put("adventure", 2);
            this.put("spectator", 3);
        }
    };

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);

        try {
            ca.clientSetGamemode(gametype.get(args[0].toLowerCase()));
        } catch (Exception ex) {
            ca.clientSetGamemode(parseInt(args[0]));
        }

        src.print(Translator.getInstance().translateKey("commands.gamemode.change"));
    }

    @Override
    public String name() {
        return "gamemode";
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
        return "<id | name> " + Translator.getInstance().translateKey("commands.gamemode");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"survival", "creative", "adventure", "spectator"});
        return list;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("gm");
    }
}
