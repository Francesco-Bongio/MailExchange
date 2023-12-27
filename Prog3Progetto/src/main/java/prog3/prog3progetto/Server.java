package prog3.prog3progetto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final int PORT = 12345;
    private static final Set<String> VALID_EMAILS = new HashSet<>(
            Arrays.asList("user_1@3mail.com", "user_2@3mail.com", "user_3@3mail.com"));

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, listening on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    // Inside the server's client handling loop
                    String email = reader.readLine();
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                    if (VALID_EMAILS.contains(email)) {
                        System.out.println("Received valid email: " + email);
                        writer.println("VALID"); // Send a response back to the client
                    } else {
                        System.out.println("Received invalid email: " + email);
                        writer.println("INVALID"); // Send a response back to the client
                    }

                } catch (IOException e) {
                    System.err.println("Error handling client connection");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server");
            e.printStackTrace();
        }
    }
}
