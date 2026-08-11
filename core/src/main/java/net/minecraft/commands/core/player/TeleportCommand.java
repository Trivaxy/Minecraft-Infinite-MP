package net.minecraft.commands.core.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;

public class TeleportCommand extends Command {
    private double x;
    private double y;
    private double z;

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        ConfigManager cf = ca.getConfigManager();
        if (args.length == 4) {
            Player player2 = ca.demandPlayer(src);
            Player player = cf.getPlayer(args[0]);
            this.x = parseRelativeCoords(args[1], player2.posX);
            this.y = parseRelativeCoords(args[2], player2.posY);
            this.z = parseRelativeCoords(args[3], player2.posZ);
            if (this.x > 2.147483047E9 || this.z > 2.147483047E9 || this.x < -2.147483047E9 || this.z < -2.147483047E9) {
                src.print(Translator.getInstance().translateKey("commands.teleport.error.coords"));
            } else if (player == null) {
                src.print(Translator.getInstance().translateKey("commands.teleport.error.user") + " " + args[0]);
            } else {
                cf.teleportPlayerTo(player, this.x, this.y, this.z, 0.0F, 0.0F);
                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.teleport.coords"), args[0], this.x, this.y, this.z));
            }
        } else if (args.length == 3) {
            Player player2 = ca.demandPlayer(src);
            this.x = parseRelativeCoords(args[0], player2.posX);
            this.y = parseRelativeCoords(args[1], player2.posY);
            this.z = parseRelativeCoords(args[2], player2.posZ);
            if (!(this.x > 2.147483047E9) && !(this.z > 2.147483047E9) && !(this.x < -2.147483047E9) && !(this.z < -2.147483047E9)) {
                cf.teleportPlayerTo(player2, this.x, this.y, this.z, 0.0F, 0.0F);
                sendNoticeToOps(
                    ca, src, String.format(Translator.getInstance().translateKey("commands.teleport.coords"), player2.username, this.x, this.y, this.z)
                );
            } else {
                src.print(Translator.getInstance().translateKey("commands.teleport.error.coords"));
            }
        } else {
            if (args.length == 2) {
                Player player1 = cf.getPlayer(args[0]);
                Player trgt = cf.getPlayer(args[1]);
                if (player1 == null) {
                    src.print(Translator.getInstance().translateKey("commands.teleport.error.user") + args[0]);
                } else if (trgt == null) {
                    src.print(Translator.getInstance().translateKey("commands.teleport.error.user") + args[1]);
                } else if (player1.dimension != trgt.dimension) {
                    src.print(Translator.getInstance().translateKey("commands.teleport.error.dimension"));
                } else {
                    cf.teleportPlayerTo(player1, trgt.posX, trgt.posY, trgt.posZ, trgt.yaw, trgt.pitch);
                    sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.teleport.players"), args[0], args[1]));
                }
            } else {
                src.print(Translator.getInstance().translateKey("commands.teleport.error"));
            }
        }
    }

    @Override
    public String name() {
        return "teleport";
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
        return "<x> <y> <z> " + Translator.getInstance().translateKey("commands.teleport");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<player1> <player2> " + Translator.getInstance().translateKey("commands.teleport.player"),
            "<player> <x> <y> <z> " + Translator.getInstance().translateKey("commands.teleport.player.loc")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        return list;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tp", "tele");
    }
}
