package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

public class StartController {
    @FXML
    private TextField enterEmailText;
    @FXML
    private Label displayError;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @FXML
    protected void onSubmission() {
        String email = enterEmailText.getText();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            displayError.setText("Invalid email format");
        } else if (!sendEmailToServer(email)) {
            displayError.setText("Email not registered");
        }
        else {
            SessionStore.getInstance().setUserEmail(email);
            openMailboxView();
        }
    }

    private boolean sendEmailToServer(String email) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

            objectOut.writeObject(email);
            objectOut.flush();

            return (Boolean) objectIn.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openMailboxView() {
        try {
            // Load the Mailbox FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mailbox-view.fxml"));
            Parent root = loader.load();
            MailboxController mailboxController = loader.getController();

            if (mailboxController == null) {
                System.err.println("MailboxController is null. Check FXML file and controller association.");
                return; // Exit to prevent NPE
            }

            // Hide the current (StartView) window
            Stage stage = (Stage) enterEmailText.getScene().getWindow();
            stage.hide();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.setTitle("3Mail");
            stage.setOnCloseRequest(event -> mailboxController.stopMailboxController());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            displayError.setText("Error opening the mailbox view.");
        }
    }
}