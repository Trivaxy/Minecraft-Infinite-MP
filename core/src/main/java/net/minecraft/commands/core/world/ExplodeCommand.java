package net.minecraft.commands.core.world;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.Explosion;
import net.minecraft.localization.Translator;

public class ExplodeCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        demandArgCountOf(2, args);
        if (args.length == 5) {
            x = parseRelativeCoords(args[2], player.posX);
            y = parseRelativeCoords(args[3], player.posY);
            z = parseRelativeCoords(args[4], player.posZ);
        }

        float rad = parseFloat(args[0]);
        int type = parseInt(args[1]);
        World level = ca.getWorld(player.dimension);
        Explosion e = new Explosion(level, player, x, y, z, rad);
        switch (type) {
            case 1:
                e.isFlaming = true;
                break;
            case 2:
                e.isMagic = true;
                break;
            case 3:
                e.isWind = true;
                break;
            case 4:
                e.isFrost = true;
        }

        e.explode();
        e.addParticles(type == 0);
    }

    @Override
    public String name() {
        return "explosion";
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
        return "<radius> <type> " + Translator.getInstance().translateKey("commands.explosion");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<radius> <type> <x> <y> <z> " + Translator.getInstance().translateKey("commands.explosion.type"));
    }
}
