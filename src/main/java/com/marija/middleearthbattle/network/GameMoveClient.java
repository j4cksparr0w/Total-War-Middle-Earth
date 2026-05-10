package com.marija.middleearthbattle.network;

import com.marija.middleearthbattle.move.GameMove;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameMoveClient {

    public void send(String host, int port, GameMove gameMove) throws IOException {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            objectOutputStream.writeObject(gameMove);
            objectOutputStream.flush();
        }
    }
}