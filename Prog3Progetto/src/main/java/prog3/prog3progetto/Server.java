package prog3.prog3progetto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 12345;
    private static final Set<String> VALID_EMAILS = new HashSet<>(
            Arrays.asList("user_1@3mail.com", "user_2@3mail.com", "user_3@3mail.com"));
    private static ServerViewController controller;

    public static void main(String[] args) {
        // Initialize JavaFX and ServerViewController here

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Server started, listening on port " + PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Runnable task = () -> handleClient(clientSocket);
                    executor.execute(task);
                } catch (IOException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    log("Exception in accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log("Could not start server: " + e.getMessage());
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String email = reader.readLine();
            if (VALID_EMAILS.contains(email)) {
                log("Received valid email: " + email);
                writer.println("VALID");
            } else {
                log("Received invalid email: " + email);
                writer.println("INVALID");
            }
        } catch (IOException e) {
            log("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    log("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void log(String message) {
        if (controller != null) {
            controller.logMessage(message);
        } else {
            System.out.println(message); // Fallback to console if controller is not initialized
        }
    }

    // Method to set the controller from the JavaFX application
    public static void setController(ServerViewController newController) {
        controller = newController;
    }
}
