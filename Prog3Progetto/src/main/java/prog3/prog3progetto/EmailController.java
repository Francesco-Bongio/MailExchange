package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.security.cert.PolicyNode;
import java.util.List;

public class EmailController {

    @FXML
    private TextField subjectField;
    @FXML
    private Label senderLabel;
    @FXML
    private TextArea messageArea;
    @FXML
    private ListView<String> recipientsListView;

    public void setEmail(Email email) {
        if (email != null) {
            senderLabel.setText(email.getSender());
            subjectField.setText(email.getSubject());
            messageArea.setText(email.getBodyMessage());
            // Clear the current recipients and add the new ones
            recipientsListView.getItems().clear();
            recipientsListView.getItems().addAll(email.getRecipients());
        }
    }
    @FXML
    public void replyToEmail() {
        // Implement logic for replying to the email
    }

    // Method to handle forwarding the email
    @FXML
    public void forwardEmail() {
        // Implement logic for forwarding the email
    }

    // Method to handle replying to all recipients of the email
    @FXML
    public void replyAllToEmail() {
        // Implement logic for replying to all recipients of the email
    }
}
