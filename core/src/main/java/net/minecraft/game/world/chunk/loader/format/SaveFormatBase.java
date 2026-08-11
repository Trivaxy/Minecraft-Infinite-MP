package net.minecraft.game.world.chunk.loader.format;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.chunk.loader.handler.SaveHandlerBase;
import net.minecraft.util.ProgressBar;

public class SaveFormatBase implements SaveFormat {
    static Logger logger = Logger.getLogger("Minecraft");
    protected final File savesDirectory;

    public SaveFormatBase(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.savesDirectory = file;
    }

    @Override
    public String getFormatName() {
        return "Alpha Format";
    }

    @Override
    public List<FormatComparator> getSaveList() {
        ArrayList<FormatComparator> saves = new ArrayList<>();

        for (int index = 0; index < 5; index++) {
            String name = "World" + (index + 1);
            WorldInfo worldinfo = this.getWorldInfo(name);
            if (worldinfo != null) {
                saves.add(
                    new FormatComparator(
                        name, "", "", worldinfo.getLastTimePlayed(), worldinfo.getSizeOnDisk(), worldinfo.getGamemode(), false, worldinfo.getCheats()
                    )
                );
            }
        }

        return saves;
    }

    @Override
    public void flushCache() {
    }

    @Override
    public WorldInfo getWorldInfo(String name) {
        File file = new File(this.savesDirectory, name);
        if (!file.exists()) {
            return null;
        }

        File levelFile = new File(file, "level.dat");
        if (levelFile.exists()) {
            try {
                CompoundTag tag = NBTReader.readCompressed(Files.newInputStream(levelFile.toPath()));
                CompoundTag data = tag.getCompoundTag("Data");
                return new WorldInfo(data);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        levelFile = new File(file, "level.dat_old");
        if (levelFile.exists()) {
            try {
                CompoundTag tag = NBTReader.readCompressed(Files.newInputStream(levelFile.toPath()));
                CompoundTag data = tag.getCompoundTag("Data");
                return new WorldInfo(data);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void renameWorld(String oldName, String newName) {
        File file = new File(this.savesDirectory, oldName);
        if (file.exists()) {
            File levelFile = new File(file, "level.dat");
            if (levelFile.exists()) {
                try {
                    CompoundTag tag = NBTReader.readCompressed(Files.newInputStream(levelFile.toPath()));
                    CompoundTag data = tag.getCompoundTag("Data");
                    data.setString("LevelName", newName);
                    NBTReader.writeCompressed(tag, Files.newOutputStream(levelFile.toPath()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteWorldDirectory(String name) {
        File file = new File(this.savesDirectory, name);
        if (file.exists()) {
            deleteFiles(Objects.requireNonNull(file.listFiles()));
            file.delete();
        }
    }

    protected static void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                logger.info("Deleting " + file);
                deleteFiles(Objects.requireNonNull(file.listFiles()));
            }

            file.delete();
        }
    }

    @Override
    public SaveHandler getSaveLoader(String name, boolean flag) {
        return new SaveHandlerBase(this.savesDirectory, name, flag);
    }

    @Override
    public boolean isOldMapFormat(String name) {
        return false;
    }

    @Override
    public boolean convertMapFormat(String name, ProgressBar progress) {
        return false;
    }
}
