package src.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import src.model.db.DatabaseUpdater;

public class ThreadPoolController {

    private final ThreadPoolController threadPoolManager = new ThreadPoolController(); // Inizializza il gestore del thread pool
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(); // Inizializza il database updater con il gestore del thread pool
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

    public void startDatabaseSync() {
        databaseUpdater.sincronizzaDalDatabase();
        databaseUpdater.avviaSincronizzazioneConSleep();
    }

    // Metodo per arrestare tutti i thread pool registrati
    public void shutdownAll() {
        databaseUpdater.arrestaSincronizzazioneConSleep();
        threadPoolManager.shutdownAll();
    }

    public static ThreadPoolController getInstance() {
        return new ThreadPoolController();
    }
}
