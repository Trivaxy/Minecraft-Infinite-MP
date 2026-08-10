package net.minecraft.game.world.chunk.loader.handler;

import java.io.File;
import java.util.List;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.dimension.Dimension;

public interface SaveHandler {
    WorldInfo loadWorldInfo();

    void checkSessionLock();

    FileLoader getChunkLoader(Dimension var1);

    void saveWorldInfo(WorldInfo var1, List<Player> var2);

    void cleanupFileCache();

    PlayerFileData getPlayerStorage();

    File getMapFileFromName(String var1);

    String getSaveDirectoryName();
}
