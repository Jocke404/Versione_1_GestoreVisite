package src.it.unibs.ingsw.gestvisit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private final List<ExecutorService> threadPools = new ArrayList<>();

    // Metodo per creare un nuovo thread pool e registrarlo
    public ExecutorService createThreadPool(int poolSize) {
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        threadPools.add(executorService);
        return executorService;
    }

    // Metodo per creare un single-threaded executor e registrarlo
    public ExecutorService createSingleThreadExecutor() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        threadPools.add(executorService);
        return executorService;
    }

    // Metodo per arrestare tutti i thread pool registrati
    public void shutdownAll() {
        for (ExecutorService executorService : threadPools) {
            executorService.shutdown();
            try {
                com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Forzando la chiusura di un thread pool...");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Tutti i thread pool sono stati arrestati.");
    }
}
