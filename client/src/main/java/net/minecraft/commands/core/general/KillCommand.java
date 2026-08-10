package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class KillCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        ConfigManager cfm = ca.getConfigManager();
        if (args.length == 0) {
            this.killTarget(cfm, ca, src.getUsername(), src);
        } else {
            if (args.length > 0 && (cfm.isOp(src.getUsername()) || src.getType() == Command.UserType.ADMINISTRATOR)) {
                if (this.killTarget(cfm, ca, args[0], src)) {
                    sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.kill.direct") + " " + args[0]);
                }
            } else {
                this.killTarget(cfm, ca, src.getUsername(), src);
            }
        }
    }

    private boolean killTarget(ConfigManager cfm, CommandAccessor c, String username, CommandSource s) {
        if ("all".equalsIgnoreCase(username)) {
            int e = this.killAll(c.getWorld(0));
            sendNoticeToOps(c, s, String.format(Translator.getInstance().translateKey("commands.kill.all"), e));
            return true;
        }

        Player d = cfm.getPlayer(username);
        if (d != null) {
            d.damageEntity(DamageSource.outOfWorld, 1000);
            return true;
        }

        String e = "";

        for (Entity entity : c.getWorld(0).entities.toArray(new Entity[0])) {
            if (entity.getEntityString() != null && username.equalsIgnoreCase(entity.getEntityString())) {
                entity.damageEntity(DamageSource.outOfWorld, 1000);
                e = entity.getEntityString();
            }
        }

        if (!e.isEmpty()) {
            sendNoticeToOps(c, s, String.format(Translator.getInstance().translateKey("commands.kill.all.specific"), e));
        }

        return false;
    }

    private int killAll(World world) {
        int count = 0;

        for (Entity entity : world.entities.toArray(new Entity[0])) {
            if (entity != null && !(entity instanceof Player)) {
                entity.damageEntity(DamageSource.outOfWorld, 1000);
                count++;
            }
        }

        return count;
    }

    @Override
    public String name() {
        return "kill";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.kill");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<all> " + Translator.getInstance().translateKey("commands.kill.extra"),
            "<mob> " + Translator.getInstance().translateKey("commands.kill.specific"),
            "<playername> " + Translator.getInstance().translateKey("commands.kill.player")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> finalList = EntityManager.validEntityIDs();
        finalList.add("all");
        String[] entities = finalList.toArray(new String[0]);
        list.add(entities);
        return list;
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }
}
