package prog3.prog3progetto;

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 12345;
    private ServerViewController controller;
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private static final Set<String> VALID_EMAILS = new HashSet<>(
            Arrays.asList("user_1@3mail.com", "user_2@3mail.com", "user_3@3mail.com"));

    public Server(ServerViewController controller) {
        this.controller = controller;
        this.executor = Executors.newFixedThreadPool(10);
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            log("Server started, listening on port " + PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Runnable task = () -> handleClient(clientSocket);
                    executor.execute(task);
                } catch (IOException e) {
                    log("Exception in accepting client connection: " + e.getMessage());
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log("Could not start server: " + e.getMessage());
        } finally {
            shutdownAndAwaitTermination();
        }
    }

    public void stopServer() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
            log("Server is shutting down.");
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("Error closing server socket: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Object obj = objectIn.readObject();

            if (obj instanceof String) {
                String email = (String) obj;
                boolean isValidEmail = VALID_EMAILS.contains(email);
                objectOut.writeObject(isValidEmail);
                log("Login request: " + email + " - Valid: " + isValidEmail);
            } else if (obj instanceof Email) {
                Email email = (Email) obj;
                processEmail(email);
                objectOut.writeObject("Email processed");
            }

        } catch (IOException | ClassNotFoundException e) {
            log("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void processEmail(Email email) {
        if (email != null && VALID_EMAILS.containsAll(email.getRecipients())) {
            log("Received and processed email from: " + email.getSender());
            // Further processing, like storing the email or forwarding it, goes here
        } else {
            log("Received email with invalid recipients: " + email.getRecipients());
        }
    }

    private void log(String message) {
        if (controller != null) {
            // Ensure GUI updates are done on the JavaFX Application Thread
            Platform.runLater(() -> controller.logMessage(message));
        } else {
            System.out.println(message); // Fallback to console if controller is not initialized
        }
    }

    private void shutdownAndAwaitTermination() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    log("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
