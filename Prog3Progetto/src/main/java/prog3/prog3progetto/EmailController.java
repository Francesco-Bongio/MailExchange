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
    private Pane mailboxPane;
    @FXML
    private ListView<String> recipientsListView;

    // Method to handle going back to the mailbox view
    @FXML
    public void goBackToMailbox() {
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("../../../../../../../../Downloads/mailbox-view.fxml"));
            Node mailboxContent = mailboxLoader.load();

            // Get the controller associated with the mailbox view if needed
            MailboxController mailboxController = mailboxLoader.getController();

            // Replace the current email view with the mailbox view content
            mailboxPane.getChildren().clear();
            mailboxPane.getChildren().add(mailboxContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions
        }
    }


    // Method to handle deleting the email
    @FXML
    public void deleteEmail() {
        // Implement logic to delete the email
    }

    // Method to handle replying to the email
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
