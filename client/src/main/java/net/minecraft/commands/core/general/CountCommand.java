package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.localization.Translator;

public class CountCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        int count = 0;
        if (!args[0].equalsIgnoreCase("all")) {
            for (Entity entity : ca.getWorld(0).entities.toArray(new Entity[0])) {
                if (entity.getEntityString() != null && args[0].equalsIgnoreCase(entity.getEntityString())) {
                    count++;
                }
            }

            sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.count.success"), count, args[0]));
        } else {
            Entity[] array = ca.getWorld(0).entities.toArray(new Entity[0]);

            for (int i = 0; i < array.length; i++) {
                count++;
            }

            sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.count.all"), count));
        }
    }

    @Override
    public String name() {
        return "count";
    }

    @Override
    public String help() {
        return "<all | entity> " + Translator.getInstance().translateKey("commands.count");
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
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }
}
