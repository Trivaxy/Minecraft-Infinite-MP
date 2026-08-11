package net.minecraft.commands.core.player;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.misc.ChatPacket;

public class MeCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        Player playerEntity = ca.getConfigManager().getPlayer(src.getUsername());
        String ass = joinArgArray(args, 0);
        String msg = ass;
        if (playerEntity != null) {
            msg = "* " + playerEntity.username + " " + msg.trim();
            if (ca.isServer()) {
                logger.info(msg);
            }

            ca.getConfigManager().sendPacketToAll(new ChatPacket(msg));
        }
    }

    @Override
    public String name() {
        return "me";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.me");
    }
}
