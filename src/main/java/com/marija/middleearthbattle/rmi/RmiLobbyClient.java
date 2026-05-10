package com.marija.middleearthbattle.rmi;

import com.marija.middleearthbattle.model.Faction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

public class RmiLobbyClient {

    private final GameLobbyRemoteService remoteService;

    public RmiLobbyClient(String host, int port, String serviceName) throws NamingException {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        environment.put(Context.PROVIDER_URL, "rmi://" + host + ":" + port);

        Context context = new InitialContext(environment);
        remoteService = (GameLobbyRemoteService) context.lookup(serviceName);
    }

    public void registerPlayer(String playerName, Faction faction) throws RemoteException {
        remoteService.registerPlayer(playerName, faction);
    }

    public List<PlayerInfo> getPlayers() throws RemoteException {
        return remoteService.getPlayers();
    }

    public void sendMessage(String senderName, String content) throws RemoteException {
        remoteService.sendMessage(senderName, content);
    }

    public List<ChatMessage> getMessages() throws RemoteException {
        return remoteService.getMessages();
    }
}