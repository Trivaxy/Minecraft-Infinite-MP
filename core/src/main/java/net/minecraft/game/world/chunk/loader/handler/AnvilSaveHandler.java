package net.minecraft.game.world.chunk.loader.handler;

import java.io.File;
import java.util.List;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.AnvilLoader;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.chunk.loader.region.RegionFileCache;
import net.minecraft.game.world.dimension.Dimension;
import net.minecraft.util.io.ThreadedFileIOBase;

public class AnvilSaveHandler extends SaveHandlerBase {
    public AnvilSaveHandler(File file, String name, boolean flag) {
        super(file, name, flag);
    }

    @Override
    public FileLoader getChunkLoader(Dimension dim) {
        File file = this.getSaveDirectory();
        if (!dim.getSaveFolder().isEmpty()) {
            File dimfolder = new File(file, dim.getSaveFolder());
            dimfolder.mkdirs();
            return new AnvilLoader(dimfolder);
        } else {
            return new AnvilLoader(file);
        }
    }

    @Override
    public void saveWorldInfo(WorldInfo info, List<Player> players) {
        super.saveWorldInfo(info, players);
        info.setConversionStatus(19133);
    }

    @Override
    public void cleanupFileCache() {
        try {
            ThreadedFileIOBase.threadedIOInstance.waitForFinish();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }

        RegionFileCache.clearFileReferences();
    }
}
