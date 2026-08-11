package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class GameRuleCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        World level = ca.getWorld(player.dimension);
        demandArgCountOf(2, args);
        boolean state = false;
        if (args[1].equalsIgnoreCase("on")) {
            state = true;
        } else if (args[1].equalsIgnoreCase("off")) {
            state = false;
        }

        switch (args[0]) {
            case "oldarmor":
                player.properties.setArmor(state);
                break;
            case "combat":
                player.properties.setAttack(state);
                break;
            case "durability":
                player.properties.setDurability(state);
                break;
            case "graves":
                player.properties.setGrave(state);
                break;
            case "hunger":
                player.properties.setHunger(state);
                break;
            case "keepinventory":
                player.properties.setBabyMode(state);
                break;
            case "hardcore":
                player.properties.setPermadeath(state);
                break;
            case "insomnia":
                player.properties.setCanSleep(!state);
                break;
            case "sprint":
                player.properties.setSprint(state);
                break;
            case "blockdrops":
                level.settings.blockDrops = state;
                break;
            case "griefing":
                level.settings.explosions = state;
                break;
            case "physics":
                level.settings.setPhysicsDisabled(!state);
                break;
            case "mobdrops":
                level.settings.mobDrops = state;
                break;
            case "mobai":
                level.settings.setAiDisabled(!state);
                break;
            case "mobspawning":
                level.settings.spawning = state;
                break;
            default:
                throw new CommandException(Translator.getInstance().translateKey("commands.gamerule.error"));
        }

        if (state) {
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.gamerule.set") + " " + args[0]);
        } else {
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.gamerule.dis") + " " + args[0]);
        }
    }

    @Override
    public String name() {
        return "gamerule";
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
        return "<gamerule> <on | off> " + Translator.getInstance().translateKey("commands.gamerule");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(
            new String[]{
                "oldarmor",
                "combat",
                "durability",
                "graves",
                "hunger",
                "keepinventory",
                "hardcore",
                "insomnia",
                "sprint",
                "blockdrops",
                "griefing",
                "physics",
                "mobdrops",
                "mobai",
                "mobspawning"
            }
        );
        list.add(new String[]{"on", "off"});
        return list;
    }
}
