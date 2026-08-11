package net.minecraft.game.world.chunk.loader.handler;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.dimension.Dimension;

public class SaveHandlerBase implements SaveHandler, PlayerFileData {
    private static final Logger logger = Logger.getLogger("Minecraft");
    public final File saveDirectory;
    private final File playersDirectory;
    private final File mapDataDir;
    private final String saveDirectoryName;
    private final long currTime = System.currentTimeMillis();

    public SaveHandlerBase(File directory, String name, boolean noPlayers) {
        this.saveDirectory = new File(directory, name);
        this.saveDirectory.mkdirs();
        this.mapDataDir = new File(this.saveDirectory, "data");
        this.mapDataDir.mkdirs();
        this.playersDirectory = new File(this.saveDirectory, "players");
        this.saveDirectoryName = name;
        if (noPlayers) {
            this.playersDirectory.mkdirs();
        }

        this.setSessionLock();
    }

    private void setSessionLock() {
        try {
            File file = new File(this.saveDirectory, "session.lock");

            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file.toPath()))) {
                out.writeLong(this.currTime);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    protected File getSaveDirectory() {
        return this.saveDirectory;
    }

    @Override
    public void checkSessionLock() {
        try {
            File file = new File(this.saveDirectory, "session.lock");

            try (DataInputStream out = new DataInputStream(Files.newInputStream(file.toPath()))) {
                if (out.readLong() != this.currTime) {
                    throw new SaveHandlerBase.LevelConflictException("The save is being accessed from another location, aborting");
                }
            }
        } catch (IOException ex) {
            throw new SaveHandlerBase.LevelConflictException("Failed to check session lock, aborting");
        }
    }

    @Override
    public FileLoader getChunkLoader(Dimension dim) {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    @Override
    public WorldInfo loadWorldInfo() {
        File levelFile = new File(this.saveDirectory, "level.dat");
        if (levelFile.exists()) {
            try {
                CompoundTag levelTag = NBTReader.readCompressed(Files.newInputStream(levelFile.toPath()));
                CompoundTag data = levelTag.getCompoundTag("Data");
                return new WorldInfo(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void saveWorldInfo(WorldInfo info, List<Player> players) {
        CompoundTag tag = info.getTag();
        if (players != null) {
            tag = info.getPlayerTag(players);
        }

        CompoundTag data = new CompoundTag();
        data.setTag("Data", tag);

        try {
            File newData = new File(this.saveDirectory, "level.dat_new");
            File oldData = new File(this.saveDirectory, "level.dat_old");
            File finalData = new File(this.saveDirectory, "level.dat");
            NBTReader.writeCompressed(data, Files.newOutputStream(newData.toPath()));
            if (oldData.exists()) {
                oldData.delete();
            }

            finalData.renameTo(oldData);
            if (finalData.exists()) {
                finalData.delete();
            }

            newData.renameTo(finalData);
            if (newData.exists()) {
                newData.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void writePlayerData(Player player) {
        try {
            CompoundTag data = new CompoundTag();
            player.save(data);
            File tempFile = new File(this.playersDirectory, "_tmp_.dat");
            File file = new File(this.playersDirectory, player.username + ".dat");
            NBTReader.writeCompressed(data, Files.newOutputStream(tempFile.toPath()));
            if (file.exists()) {
                file.delete();
            }

            tempFile.renameTo(file);
        } catch (Exception ex) {
            logger.warning("Failed to save player data for " + player.username);
        }
    }

    @Override
    public void readPlayerData(Player player) {
        CompoundTag tag = this.getPlayerData(player.username);
        if (tag != null) {
            player.load(tag);
        }
    }

    @Override
    public CompoundTag getPlayerData(String filename) {
        FileInputStream stream = null;

        try {
            File file = new File(this.playersDirectory, filename + ".dat");
            if (file.exists()) {
                stream = new FileInputStream(file);
                return NBTReader.readCompressed(stream);
            }
        } catch (Exception ex) {
            logger.warning("Failed to load player data for " + filename);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public void cleanupFileCache() {
    }

    @Override
    public PlayerFileData getPlayerStorage() {
        return this;
    }

    @Override
    public File getMapFileFromName(String filename) {
        return new File(this.mapDataDir, filename + ".dat");
    }

    @Override
    public String getSaveDirectoryName() {
        return this.saveDirectoryName;
    }

    public class LevelConflictException extends RuntimeException {
        private static final long serialVersionUID = 5264976095742345215L;

        public LevelConflictException(String msg) {
            super(msg);
        }
    }
}
