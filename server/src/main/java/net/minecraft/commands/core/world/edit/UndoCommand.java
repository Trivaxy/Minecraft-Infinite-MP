package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class UndoCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        if (WorldEdit.hasUndo) {
            src.print(Translator.getInstance().translateKey("commands.undo.success") + " " + WorldEdit.undoOrigin);
            WorldEdit.undo(theWorld);
        } else {
            src.print(Translator.getInstance().translateKey("commands.undo.error"));
        }
    }

    @Override
    public String name() {
        return "undo";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.undo");
    }
}
