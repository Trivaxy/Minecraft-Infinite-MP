package net.minecraft.game.world.chunk.loader.format;

public class FormatComparator implements Comparable<FormatComparator> {
    private final String fileName;
    private final String displayName;
    private final long lastTimePlayed;
    public final long sizeOnDisk;
    private final boolean requiresConversion;
    private final int gameType;
    private final boolean cmds;
    public String versionString;

    public FormatComparator(String filename, String name, String version, long playtime, long size, int gm, boolean convert, boolean cmds) {
        this.fileName = filename;
        this.displayName = name;
        this.versionString = version;
        this.lastTimePlayed = playtime;
        this.sizeOnDisk = size;
        this.gameType = gm;
        this.requiresConversion = convert;
        this.cmds = cmds;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    public long getLastTimePlayed() {
        return this.lastTimePlayed;
    }

    public int compareTo(FormatComparator compare) {
        if (this.lastTimePlayed < compare.lastTimePlayed) {
            return 1;
        } else {
            return this.lastTimePlayed > compare.lastTimePlayed ? -1 : this.fileName.compareTo(compare.fileName);
        }
    }

    public int getGameType() {
        return this.gameType;
    }

    public boolean commandsEnabled() {
        return this.cmds;
    }
}
