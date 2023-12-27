package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    // In the StartController's onSubmission() method
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
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write(email);
            writer.newLine();
            writer.flush();

            String serverResponse = reader.readLine();
            return "VALID".equals(serverResponse);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openMailboxView() {
        try {
            // Load the Mailbox FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MailboxView.fxml"));
            Stage stage = (Stage) enterEmailText.getScene().getWindow();

            // Hide the current (StartView) window
            stage.hide();

            // Set the new scene
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Optionally, you can show the stage again if you want it to be hidden just during the transition
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            displayError.setText("Error opening the mailbox view.");
        }
    }
}