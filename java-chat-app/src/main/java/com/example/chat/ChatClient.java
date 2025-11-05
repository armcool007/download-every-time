package com.example.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple console chat client.
 *
 * Usage: java -cp target/java-chat-app-1.0.0.jar com.example.chat.ChatClient <host> <port> <name>
 */
public final class ChatClient {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        String name = args.length > 2 ? args[2] : "User" + System.currentTimeMillis();

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {

            out.println("JOIN " + name);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {
                }
            });

            System.out.println("Connected as '" + name + "'. Type /quit to exit.");
            String input;
            while ((input = stdin.readLine()) != null) {
                if ("/quit".equalsIgnoreCase(input.trim())) {
                    out.println("/quit");
                    break;
                }
                out.println(input);
            }

            executor.shutdownNow();
        }
    }
}
