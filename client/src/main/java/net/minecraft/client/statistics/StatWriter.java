package net.minecraft.client.statistics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.statistics.MD5Hash;
import net.minecraft.statistics.Stat;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.Achievement;
import org.json.JSONObject;

public class StatWriter {
    private Map<Stat, Object> statsMap = new HashMap<>();
    private Map<Stat, Object> achievementMap = new HashMap<>();
    private StatsSyncher syncher;
    private boolean syncing = false;

    public StatWriter(File file) {
        File statsFolder = new File(file, "stats");
        if (!statsFolder.exists()) {
            statsFolder.mkdir();
        }

        for (File innerFile : file.listFiles()) {
            if (innerFile.getName().startsWith("stats_") && innerFile.getName().endsWith(".dat")) {
                File statsFile = new File(statsFolder, innerFile.getName());
                if (!statsFile.exists()) {
                    System.out.println("Relocating " + innerFile.getName());
                    innerFile.renameTo(statsFile);
                }
            }
        }

        this.syncher = new StatsSyncher(this, statsFolder);
    }

    public void writeStat(Stat stat, int value) {
        this.writeStatToMap(this.achievementMap, stat, value);
        this.writeStatToMap(this.statsMap, stat, value);
        this.syncing = true;
    }

    public void removeStat(Stat stat) {
        this.achievementMap.remove(stat);
        this.statsMap.remove(stat);
        this.syncing = true;
    }

    private void writeStatToMap(Map<Stat, Object> statsMap, Stat stat, int value) {
        Integer val = (Integer)statsMap.get(stat);
        int amount = val == null ? 0 : val;
        statsMap.put(stat, amount + value);
    }

    public Map<Stat, Object> getAchievementMap() {
        return new HashMap<>(this.achievementMap);
    }

    public void loadStats(Map<?, ?> map) {
        if (map != null) {
            this.syncing = true;

            for (Object key : map.keySet()) {
                Stat stat = (Stat)key;
                this.writeStatToMap(this.achievementMap, stat, (Integer)map.get(stat));
                this.writeStatToMap(this.statsMap, stat, (Integer)map.get(stat));
            }
        }
    }

    public void saveStats(Map<?, ?> map) {
        if (map != null) {
            for (Object key : map.keySet()) {
                Stat stat = (Stat)key;
                Integer val = (Integer)this.achievementMap.get(stat);
                int value = val == null ? 0 : val;
                this.statsMap.put(stat, (Integer)map.get(stat) + value);
            }
        }
    }

    public void saveAchievements(Map<?, ?> map) {
        if (map != null) {
            this.syncing = true;

            for (Object key : map.keySet()) {
                Stat stat = (Stat)key;
                this.writeStatToMap(this.achievementMap, stat, (Integer)map.get(stat));
            }
        }
    }

    public static Map<Stat, Integer> saveToFile(String name) {
        HashMap<Stat, Integer> stats = new HashMap<>();

        try {
            String sesid = "local";
            StringBuilder sb = new StringBuilder();
            JSONObject root = new JSONObject(name);

            for (Object obj : root.getJSONArray("stats-change")) {
                Map<String, Object> map = ((JSONObject)obj).toMap();
                Entry<String, Object> mapEntry = map.entrySet().iterator().next();
                int statid = Integer.parseInt(mapEntry.getKey());
                int statcnt = Integer.parseInt(mapEntry.getValue().toString());
                Stat stat = StatList.getOneShotStat(statid);
                if (stat == null) {
                    System.out.println(statid + " is not a valid stat");
                } else {
                    sb.append(statcnt).append(",");
                    stats.put(stat, statcnt);
                }
            }

            MD5Hash hash = new MD5Hash(sesid);
            String hashCode = hash.hash(sb.toString());
            if (!hashCode.equals(root.getString("checksum"))) {
                System.out.println("CHECKSUM MISMATCH");
            }
        } catch (NumberFormatException nfe) {
            throw nfe;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stats;
    }

    public static String generateMD5(String name, String sesh, Map<?, ?> map2) {
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        boolean validityCheck = true;
        builder1.append("{\r\n");
        if (name != null && sesh != null) {
            builder1.append("  \"user\":{\r\n");
            builder1.append("    \"name\":\"").append(name).append("\",\r\n");
            builder1.append("    \"sessionid\":\"").append(sesh).append("\"\r\n");
            builder1.append("  },\r\n");
        }

        builder1.append("  \"stats-change\":[");

        for (Object key : map2.keySet()) {
            Stat stat = (Stat)key;
            if (!validityCheck) {
                builder1.append("},");
            } else {
                validityCheck = false;
            }

            builder1.append("\r\n    {\"").append(stat.statId).append("\":").append(map2.get(stat));
            builder2.append(map2.get(stat)).append(",");
        }

        if (!validityCheck) {
            builder1.append("}");
        }

        MD5Hash hashCode = new MD5Hash(sesh);
        builder1.append("\r\n  ],\r\n");
        builder1.append("  \"checksum\":\"").append(hashCode.hash(builder2.toString())).append("\"\r\n");
        builder1.append("}");
        return builder1.toString();
    }

    public boolean hasAchievementUnlocked(Achievement achievement) {
        return this.statsMap.containsKey(achievement);
    }

    public boolean canUnlockAchievement(Achievement achievement) {
        return achievement.parentAchievement == null || this.hasAchievementUnlocked(achievement.parentAchievement);
    }

    public int getStatCount(Stat stat) {
        Integer count = (Integer)this.statsMap.get(stat);
        return count == null ? 0 : count;
    }

    public void syncStats() {
        this.syncher.syncStats(this.getAchievementMap());
    }

    public void synchronizeStats() {
        if (this.syncing && this.syncher.isSynchronizing()) {
            this.syncher.saveStats(this.getAchievementMap());
        }

        this.syncher.saveAllStats();
    }
}
