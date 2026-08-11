package net.minecraft.game.world.chunk.loader.format;

import java.util.List;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.util.ProgressBar;

public interface SaveFormat {
    String getFormatName();

    SaveHandler getSaveLoader(String var1, boolean var2);

    List<FormatComparator> getSaveList();

    void flushCache();

    WorldInfo getWorldInfo(String var1);

    void deleteWorldDirectory(String var1);

    void renameWorld(String var1, String var2);

    boolean isOldMapFormat(String var1);

    boolean convertMapFormat(String var1, ProgressBar var2);
}
