package com.marija.middleearthbattle.network;

import com.marija.middleearthbattle.move.GameMove;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NetworkMoveService {

    private final GameMoveClient gameMoveClient = new GameMoveClient();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.setName("game-move-client-thread");
        return thread;
    });

    public void sendMove(String host, int port, GameMove gameMove, Consumer<GameMove> onSuccess, Consumer<Throwable> onFailure) {
        CompletableFuture.runAsync(() -> {
            try {
                gameMoveClient.send(host, port, gameMove);
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, executorService).thenRun(() -> onSuccess.accept(gameMove)).exceptionally(throwable -> {
            onFailure.accept(throwable);
            return null;
        });
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}