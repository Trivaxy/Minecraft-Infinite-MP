package net.minecraft.commands.core.world.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.game.world.edit.exporter.ExporterBase;
import net.minecraft.localization.Translator;

public class ExportCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        int idx = args.length;
        if (idx == 1 && "list".equals(args[0])) {
            src.print(ExporterBase.getList());
        } else if (idx == 2 && "help".equals(args[0])) {
            ExporterBase exporter = ExporterBase.getByName(args[1]);
            if (exporter != null) {
                src.print(exporter.getHelp());
            } else {
                src.print(Translator.getInstance().translateKey("commands.export.error") + " " + args[1]);
            }
        } else if (this.checkCorners(src)) {
            src.print(Translator.getInstance().translateKey("commands.export.attempt"));
            String exporterName = args[0];
            String fileName = args[1];
            String arg = idx > 3 ? args[2] : "";
            ExporterBase exporter = ExporterBase.getByName(exporterName);
            if (exporter != null) {
                if (WorldEdit.export(theWorld, 0, true, thePlayer, fileName, exporter, arg)) {
                    src.printf(Translator.getInstance().translateKey("commands.export.success"), WorldEdit.clipboardSize(), fileName, exporterName);
                } else {
                    src.print(Translator.getInstance().translateKey("commands.export.fail"));
                }
            } else {
                src.print(Translator.getInstance().translateKey("commands.export.error") + " " + exporterName);
            }
        }
    }

    @Override
    public String name() {
        return "export";
    }

    @Override
    public String help() {
        return "<exporter> <filename> [<arg>] " + Translator.getInstance().translateKey("commands.export");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<list> " + Translator.getInstance().translateKey("commands.export.list"),
            "<help> <exporter> " + Translator.getInstance().translateKey("commands.export.help")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"list", "help", "raw", "columnsArray", "schematic"});
        return list;
    }
}
