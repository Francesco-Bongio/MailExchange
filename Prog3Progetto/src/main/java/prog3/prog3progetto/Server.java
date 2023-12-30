package prog3.prog3progetto;

import javafx.application.Platform;
import java.util.stream.Collectors;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 12345;
    private final ServerViewController controller;
    private final ExecutorService executor;
    private ServerSocket serverSocket;
    private final List<Email> allEmails = new ArrayList<>();
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

            if (obj instanceof String request) {
                if (request.startsWith("GET_EMAILS,")) {
                    // Handle GET_EMAILS request
                    String userEmail = request.substring("GET_EMAILS, ".length());
                    List<Email> emails = getEmailsForUser(userEmail);
                    objectOut.writeObject(emails);
                    log("Email request for: " + userEmail);
                } else if ("PING".equals(request)) {
                    objectOut.writeObject("PONG");
                    log("Ping request received");
                } else {
                    // Handle login request
                    boolean isValidEmail = VALID_EMAILS.contains(request);
                    objectOut.writeObject(isValidEmail);
                    log("Login request: " + request + " - Valid: " + isValidEmail);
                }
            } else if (obj instanceof Email email) {
                // Handle email object
                boolean result = processEmail(email); // Implement this method
                objectOut.writeObject(result);
                log("Email processed and sent to recipients");
            }

        } catch (IOException | ClassNotFoundException e) {
            log("Error handling client connection: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private List<Email> getEmailsForUser(String userEmail) {
        // Filter and return emails where the user is one of the recipients
        return allEmails.stream()
                .filter(email -> email.getRecipients().contains(userEmail))
                .collect(Collectors.toList());
    }

    private boolean processEmail(Email email) {
        if (email != null && VALID_EMAILS.containsAll(email.getRecipients())) {
            System.out.println(email.getBodyMessage());
            log("Received and processed email from: " + email.getSender());
            storeEmail(email); // Store the email
            // You can add more logic here if you need to "forward" the email to recipients
            return true;
        } else {
            assert email != null;
            log("Received email with invalid recipients: " + email.getRecipients());
            return false;
        }
    }

    private void storeEmail(Email email) {
        allEmails.add(email); // Add the email to the collection
        System.out.println("recipients of the mail: " + email.getRecipients());
        log("Email stored from: " + email.getSender());
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
