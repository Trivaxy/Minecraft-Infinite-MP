package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class CutCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        if (this.checkCorners(src)) {
            WorldEdit.cut(theWorld, thePlayer);
            src.print(WorldEdit.clipboardSize() + " " + Translator.getInstance().translateKey("commands.cut.blocks"));
        }
    }

    @Override
    public String name() {
        return "cut";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.cut");
    }
}
