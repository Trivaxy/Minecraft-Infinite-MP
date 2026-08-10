package net.minecraft.client.statistics;

import java.util.Map;

class StatSender extends Thread {
    final Map<StatsSyncher, ?> synchronizers;
    final StatsSyncher statSynchronizer;

    StatSender(StatsSyncher syncher, Map<StatsSyncher, ?> synchMap) {
        this.statSynchronizer = syncher;
        this.synchronizers = synchMap;
    }

    @Override
    public void run() {
        try {
            StatsSyncher.checkFiles(
                this.statSynchronizer,
                this.synchronizers,
                StatsSyncher.getFileData(this.statSynchronizer),
                StatsSyncher.getTempData(this.statSynchronizer),
                StatsSyncher.getOldData(this.statSynchronizer)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            StatsSyncher.isSynchronizing(this.statSynchronizer, false);
        }
    }
}
