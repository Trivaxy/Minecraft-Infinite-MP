package net.minecraft.game.world.edit.exporter;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.game.physics.VectorBasic;

public abstract class ExporterBase {
    public static HashMap<String, ExporterBase> exporterList = new HashMap<>();
    public static int lastId = 0;
    public static ExporterBase raw = new ExporterRaw().register();
    public static ExporterBase columnsArray = new ExporterColumnsArray().register();
    public static ExporterBase schematic = new ExporterSchematic().register();
    public int id = lastId++;

    public ExporterBase register() {
        exporterList.put(this.getName(), this);
        return this;
    }

    public static ExporterBase getByName(String name) {
        return exporterList.get(name);
    }

    public static String getList() {
        String res = "";
        Iterator<String> it = exporterList.keySet().iterator();

        while (it.hasNext()) {
            res = res + exporterList.get(it.next()).getName();
            if (it.hasNext()) {
                res = res + ", ";
            }
        }

        return res;
    }

    public abstract String getName();

    public abstract boolean export(int[][][] var1, VectorBasic var2, String var3, String var4);

    public abstract String getHelp();
}
