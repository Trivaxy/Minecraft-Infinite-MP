package net.minecraft.commands.core.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;

public class ClearInvCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player p = null;
        if (args.length == 0) {
            p = ca.getConfigManager().getPlayer(src.getUsername());
        }

        if (args.length == 1) {
            p = ca.getConfigManager().getPlayer(args[0]);
        }

        if (p != null) {
            p.inventory.clearContents(false);
            sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.inv.cleared"), p.username));
        } else {
            src.print(Translator.getInstance().translateKey("commands.inv.noplayer"));
        }
    }

    @Override
    public String name() {
        return "clearinv";
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
        return Translator.getInstance().translateKey("commands.inv.clear");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<player> " + Translator.getInstance().translateKey("commands.inv.clear.player"));
    }
}
