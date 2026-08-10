package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.player.Player;

public class CommandKick extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        demandArgCountOf(1, args);
        ConfigManager cfm = ca.getConfigManager();
        String duende = joinArgArray(args, 0);
        Player player = cfm.getPlayer(duende);
        if (player != null) {
            cfm.kickPlayer(player, "Kicked by admin");
            sendNoticeToOps(ca, source, "Kicking " + player.username);
        } else {
            source.print("Can't find user " + duende + ". No kick.");
        }
    }

    @Override
    public String name() {
        return "kick";
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
        return "<player> kicks the specified player";
    }
}
