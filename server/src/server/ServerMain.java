package server;

import server.net.BlackjackServer;

public class ServerMain {
    public static void main(String[] args) {
        int port = 5555;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        BlackjackServer server = new BlackjackServer(port);
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Falha ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
