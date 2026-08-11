package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class ReplaceCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        int existingBlockID = -1;
        int existingMeta = -1;
        int blockID = 0;
        int meta = 0;

        try {
            existingBlockID = Integer.parseInt(args[0]);
            existingMeta = Integer.parseInt(args[1]);
            blockID = Integer.parseInt(args[2]);
            if (args.length > 3) {
                meta = Integer.parseInt(args[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.checkCorners(src)) {
            int cleared = WorldEdit.substitute(theWorld, existingBlockID, existingMeta, blockID, meta);
            src.print(cleared + " " + Translator.getInstance().translateKey("commands.fill.blocks"));
        }
    }

    @Override
    public String name() {
        return "replace";
    }

    @Override
    public String help() {
        return "<existingBlockID> <existingMeta> <blockID> <meta> " + Translator.getInstance().translateKey("commands.replace");
    }
}
