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
    private final ServerViewController controller;
    private final ExecutorService executor;
    private ServerSocket serverSocket;
    private final List<Email> allEmails = new ArrayList<>();
    private static final Set<String> VALID_EMAILS = new HashSet<>(
            Arrays.asList("user_1@3mail.com", "user_2@3mail.com", "user_3@3mail.com", "user_4@3mail.com"));

    public Server(ServerViewController controller) {
        this.controller = controller;
        this.executor = Executors.newFixedThreadPool(10);
    }


    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            log("Server started, listening on port " + PORT);
            loadEmailsFromFile();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Runnable task = () -> handleClient(clientSocket);
                    executor.execute(task);
                } catch (IOException e) {
                    log("Exception in accepting client connection: " + e.getMessage());
                    if (serverSocket.isClosed()) {
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
        try {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        log("Executor did not terminate in the specified time.");
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log("Interrupted during shutdown: " + e.getMessage());
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
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
                if (request.startsWith("FETCH_ALL_EMAILS, ")) {
                    String userEmail = request.substring("FETCH_ALL_EMAILS, ".length());
                    List<Email> emails = getAllEmailsForUser(userEmail);
                    objectOut.writeObject(emails);
                    log("Email request for: " + userEmail);
                }
                else if (request.startsWith("GET_EMAILS,")) {
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
            } else if (obj instanceof List<?> rawList) {
                if (!rawList.isEmpty() && rawList.getFirst() instanceof Email) {
                    @SuppressWarnings("unchecked")
                    List<Email> emails = (List<Email>) rawList;
                    for (Email email : emails) {
                        processEmail(email);
                    }
                    log("Emails processed and sent to recipients");
                } else {
                    log("Received an invalid list type");
                }
            } else if (obj instanceof DeleteEmailsRequest deleteRequest) {
                deleteEmails(deleteRequest.getEmailsToDelete(), deleteRequest.getUser());
                log("Delete email request processed.");
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

    private synchronized void processEmail(Email email) {
        if (email != null && VALID_EMAILS.containsAll(email.getRecipients())) {
            log("Received and processed email from: " + email.getSender());
            storeEmail(email);
        } else {
            assert email != null;
            log("Received email with invalid recipients: " + email.getRecipients());
        }
    }

    private synchronized void storeEmail(Email email) {
        allEmails.add(email);
        log("Email added to list from: " + email.getSender());
        saveEmailsToFile();
        log("All emails saved. Total count: " + allEmails.size());
    }


    private void log(String message) {
        if (controller != null) {
            Platform.runLater(() -> controller.logMessage(message));
        } else {
            System.out.println(message);
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

    private void loadEmailsFromFile() {
        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream("emails.dat"))) {
            List<Email> loadedEmails = (List<Email>) objectIn.readObject();
            allEmails.clear();
            allEmails.addAll(loadedEmails);
            log("Loaded " + loadedEmails.size() + " emails from file. Total emails in list: " + allEmails.size());
        } catch (IOException | ClassNotFoundException e) {
            if (e.getMessage() == null) {
                log("No emails to load");
            } else {
                log("Error loading emails from file: " + e.getMessage());
            }
        }
    }

    private void saveEmailsToFile() {
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream("emails.dat"))) {
            objectOut.writeObject(allEmails);
            log("Saved " + allEmails.size() + " emails to file.");
        } catch (IOException e) {
            log("Error saving emails to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized List<Email> getAllEmailsForUser(String userEmail) {
        List<Email> emailsForUser = new ArrayList<>();
        for (Email email : allEmails) {
            if (email.getRecipients().contains(userEmail) && email.hasNotRemoved(userEmail)) {
                emailsForUser.add(email);
                if(!email.hasReceived(userEmail)){
                    email.markAsReceived(userEmail);
                }
            }
        }
        return emailsForUser;
    }

    private synchronized List<Email> getEmailsForUser(String userEmail) {
        List<Email> emailsForUser = new ArrayList<>();
        for (Email email : allEmails) {
            if (email.getRecipients().contains(userEmail) && !email.hasReceived(userEmail)) {
                email.markAsReceived(userEmail);
                emailsForUser.add(email);
            }
        }
        saveEmailsToFile();
        return emailsForUser;
    }

    private synchronized void deleteEmails(List<Email> emailsToDelete, String user) {
        log("Attempting to delete " + emailsToDelete.size() + " emails for user " + user);
        for (Email emailToDelete : emailsToDelete) {
            for (Email emailInAllEmails : allEmails) {
                if (emailToDelete.equals(emailInAllEmails)) {
                    if (emailInAllEmails.hasNotRemoved(user)) {
                        emailInAllEmails.markAsRemoved(user);
                        if (emailInAllEmails.isRemovedByAllRecipients()) {
                            allEmails.remove(emailInAllEmails);
                        }
                    }
                    break;
                }
            }
        }
        saveEmailsToFile();
    }
}
