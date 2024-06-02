package main.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int MIDDLE_BOUNDARY = 100; // Bound for middle area

    private static Map<Integer, PrintWriter> writers = new HashMap<>();
    private static Map<Integer, String> positions = new HashMap<>();
    private static Map<Integer, String> enemyPositions = new HashMap<>();
    private static ScheduledExecutorService enemyMovementScheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledExecutorService targetChangeScheduler = Executors.newScheduledThreadPool(1);
    private static Map<Integer, Integer> enemyTargets = new HashMap<>(); // Enemy targets

    public static void main(String[] args) throws Exception {
        System.out.println("The game server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(58901);
        try {
            // Spawn enemies
            spawnEnemies(5);  // Example: spawn 5 enemies

            // Schedule enemy movement
            scheduleEnemyMovement();

            // Schedule target player change
            scheduleTargetChange();

            while (true) {
                new Handler(listener.accept(), clientNumber++, false).start();
            }
        } finally {
            listener.close();
            enemyMovementScheduler.shutdown();
            targetChangeScheduler.shutdown();
        }
    }

    private static void spawnEnemies(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int enemyId = -i - 1; // Negative IDs for enemies
            String position = generateRandomPosition();
            enemyPositions.put(enemyId, position);
            for (PrintWriter writer : writers.values()) {
                writer.println("ENEMY " + enemyId + " " + position);
            }
            enemyTargets.put(enemyId, null); // Initialize target to null
        }
    }

    private static String generateRandomPosition() {
        Random rand = new Random();
        int x = rand.nextInt(SCREEN_WIDTH);
        int y = rand.nextInt(SCREEN_HEIGHT);
        return x + "," + y;
    }

    private static void scheduleEnemyMovement() {
        enemyMovementScheduler.scheduleAtFixedRate(() -> {
            moveEnemies();
        }, 0, 50, TimeUnit.MILLISECONDS); // Move enemies every 50 milliseconds
    }

    private static void scheduleTargetChange() {
        targetChangeScheduler.scheduleAtFixedRate(() -> {
            changeTargets();
        }, 0, 5, TimeUnit.SECONDS); // Change targets every 5 seconds
    }

    private static void changeTargets() {
        Random rand = new Random();
        List<Integer> playerIds = new ArrayList<>(positions.keySet());
        if (playerIds.isEmpty()) return; // No players to target

        for (Integer enemyId : enemyPositions.keySet()) {
            int targetPlayer = playerIds.get(rand.nextInt(playerIds.size()));
            enemyTargets.put(enemyId, targetPlayer);
        }
    }

    private static void moveEnemies() {
        Random rand = new Random();
        for (Map.Entry<Integer, String> entry : enemyPositions.entrySet()) {
            int enemyId = entry.getKey();
            String[] pos = entry.getValue().split(",");
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);

            Integer targetPlayer = enemyTargets.get(enemyId);
            if (targetPlayer != null && positions.containsKey(targetPlayer)) {
                String[] targetPos = positions.get(targetPlayer).split(",");
                int targetX = Integer.parseInt(targetPos[0]);
                int targetY = Integer.parseInt(targetPos[1]);

                // Move towards the target player
                if (x < targetX) x++;
                if (x > targetX) x--;
                if (y < targetY) y++;
                if (y > targetY) y--;
            } else {
                // Random move if no target
                x += rand.nextInt(7) - 3; // Move -3 to +3 pixels
                y += rand.nextInt(7) - 3; // Move -3 to +3 pixels
            }

            // Bound the movement within screen
            x = Math.max(0, Math.min(SCREEN_WIDTH, x));
            y = Math.max(0, Math.min(SCREEN_HEIGHT, y));

            enemyPositions.put(enemyId, x + "," + y);
            for (PrintWriter writer : writers.values()) {
                writer.println("ENEMY " + enemyId + " " + x + "," + y);
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private int clientNumber;
        private boolean isEnemy;

        public Handler(Socket socket, int clientNumber, boolean isEnemy) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.isEnemy = isEnemy;
            if (isEnemy) {
                System.out.println("New enemy with ID #" + clientNumber + " connected.");
            } else {
                System.out.println("New player with client #" + clientNumber + " connected.");
            }
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Inform the client of its ID
                out.println("YOURID " + clientNumber);
                writers.put(clientNumber, out);

                // Send initial enemy positions to the new client
                for (Map.Entry<Integer, String> entry : enemyPositions.entrySet()) {
                    out.println("ENEMY " + entry.getKey() + " " + entry.getValue());
                }

                String input;
                while ((input = in.readLine()) != null) {
                    if (isEnemy) {
                        enemyPositions.put(clientNumber, input);
                    } else {
                        positions.put(clientNumber, input);
                    }
                    for (PrintWriter writer : writers.values()) {
                        writer.println((isEnemy ? "ENEMY " : "PLAYER ") + clientNumber + " " + input);
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
                if (isEnemy) {
                    enemyPositions.remove(clientNumber);
                } else {
                    positions.remove(clientNumber);
                }
                System.out.println((isEnemy ? "Enemy " : "Player ") + "#" + clientNumber + " has disconnected.");
            }
        }
    }
}
