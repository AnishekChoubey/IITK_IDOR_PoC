import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IdorPollingSystem<RESPONSE> {

    public interface Pollable<RESPONSE>{
        RESPONSE fetchStockDataFromServer(int idorId);
        boolean shouldStop(int idorId, RESPONSE data);
    }
    private final AtomicInteger currentIdorId;
    private final int maxIdorId;
    private final int threadCount;
    private ExecutorService executorPool;
    private volatile boolean isRunning = false;

    private final Pollable<RESPONSE> pollable;
    public IdorPollingSystem(int startIdorId, int maxIdorId, int threadCount, Pollable<RESPONSE> pollable) {
        this.currentIdorId = new AtomicInteger(startIdorId);
        this.maxIdorId = maxIdorId;
        this.threadCount = threadCount;
        this.pollable = pollable;
    }

    public synchronized void startPolling() throws Exception {
        if (isRunning) return;
        isRunning = true;

        executorPool = Executors.newFixedThreadPool(threadCount);
        System.out.println("Starting polling system with " + threadCount + " threads from ID: " + currentIdorId.get());

        for (int i = 0; i < threadCount; i++) {
            executorPool.submit(new PollingWorker(pollable));
        }
    }

    public synchronized void stopPolling() {
        if (!isRunning) return;
        isRunning = false;

        System.out.println("\n[Shutting Down...] Initiating graceful stop.");
        executorPool.shutdown();
        try {
            if (!executorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                List<Runnable> droppedTasks = executorPool.shutdownNow();
                System.out.println("Forced shutdown applied. Tasks dropped: " + droppedTasks.size());
            }
        } catch (InterruptedException e) {
            executorPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("----------------------------------------");
        System.out.println("POLLING HALTED");
        System.out.println("Next available ID to poll would be: " + currentIdorId.get());
        System.out.println("Highest potential completed ID: " + (currentIdorId.get() - 1));
        System.out.println("--------------------------------------------");
    }
    private class PollingWorker implements Runnable {
        private PollingWorker(Pollable<RESPONSE> pollable) {
            this.pollable = pollable;
        }

        @Override
        public void run() {
            while (isRunning && currentIdorId.get() <= maxIdorId) {
                int idorId = currentIdorId.getAndIncrement();
                if (idorId > maxIdorId) {
                    break;
                }
                try {
                    RESPONSE stockData = fetchStockDataFromServer(idorId);
                    if (criticalPointDetector(idorId,stockData)) {
                        System.err.printf("[ALERTER] CRITICAL SIGNAL DETECTED -> ID: %d | Data: %s%n", idorId, stockData);
                    }

                } catch (Exception e) {
                    System.out.println("Failed to poll ID: " + idorId + " Due to: " + e.getMessage());
                }
            }
        }
        private final Pollable<RESPONSE> pollable;



        private RESPONSE fetchStockDataFromServer(int idorId) throws Exception {
            return pollable.fetchStockDataFromServer(idorId);
        }

        private boolean criticalPointDetector(int idorId, RESPONSE data) {
            return pollable.shouldStop(idorId,data);
        }
    }

}