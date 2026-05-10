package com.marija.middleearthbattle.rmi;

import com.marija.middleearthbattle.model.Faction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameLobbyRemoteService extends Remote {

    void registerPlayer(String playerName, Faction faction) throws RemoteException;

    List<PlayerInfo> getPlayers() throws RemoteException;

    void sendMessage(String senderName, String content) throws RemoteException;

    List<ChatMessage> getMessages() throws RemoteException;
}