package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class FillCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        if (this.checkCorners(src)) {
            int id = parseInt(args[0]);
            int meta = 0;
            if (args.length == 2) {
                meta = parseInt(args[1]);
            }

            int cleared = WorldEdit.fill(theWorld, id, meta);
            src.print(cleared + " " + Translator.getInstance().translateKey("commands.fill.blocks"));
        }
    }

    @Override
    public String name() {
        return "fill";
    }

    @Override
    public String help() {
        return "<id> <metadata> " + Translator.getInstance().translateKey("commands.fill");
    }
}
