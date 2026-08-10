package net.minecraft.util.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable {
    public static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
    private List<IThreadedFileIO> threadedIOQueue = Collections.synchronizedList(new ArrayList<>());
    private volatile long writeQueuedCounter = 0L;
    private volatile long savedIOCounter = 0L;
    private volatile boolean isThreadWaiting = false;

    private ThreadedFileIOBase() {
        Thread thread = new Thread(this, "File IO Thread");
        thread.setPriority(1);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            this.processQueue();
        }
    }

    private void processQueue() {
        for (int i = 0; i < this.threadedIOQueue.size(); i++) {
            IThreadedFileIO ithreadedfileio = this.threadedIOQueue.get(i);
            boolean flag = ithreadedfileio.writeNextIO();
            if (!flag) {
                this.threadedIOQueue.remove(i--);
                this.savedIOCounter++;
            }

            try {
                if (!this.isThreadWaiting) {
                    Thread.sleep(10L);
                } else {
                    Thread.sleep(0L);
                }
            } catch (InterruptedException interruptedexception1) {
                interruptedexception1.printStackTrace();
            }
        }

        if (this.threadedIOQueue.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }
        }
    }

    public void queueIO(IThreadedFileIO par1IThreadedFileIO) {
        if (!this.threadedIOQueue.contains(par1IThreadedFileIO)) {
            this.writeQueuedCounter++;
            this.threadedIOQueue.add(par1IThreadedFileIO);
        }
    }

    public void waitForFinish() throws InterruptedException {
        this.isThreadWaiting = true;

        while (this.writeQueuedCounter != this.savedIOCounter) {
            Thread.sleep(10L);
        }

        this.isThreadWaiting = false;
    }
}
