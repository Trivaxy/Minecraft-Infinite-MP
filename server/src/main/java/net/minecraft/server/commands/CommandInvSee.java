package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;

public class CommandInvSee extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        Player exect = ca.getPlayer(src);
        Player target = ca.getPlayer(args[0]);
        if (exect != null && target != null) {
            exect.displayGUIChest(target.inventory, false);
            sendNoticeToOps(ca, src, String.format("Opened %s's inventory", target.username));
        } else {
            src.print("This command requires a player.");
        }
    }

    @Override
    public String name() {
        return "invsee";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "opens the specified player's inventory on your screen";
    }
}
