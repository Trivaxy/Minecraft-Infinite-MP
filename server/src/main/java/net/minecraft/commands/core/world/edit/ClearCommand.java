package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class ClearCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        if (this.checkCorners(src)) {
            int cleared = WorldEdit.clear(theWorld);
            src.print(cleared + " " + Translator.getInstance().translateKey("commands.clear.blocks"));
        }
    }

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.clear");
    }
}
