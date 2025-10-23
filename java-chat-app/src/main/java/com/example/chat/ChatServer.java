package com.example.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple multi-client chat server using plain TCP sockets.
 *
 * Protocol:
 * - First line from client must be: "JOIN <name>"
 * - Subsequent lines are treated as chat messages and broadcast to all clients
 * - Client can send "/quit" to disconnect gracefully
 */
public final class ChatServer {

    private final int port;

    private final Set<ClientHandler> connectedClients = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ChatServer listening on port " + port + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                connectedClients.add(handler);
                Thread thread = new Thread(handler, "client-" + socket.getPort());
                thread.start();
            }
        }
    }

    private void broadcast(String message) {
        synchronized (connectedClients) {
            for (ClientHandler client : connectedClients) {
                client.send(message);
            }
        }
    }

    private final class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String displayName = "Anonymous";

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Expect: JOIN <name>
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.startsWith("JOIN ") && firstLine.length() > 5) {
                    displayName = firstLine.substring(5).trim();
                }

                broadcast("[system] " + displayName + " joined the chat.");

                String line;
                while ((line = reader.readLine()) != null) {
                    if ("/quit".equalsIgnoreCase(line.trim())) {
                        break;
                    }
                    if (!line.isBlank()) {
                        broadcast(displayName + ": " + line);
                    }
                }
            } catch (IOException ignored) {
                // fall-through to cleanup
            } finally {
                cleanup();
            }
        }

        void send(String message) {
            if (writer != null) {
                writer.println(message);
            }
        }

        private void cleanup() {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) { }
            if (writer != null) {
                writer.close();
            }
            try {
                socket.close();
            } catch (IOException ignored) { }

            connectedClients.remove(this);
            broadcast("[system] " + displayName + " left the chat.");
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port. Using default 5000.");
            }
        }

        try {
            new ChatServer(port).start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
