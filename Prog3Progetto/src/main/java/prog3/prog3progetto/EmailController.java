package prog3.prog3progetto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.cert.PolicyNode;
import java.util.*;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("compose-view.fxml"));
            Parent composeView = loader.load();
            ComposeController composeController= loader.getController();
            composeController.setRecipientsField(senderLabel.getText());
            composeController.setSubjectField("RE: "+ subjectField.getText());

            // Create a new window (Stage) for the compose view
            Stage composeStage = new Stage();
            composeStage.setScene(new Scene(composeView));
            composeStage.setTitle("New Email");
            composeStage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open the compose view.", Alert.AlertType.ERROR);
        }
    }

    private void closeEmailWindow() {
        Stage stage = (Stage) subjectField.getScene().getWindow();
        stage.close();
    }

    // Method to handle forwarding the email
    @FXML
    public void forwardEmail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("compose-view.fxml"));
            Parent composeView = loader.load();
            ComposeController composeController= loader.getController();
            composeController.setSubjectField(subjectField.getText());
            composeController.setMessageArea(messageArea.getText());

            // Create a new window (Stage) for the compose view
            Stage composeStage = new Stage();
            composeStage.setScene(new Scene(composeView));
            composeStage.setTitle("New Email");
            composeStage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open the compose view.", Alert.AlertType.ERROR);
        }
    }

    // Method to handle replying to all recipients of the email
    @FXML
    public void replyAllToEmail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("compose-view.fxml"));
            Parent composeView = loader.load();
            ComposeController composeController= loader.getController();
            composeController.setSubjectField("RE: " + subjectField.getText());
            //composeController.setMessageArea(messageArea.getText());
            List<String> destinatari;
            destinatari=recipientsListView.getItems();
            String io = SessionStore.getInstance().getUserEmail();
            for (String s : destinatari) {
                if (!Objects.equals(s, io)) {
                    composeController.addRecipientsField(s);
                }
            }
            composeController.addRecipientsField(senderLabel.getText());


            // Create a new window (Stage) for the compose view
            Stage composeStage = new Stage();
            composeStage.setScene(new Scene(composeView));
            composeStage.setTitle("New Email");
            composeStage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open the compose view.", Alert.AlertType.ERROR);
        }
    }

    /*
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
    */

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
