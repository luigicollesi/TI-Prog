package server.net;

import server.game.TableManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlackjackServer {
    private final int port;
    private final TableManager tableManager = new TableManager();
    private final Set<ClientHandler> handlers = Collections.synchronizedSet(new HashSet<>());

    private volatile boolean running = true;

    public BlackjackServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Blackjack server ouvindo na porta " + port);

            while (running) {
                Socket client = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, this, tableManager);
                handlers.add(handler);
                Thread thread = new Thread(handler);
                thread.setName("Client-" + client.getInetAddress());
                thread.start();
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void removeHandler(ClientHandler handler) {
        handlers.remove(handler);
    }
}
