package net.minecraft.client.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class StatsSyncher {
    private volatile Map<?, ?> statisticsMap = null;
    private volatile Map<?, ?> achievementsMap = null;
    private StatWriter fileWriter;
    private File data;
    private File tempData;
    private File oldData;
    private String name = "";
    private int syncTime = 0;
    private int waitTime = 0;
    private volatile boolean syncing = false;

    public StatsSyncher(StatWriter writer, File file) {
        this.data = new File(file, "stats_char_unsent.dat");
        this.oldData = new File(file, "stats_char_unsent.old");
        this.tempData = new File(file, "stats_char_unsent.tmp");
        this.writeStatsFile(file, "stats_char_unsent.dat", this.data);
        this.writeStatsFile(file, "stats_char_unsent.old", this.oldData);
        this.writeStatsFile(file, "stats_char_unsent.tmp", this.tempData);
        this.fileWriter = writer;
        if (this.data.exists()) {
            writer.loadStats(this.fileComparator(this.data, this.tempData, this.oldData));
        }

        this.fetchStats();
    }

    private void writeStatsFile(File file, String path, File oldFile) {
        File newFile = new File(file, path);
        if (newFile.exists() && !newFile.isDirectory() && !oldFile.exists()) {
            newFile.renameTo(oldFile);
        }
    }

    private Map<?, ?> fileComparator(File data, File temp, File old) {
        return data.exists()
            ? this.getLocalStatsFile(data)
            : (old.exists() ? this.getLocalStatsFile(old) : (temp.exists() ? this.getLocalStatsFile(temp) : null));
    }

    private Map<?, ?> getLocalStatsFile(File file) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String path = "";
            StringBuilder sb = new StringBuilder();

            while ((path = reader.readLine()) != null) {
                sb.append(path);
            }

            return StatWriter.saveToFile(sb.toString());
        } catch (Exception var16) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception var15) {
                }
            }
        }

        return null;
    }

    private void checksumFile(Map<?, ?> map, File data, File temp, File old) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(temp, false));

        try {
            writer.print(StatWriter.generateMD5(this.name, "local", map));
        } finally {
            writer.close();
        }

        if (old.exists()) {
            old.delete();
        }

        if (data.exists()) {
            data.renameTo(old);
        }

        temp.renameTo(data);
    }

    public void fetchStats() {
        if (this.syncing) {
            throw new IllegalStateException("Can't get stats from server while StatsSyncher is busy!");
        }

        this.syncTime = 100;
        this.syncing = true;
        new StatReceiver(this).start();
    }

    public void saveStats(Map map) {
        if (this.syncing) {
            throw new IllegalStateException("Can't save stats while StatsSyncher is busy!");
        }

        this.syncTime = 100;
        this.syncing = true;
        new StatSender(this, map).start();
    }

    public void syncStats(Map<?, ?> map) {
        int attempts = 30;

        while (this.syncing) {
            if (--attempts <= 0) {
                break;
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException var10) {
            }
        }

        this.syncing = true;

        try {
            this.checksumFile(map, this.data, this.tempData, this.oldData);
        } catch (Exception var8) {
        } finally {
            this.syncing = false;
        }
    }

    public boolean isSynchronizing() {
        return this.syncTime <= 0 && !this.syncing && this.achievementsMap == null;
    }

    public void saveAllStats() {
        if (this.syncTime > 0) {
            this.syncTime--;
        }

        if (this.waitTime > 0) {
            this.waitTime--;
        }

        if (this.achievementsMap != null) {
            this.fileWriter.saveAchievements(this.achievementsMap);
            this.achievementsMap = null;
        }

        if (this.statisticsMap != null) {
            this.fileWriter.saveStats(this.statisticsMap);
            this.statisticsMap = null;
        }
    }

    static Map<?, ?> getStatisticsMap(StatsSyncher syncher) {
        return syncher.statisticsMap;
    }

    static File getFileData(StatsSyncher syncher) {
        return syncher.data;
    }

    static File getTempData(StatsSyncher syncher) {
        return syncher.tempData;
    }

    static File getOldData(StatsSyncher syncher) {
        return syncher.oldData;
    }

    static void checkFiles(StatsSyncher syncher, Map<?, ?> map, File data, File temp, File old) throws IOException {
        syncher.checksumFile(map, data, temp, old);
    }

    static Map<?, ?> getStatisticsMap(StatsSyncher syncher, Map<?, ?> map) {
        return syncher.statisticsMap = map;
    }

    static Map<?, ?> compareFiles(StatsSyncher syncher, File data, File temp, File old) {
        return syncher.fileComparator(data, temp, old);
    }

    static boolean isSynchronizing(StatsSyncher syncher, boolean state) {
        return syncher.syncing = state;
    }
}
