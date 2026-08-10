package net.minecraft.client;

public class Timer {
    float ticksPerSecond;
    private double lastHRTime;
    public int elapsedTicks;
    public float renderPartialTicks;
    public float timerSpeed = 1.0F;
    public float elapsedPartialTicks = 0.0F;
    private long lastSyncSysClock;
    private long lastSyncHRClock;
    private double timeSyncAdjustment = 1.0;

    public Timer(float tickRate) {
        this.ticksPerSecond = tickRate;
        this.lastSyncSysClock = System.currentTimeMillis();
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer() {
        long systemTime = System.currentTimeMillis();
        long currentTime = systemTime - this.lastSyncSysClock;
        long totalTimeElapsed = System.nanoTime() / 1000000L;
        if (currentTime > 1000L) {
            long totalHourPassed = totalTimeElapsed - this.lastSyncHRClock;
            double hourValue = (double)currentTime / totalHourPassed;
            this.timeSyncAdjustment = this.timeSyncAdjustment + (hourValue - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncSysClock = systemTime;
            this.lastSyncHRClock = totalTimeElapsed;
        }

        if (currentTime < 0L) {
            this.lastSyncSysClock = systemTime;
            this.lastSyncHRClock = totalTimeElapsed;
        }

        double minute = totalTimeElapsed / 1000.0;
        double hourValue = (minute - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = minute;
        if (hourValue < 0.0) {
            hourValue = 0.0;
        }

        if (hourValue > 1.0) {
            hourValue = 1.0;
        }

        this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + hourValue * this.timerSpeed * this.ticksPerSecond);
        this.elapsedTicks = (int)this.elapsedPartialTicks;
        this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
        if (this.elapsedTicks > 10) {
            this.elapsedTicks = 10;
        }

        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}
