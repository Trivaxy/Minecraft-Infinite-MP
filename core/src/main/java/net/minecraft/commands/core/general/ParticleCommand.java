package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class ParticleCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        demandArgCountOf(1, args);
        if (args[0].equalsIgnoreCase("list")) {
            for (int i = 0; i < ParticleType.values().length; i++) {
                src.print(ParticleType.values()[i].getName());
            }
        } else {
            World level = ca.getWorld(player.dimension);
            ParticleType particleToSpawn = ParticleType.getEnum(args[0]);
            if (particleToSpawn != null) {
                if (args.length != 4 && args.length != 5) {
                    level.spawnParticle(particleToSpawn, x, y, z, 0.0, 0.0, 0.0);
                } else {
                    x = parseRelativeCoords(args[1], player.posX);
                    y = parseRelativeCoords(args[2], player.posY);
                    z = parseRelativeCoords(args[3], player.posZ);
                    if (args.length == 5) {
                        for (int i = 0; i < Integer.valueOf(args[4]); i++) {
                            level.spawnParticle(particleToSpawn, x, y, z, 0.0, 0.0, 0.0);
                        }
                    } else {
                        level.spawnParticle(particleToSpawn, x, y, z, 0.0, 0.0, 0.0);
                    }
                }

                sendNoticeToOps(ca, src, String.format(Translator.getInstance().translateKey("commands.particle.spawn"), particleToSpawn.name(), x, y, z));
            } else {
                src.print(Translator.getInstance().translateKey("commands.particle.error"));
            }
        }
    }

    @Override
    public String name() {
        return "particle";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.CLIENT;
    }

    @Override
    public String help() {
        return "<name> " + Translator.getInstance().translateKey("commands.particle");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<x> <y> <z> " + Translator.getInstance().translateKey("commands.particle.loc"),
            "<x> <y> <z> <count> " + Translator.getInstance().translateKey("commands.particle.count"),
            "<list> " + Translator.getInstance().translateKey("commands.particle.list")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> particles = new ArrayList<>();

        for (int i = 0; i < ParticleType.values().length; i++) {
            particles.add(ParticleType.values()[i].getName());
        }

        particles.add("list");
        String[] entities = particles.toArray(new String[0]);
        list.add(entities);
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        return list;
    }
}
