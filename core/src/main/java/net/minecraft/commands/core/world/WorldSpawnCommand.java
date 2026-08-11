package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class WorldSpawnCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        World w = ca.getWorld(player.dimension);
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        if (args.length == 3) {
            x = parseRelativeCoords(args[0], player.posX);
            y = parseRelativeCoords(args[1], player.posY);
            z = parseRelativeCoords(args[2], player.posZ);
        }

        int xPos = MathHelper.floor_double(x);
        int yPos = MathHelper.floor_double(y);
        int zPos = MathHelper.floor_double(z);
        w.setSpawnPoint(new Position(xPos, yPos, zPos));
        src.printf(Translator.getInstance().translateKey("commands.world.setspawn"), xPos, yPos, zPos);
    }

    @Override
    public String name() {
        return "worldspawn";
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
        return "<x> <y> <z> " + Translator.getInstance().translateKey("commands.worldspawn");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        return list;
    }
}
