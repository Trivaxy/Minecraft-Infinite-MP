package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.arrow.Trident;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class SummonCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        if (args.length < 1) {
            throw new CommandException.NotEnoughArgs(Translator.getInstance().translateKey("commands.base.error.notenoughargs"));
        }

        Player player = ca.getConfigManager().getPlayer(src.getUsername());
        String entityName = args[0];
        if (entityName.equalsIgnoreCase("list")) {
            src.print(Translator.getInstance().translateKey("commands.summon.list") + ":");

            for (String string : EntityManager.validEntityIDs()) {
                if (!EntityManager.blockedIds.contains(string)) {
                    src.print(string);
                }
            }
        } else {
            int dim = 0;
            if (player != null) {
                dim = player.dimension;
            }

            if (EntityManager.blockedIds.contains(entityName)) {
                src.print(Translator.getInstance().translateKey("commands.summon.invalid") + ": " + entityName);
            } else {
                if (args.length >= 1 && player != null) {
                    World w = ca.getWorld(dim);
                    Entity entity = EntityManager.createEntity(entityName, w);
                    if (entity == null) {
                        src.print(Translator.getInstance().translateKey("commands.summon.unknown"));
                        return;
                    }

                    if (entity instanceof Trident) {
                        Trident spear = (Trident)entity;
                        spear.shooter = ca.demandPlayer(src);
                    }

                    double x;
                    double y;
                    double z;
                    if (args.length == 4) {
                        x = parseRelativeCoords(args[1], player.posX);
                        y = parseRelativeCoords(args[2], player.posY);
                        z = parseRelativeCoords(args[3], player.posZ);
                    } else {
                        x = player.posX;
                        y = player.posY;
                        z = player.posZ;
                    }

                    entity.posX = x;
                    entity.posY = y;
                    entity.posZ = z;
                    entity.teleportTo(x, y, z, 0.0F, 0.0F);
                    w.spawnEntity(entity);
                    sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.summon.success"), entityName, x, y, z));
                }
            }
        }
    }

    @Override
    public String name() {
        return "summon";
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
        return "<entityName> " + Translator.getInstance().translateKey("commands.summon");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<x> <y> <z> " + Translator.getInstance().translateKey("commands.summon.pos"),
            "<list> " + Translator.getInstance().translateKey("commands.summon.lists")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        String[] entities = EntityManager.validEntityIDs().toArray(new String[0]);
        list.add(entities);
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        return list;
    }
}
