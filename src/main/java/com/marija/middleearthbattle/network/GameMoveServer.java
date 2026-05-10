package com.marija.middleearthbattle.network;

import com.marija.middleearthbattle.move.GameMove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GameMoveServer {

    private final int port;
    private final Consumer<GameMove> onMoveReceived;
    private final Consumer<String> onServerMessage;
    private final ExecutorService executorService;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public GameMoveServer(int port, Consumer<GameMove> onMoveReceived, Consumer<String> onServerMessage) {
        this.port = port;
        this.onMoveReceived = onMoveReceived;
        this.onServerMessage = onServerMessage;
        this.executorService = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setName("game-move-server-thread");
            return thread;
        });
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        executorService.submit(this::listen);
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        executorService.shutdownNow();
    }

    private void listen() {
        try (ServerSocket openedServerSocket = new ServerSocket()) {
            openedServerSocket.setReuseAddress(true);
            openedServerSocket.bind(new InetSocketAddress(port));
            serverSocket = openedServerSocket;

            onServerMessage.accept("TCP server started on port " + port + ".");

            while (running) {
                try (
                        Socket socket = openedServerSocket.accept();
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())
                ) {
                    Object receivedObject = objectInputStream.readObject();

                    if (receivedObject instanceof GameMove gameMove) {
                        onMoveReceived.accept(gameMove);
                    }
                } catch (IOException | ClassNotFoundException exception) {
                    if (running) {
                        onServerMessage.accept("TCP move could not be received.");
                    }
                }
            }
        } catch (IOException exception) {
            if (running) {
                onServerMessage.accept("TCP server could not be started on port " + port + ".");
            }
        }
    }
}