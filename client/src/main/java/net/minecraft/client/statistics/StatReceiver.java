package net.minecraft.client.statistics;

class StatReceiver extends Thread {
    final StatsSyncher statSynchronizer;

    StatReceiver(StatsSyncher syncher) {
        this.statSynchronizer = syncher;
    }

    @Override
    public void run() {
        try {
            if (StatsSyncher.getStatisticsMap(this.statSynchronizer) != null) {
                StatsSyncher.checkFiles(
                    this.statSynchronizer,
                    StatsSyncher.getStatisticsMap(this.statSynchronizer),
                    StatsSyncher.getFileData(this.statSynchronizer),
                    StatsSyncher.getTempData(this.statSynchronizer),
                    StatsSyncher.getOldData(this.statSynchronizer)
                );
            } else if (StatsSyncher.getFileData(this.statSynchronizer).exists()) {
                StatsSyncher.getStatisticsMap(
                    this.statSynchronizer,
                    StatsSyncher.compareFiles(
                        this.statSynchronizer,
                        StatsSyncher.getFileData(this.statSynchronizer),
                        StatsSyncher.getTempData(this.statSynchronizer),
                        StatsSyncher.getOldData(this.statSynchronizer)
                    )
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            StatsSyncher.isSynchronizing(this.statSynchronizer, false);
        }
    }
}
