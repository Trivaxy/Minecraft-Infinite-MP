package net.minecraft.commands.core.world.edit;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class PointCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        demandArgCountOf(4, args);
        double x = parseRelativeCoords(args[0], thePlayer.posX);
        double y = parseRelativeCoords(args[1], thePlayer.posY);
        double z = parseRelativeCoords(args[2], thePlayer.posZ);
        if (y <= -1.0) {
            src.print(Translator.getInstance().translateKey("commands.corner.error"));
        } else if (args[3].equalsIgnoreCase("end")) {
            WorldEdit.setCorner2(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
            src.printf(
                Translator.getInstance().translateKey("commands.corner.end.set"),
                MathHelper.floor_double(x),
                MathHelper.floor_double(y),
                MathHelper.floor_double(z)
            );
        } else if (args[3].equalsIgnoreCase("start")) {
            WorldEdit.setCorner1(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
            src.printf(
                Translator.getInstance().translateKey("commands.corner.start.set"),
                MathHelper.floor_double(x),
                MathHelper.floor_double(y),
                MathHelper.floor_double(z)
            );
        } else {
            src.print(Translator.getInstance().translateKey("commands.corner.invalid"));
        }
    }

    @Override
    public String name() {
        return "point";
    }

    @Override
    public String help() {
        return "<x> <y> <z> <start | end> " + Translator.getInstance().translateKey("commands.corner");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"start", "end"});
        return list;
    }
}
