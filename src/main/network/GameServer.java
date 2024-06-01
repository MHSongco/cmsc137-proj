package main.network;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static Map<Integer, PrintWriter> writers = new HashMap<>();
    private static Map<Integer, String> positions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The game server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(58901);
        try {
            while (true) {
                new Handler(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private int clientNumber;

        public Handler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New player with client #" + clientNumber + " connected.");
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Inform the client of its ID
                out.println("YOURID " + clientNumber);
                writers.put(clientNumber, out);

                String input;
                while ((input = in.readLine()) != null) {
                    positions.put(clientNumber, input);
                    for (PrintWriter writer : writers.values()) {
                        writer.println("PLAYER " + clientNumber + " " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client #" + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writers.remove(clientNumber);
                positions.remove(clientNumber);
                System.out.println("Player #" + clientNumber + " has disconnected.");
            }
        }
    }
}