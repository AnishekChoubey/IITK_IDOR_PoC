import java.util.List;
import java.util.concurrent.*;

public class DynamicPollingSystem<REQ, RES> {

    public interface PollingStrategy<REQ, RES> {
        RES fetchAndDeserialize(REQ requestParam) throws Exception;
        void processResult(REQ requestParam, RES responseData);
        void handleFailure(REQ requestParam, Exception e);
    }

    private final BlockingQueue<REQ> taskQueue;
    private final PollingStrategy<REQ, RES> strategy;
    private final int threadCount;
    private ExecutorService executorPool;
    private volatile boolean isRunning = false;

    public DynamicPollingSystem(PollingStrategy<REQ, RES> strategy, int threadCount) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.strategy = strategy;
        this.threadCount = threadCount;
    }
   public void submitTask(REQ task) {
        taskQueue.offer(task);
        System.out.println("[Queue] Added new task. Pending size: " + taskQueue.size());
    }

    public void submitTasks(List<REQ> tasks) {
        taskQueue.addAll(tasks);
        System.out.println("[Queue] Added batch of " + tasks.size() + ". Pending size: " + taskQueue.size());
    }

    public void clearPendingTasks() {
        taskQueue.clear();
        System.out.println("[Queue] Purged! All pending tasks removed.");
    }

    public int getPendingTaskCount() {
        return taskQueue.size();
    }
    public synchronized void startSystem() {
        if (isRunning) return;
        isRunning = true;

        executorPool = Executors.newFixedThreadPool(threadCount);
        System.out.println("Dynamic System ONLINE. " + threadCount + " workers waiting for tasks...");

        for (int i = 0; i < threadCount; i++) {
            executorPool.submit(new DaemonWorker());
        }
    }

    public synchronized void stopSystem() {
        if (!isRunning) return;
        isRunning = false;

        System.out.println("\n[Shutting Down...] Initiating graceful stop.");
        executorPool.shutdown();

        try {
            if (!executorPool.awaitTermination(3, TimeUnit.SECONDS)) {
                executorPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("System HALTED. Unprocessed tasks discarded: " + taskQueue.size());
    }
    private class DaemonWorker implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                     REQ currentRequest = taskQueue.poll(1, TimeUnit.SECONDS);

                     if (currentRequest == null) {
                        continue; 
                    }

                    RES responseData = strategy.fetchAndDeserialize(currentRequest);
                    strategy.processResult(currentRequest, responseData);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("[TRACE] Polling Error: " + e.getMessage());
                }
            }
        }
    }
}