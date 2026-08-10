package net.minecraft.client.network.world;

import java.io.File;
import java.util.List;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.chunk.loader.handler.PlayerFileData;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.dimension.Dimension;

public class SaveHandlerMP implements SaveHandler {
    @Override
    public WorldInfo loadWorldInfo() {
        return null;
    }

    @Override
    public void checkSessionLock() {
    }

    @Override
    public FileLoader getChunkLoader(Dimension provider) {
        return null;
    }

    @Override
    public void saveWorldInfo(WorldInfo info, List<Player> playerList) {
    }

    @Override
    public void cleanupFileCache() {
    }

    @Override
    public PlayerFileData getPlayerStorage() {
        return null;
    }

    @Override
    public File getMapFileFromName(String fileName) {
        return null;
    }

    @Override
    public String getSaveDirectoryName() {
        return "none";
    }
}
