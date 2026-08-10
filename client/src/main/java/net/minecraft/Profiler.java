package net.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profiler {
    public static boolean profilingEnabled = false;
    private static List<String> sectionList = new ArrayList<>();
    private static List<Long> timestampList = new ArrayList<>();
    private static String profilingSection = "";
    private static Map<String, Long> profilingMap = new HashMap<>();

    public static void clearProfiling() {
        profilingMap.clear();
    }

    public static void startSection(String sectionName) {
        if (profilingEnabled) {
            if (profilingSection.length() > 0) {
                profilingSection = profilingSection + ".";
            }

            profilingSection = profilingSection + sectionName;
            sectionList.add(profilingSection);
            timestampList.add(System.nanoTime());
        }
    }

    public static void endSection() {
        if (profilingEnabled && timestampList.size() > 0 && sectionList.size() > 0) {
            long sysTime = System.nanoTime();
            long nanoTime = timestampList.remove(timestampList.size() - 1);
            sectionList.remove(sectionList.size() - 1);
            long totalTime = sysTime - nanoTime;
            if (profilingMap.containsKey(profilingSection)) {
                profilingMap.put(profilingSection, profilingMap.get(profilingSection) + totalTime);
            } else {
                profilingMap.put(profilingSection, totalTime);
            }

            profilingSection = sectionList.size() > 0 ? sectionList.get(sectionList.size() - 1) : "";
        }
    }

    public static List<Profiler.ProfilerResult> getProfilingData(String sectionName) {
        if (!profilingEnabled) {
            return null;
        }

        long timer = profilingMap.containsKey("root") ? profilingMap.get("root") : 0L;
        long maxTime = profilingMap.containsKey(sectionName) ? profilingMap.get(sectionName) : -1L;
        ArrayList<Profiler.ProfilerResult> results = new ArrayList<>();
        if (sectionName.length() > 0) {
            sectionName = sectionName + ".";
        }

        long elapsedTime = 0L;

        for (String section : profilingMap.keySet()) {
            if (section.length() > sectionName.length() && section.startsWith(sectionName) && section.indexOf(".", sectionName.length() + 1) < 0) {
                elapsedTime += profilingMap.get(section);
            }
        }

        float deltaTicks = (float)elapsedTime;
        if (elapsedTime < maxTime) {
            elapsedTime = maxTime;
        }

        if (timer < elapsedTime) {
            timer = elapsedTime;
        }

        for (String sect : profilingMap.keySet()) {
            if (sect.length() > sectionName.length() && sect.startsWith(sectionName) && sect.indexOf(".", sectionName.length() + 1) < 0) {
                long time = profilingMap.get(sect);
                double last = time * 100.0 / elapsedTime;
                double next = time * 100.0 / timer;
                String finalSect = sect.substring(sectionName.length());
                results.add(new Profiler.ProfilerResult(finalSect, last, next));
            }
        }

        for (String sect : profilingMap.keySet()) {
            profilingMap.put(sect, profilingMap.get(sect) * 999L / 1000L);
        }

        if ((float)elapsedTime > deltaTicks) {
            results.add(
                new Profiler.ProfilerResult(
                    "unspecified", ((float)elapsedTime - deltaTicks) * 100.0 / elapsedTime, ((float)elapsedTime - deltaTicks) * 100.0 / timer
                )
            );
        }

        Collections.sort(results);
        results.add(0, new Profiler.ProfilerResult(sectionName, 100.0, elapsedTime * 100.0 / timer));
        return results;
    }

    public static void endStartSection(String section) {
        endSection();
        startSection(section);
    }

    public static final class ProfilerResult implements Comparable<Profiler.ProfilerResult> {
        public double sectionPercentage;
        public double globalPercentage;
        public String name;

        public ProfilerResult(String name, double sect, double global) {
            this.name = name;
            this.sectionPercentage = sect;
            this.globalPercentage = global;
        }

        public int compareTo(Profiler.ProfilerResult result) {
            return result.sectionPercentage < this.sectionPercentage
                ? -1
                : (result.sectionPercentage > this.sectionPercentage ? 1 : result.name.compareTo(this.name));
        }

        public int getDisplayColor() {
            return (this.name.hashCode() & 11184810) + 4473924;
        }
    }
}
