package prog3.prog3progetto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ComposeController {
    @FXML
    private TextField recipientsField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;
    @FXML
    private Button sendButton;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @FXML
    public void sendEmail() {
        List<String> recipients = Arrays.asList(recipientsField.getText().split("\\s*,\\s*"));
        if (validateEmailAddresses(recipients)) {
            String subject = subjectField.getText();
            String message = messageArea.getText();
            String senderEmail = SessionStore.getInstance().getUserEmail();

            Email email = new Email(recipients, senderEmail, subject, message);
            if (!sendEmailToServer(email)) {
                showAlert("Error", "Failed to send email.", AlertType.ERROR);
            }
        } else {
            showAlert("Error", "Invalid email address.", AlertType.ERROR);
        }
        closeComposeWindow();
    }

    private boolean sendEmailToServer(Email email) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

            objectOut.writeObject(email);
            objectOut.flush();

            // Read the server's response (assuming the server sends a response)
            return (Boolean)objectIn.readObject();

        } catch (IOException | ClassNotFoundException e) {
            Platform.runLater(() -> showAlert("Connection Error", "Failed to connect to the server. Please try again later.", Alert.AlertType.ERROR));
            return false;
        }
    }

    private void closeComposeWindow() {
        Stage stage = (Stage) subjectField.getScene().getWindow();
        stage.close();
    }

    private boolean validateEmailAddresses(List<String> emailAddresses) {
        return emailAddresses.stream()
                .allMatch(email -> EMAIL_PATTERN.matcher(email).matches());
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setRecipientsField(String subject) {
        recipientsField.setText(subject);
    }

    public void setSubjectField(String subject){
        subjectField.setText(subject);
    }

    public void setMessageArea(String text){
        messageArea.setText(text);
    }
}
