package com.marija.middleearthbattle.rmi;

import com.marija.middleearthbattle.model.Faction;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameLobbyRemoteServiceImpl extends UnicastRemoteObject implements GameLobbyRemoteService {

    private final List<PlayerInfo> players;
    private final List<ChatMessage> messages;

    public GameLobbyRemoteServiceImpl() throws RemoteException {
        this.players = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    @Override
    public synchronized void registerPlayer(String playerName, Faction faction) {
        boolean alreadyRegistered = players.stream()
                .anyMatch(player -> player.playerName().equals(playerName));

        if (!alreadyRegistered) {
            players.add(new PlayerInfo(playerName, faction, LocalDateTime.now()));
        }
    }

    @Override
    public synchronized List<PlayerInfo> getPlayers() {
        return List.copyOf(players);
    }

    @Override
    public synchronized void sendMessage(String senderName, String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        messages.add(new ChatMessage(senderName, content.trim(), LocalDateTime.now()));
    }

    @Override
    public synchronized List<ChatMessage> getMessages() {
        return List.copyOf(messages);
    }
}