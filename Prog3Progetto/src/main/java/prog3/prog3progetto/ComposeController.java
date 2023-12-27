package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class ComposeController {
    @FXML
    private TextField recipientsField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public void sendEmail(){
        List<String> recipients = Arrays.asList(recipientsField.getText().split("\\s*,\\s*"));

        if (validateEmailAddresses(recipients)) {
            String subject = subjectField.getText();
            String message = messageArea.getText();

            String senderEmail = SessionStore.getInstance().getUserEmail();

            Email email = new Email(recipients, senderEmail, subject, message);

            // Logic to send the email (e.g., via an SMTP server)

            showAlert("Success", "Email sent successfully!", AlertType.INFORMATION);
        } else {
            showAlert("Error", "Invalid email address found.", AlertType.ERROR);
        }
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
}
