package com.marija.middleearthbattle.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiLobbyServer {

    private final int port;
    private final String serviceName;
    private Registry registry;
    private GameLobbyRemoteServiceImpl service;
    private boolean running;

    public RmiLobbyServer(int port, String serviceName) {
        this.port = port;
        this.serviceName = serviceName;
    }

    public void start() throws RemoteException {
        if (running) {
            return;
        }

        registry = createOrGetRegistry();
        service = new GameLobbyRemoteServiceImpl();

        try {
            registry.bind(serviceName, service);
        } catch (AlreadyBoundException exception) {
            registry.rebind(serviceName, service);
        }

        running = true;
    }

    public void stop() {
        if (!running) {
            return;
        }

        try {
            registry.unbind(serviceName);
        } catch (RemoteException | NotBoundException exception) {
            throw new IllegalStateException(exception);
        }

        try {
            UnicastRemoteObject.unexportObject(service, true);
        } catch (Exception ignored) {
        }

        running = false;
    }

    private Registry createOrGetRegistry() throws RemoteException {
        try {
            return LocateRegistry.createRegistry(port);
        } catch (RemoteException exception) {
            return LocateRegistry.getRegistry(port);
        }
    }
}