package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class SetBlockCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        World world = ca.getWorld(player.dimension);
        demandArgCountOf(4, args);
        int x = MathHelper.floor_double(parseRelativeCoords(args[0], player.posX));
        int y = MathHelper.floor_double(parseRelativeCoords(args[1], player.posY));
        int z = MathHelper.floor_double(parseRelativeCoords(args[2], player.posZ));
        int id = 0;
        int meta = 0;
        String[] data = args[3].split(":");
        if (data.length == 2) {
            id = parseInt(data[0]);
            meta = parseInt(data[1]);
        } else {
            id = parseInt(args[3]);
        }

        if (!checkBlockId(id, meta)) {
            world.setBlockAndMetadataWithNotify(x, y, z, id, meta);
            src.printf(Translator.getInstance().translateKey("commands.block.set"), id, meta, x, y, z);
        } else {
            throw new CommandException(Translator.getInstance().translateKey("commands.setblock.error"));
        }
    }

    @Override
    public String name() {
        return "setblock";
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
        return "<x> <y> <z> <id:meta> " + Translator.getInstance().translateKey("commands.setblock");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        list.add(new String[]{"~"});
        return list;
    }

    public static boolean checkBlockId(int id, int data) {
        boolean invalidmeta = false;
        boolean nullBlock = false;
        boolean overflow = id < 0 || id > BlockList.blocks.length;
        if (!overflow) {
            nullBlock = BlockList.blocks[id] == null;
            if (!nullBlock) {
                invalidmeta = data > BlockList.blocks[id].getMaxMeta();
            }
        }

        return overflow || nullBlock || invalidmeta;
    }
}
