package net.minecraft.commands.core.world.edit;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public class RotateCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        if (this.checkCorners(src)) {
            if (args[0].equalsIgnoreCase("cw")) {
                WorldEdit.rotate_cw();
            } else if (args[0].equalsIgnoreCase("ccw")) {
                WorldEdit.rotate_ccw();
            }
        }
    }

    @Override
    public String name() {
        return "rotate";
    }

    @Override
    public String help() {
        return "<cw | ccw> " + Translator.getInstance().translateKey("commands.rotate");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"cw", "ccw"});
        return list;
    }
}
