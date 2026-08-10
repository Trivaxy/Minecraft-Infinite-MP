package net.minecraft.game.world.util;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
    private static long nextEntryID = 0L;
    private final long tickEntryID = nextEntryID++;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public int blockID;
    public long delay;

    public NextTickListEntry(int x, int y, int z, int id) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.blockID = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }

        NextTickListEntry tickEntry = (NextTickListEntry)obj;
        return this.xCoord == tickEntry.xCoord && this.yCoord == tickEntry.yCoord && this.zCoord == tickEntry.zCoord && this.blockID == tickEntry.blockID;
    }

    @Override
    public int hashCode() {
        return (this.xCoord * 128 * 1024 + this.zCoord * 128 + this.yCoord) * 256 + this.blockID;
    }

    public NextTickListEntry setDelay(long time) {
        this.delay = time;
        return this;
    }

    public int compareTo(NextTickListEntry nextEntry) {
        return this.delay < nextEntry.delay
            ? -1
            : (this.delay > nextEntry.delay ? 1 : (this.tickEntryID < nextEntry.tickEntryID ? -1 : (this.tickEntryID > nextEntry.tickEntryID ? 1 : 0)));
    }
}
