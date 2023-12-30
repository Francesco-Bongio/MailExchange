package prog3.prog3progetto;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MailboxController implements Initializable {

    @FXML
    private Button composeButton, deleteButton, refresh;
    @FXML
    private Label inbox;
    @FXML
    private ListView<Email> listView;
    @FXML
    private Pane mailboxPane;

    private final ObservableList<Email> emailList = FXCollections.observableArrayList();
    private ScheduledExecutorService reconnectionScheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setItems(emailList);
        listView.setCellFactory(lv -> new ListCell<Email>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label senderLabel = new Label();
            private final Label subjectLabel = new Label();
            private final Region spacer = new Region();
            private final HBox hbox = new HBox(10); // 10 is spacing between elements

            {
                spacer.setMinWidth(60);
                spacer.setPrefWidth(60);
                spacer.setMaxWidth(60);

                hbox.getChildren().addAll(checkBox, senderLabel, spacer, subjectLabel);
            }

            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty || email == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(email.isSelected());
                    checkBox.setOnAction(e -> email.setSelected(checkBox.isSelected()));
                    // Set the text for sender and subject labels
                    senderLabel.setText(email.getSender());
                    subjectLabel.setText(email.getSubject());
                    setGraphic(hbox); // Set the HBox as the graphic of the cell
                }
            }
        });
        populateWithStaticData(); // Populate ListView with static data for testing
        updateInboxCounter();
        //initiateReconnectionMechanism();
    }

    //test with static data
    private void populateWithStaticData() {
        List<Email> staticEmails = List.of(
                new Email(List.of("recipient1@example.com"), "sender@example.com", "Subject 1", "Body of email 1"),
                new Email(List.of("recipient2@example.com"), "sender@example.com", "Subject 2", "Body of email 2"),
                new Email(List.of("recipient3@example.com"), "sender@example.com", "Subject 3", "Body of email 3")
        );
        emailList.setAll(staticEmails);
    }

    private void initiateReconnectionMechanism() {
        reconnectionScheduler = Executors.newSingleThreadScheduledExecutor();
        reconnectionScheduler.scheduleAtFixedRate(() -> {
            try {
                if (!isServerAvailable()) {
                    Platform.runLater(() -> showAlert("Reconnection Attempt", "Trying to reconnect to the server...", Alert.AlertType.INFORMATION));
                } else {
                    // Refresh the mailbox upon reconnection
                    Platform.runLater(this::refreshMailbox);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception
            }
        }, 0, 15, TimeUnit.MINUTES);
    }

    private boolean isServerAvailable() {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

            objectOut.writeObject("PING");
            objectOut.flush();

            String response = (String) objectIn.readObject();
            return "PONG".equals(response);

        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void refreshMailbox() {
        String userEmail = SessionStore.getInstance().getUserEmail();
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

            objectOut.writeObject("GET_EMAILS, " + userEmail);
            objectOut.flush();

            Object response = objectIn.readObject();
            if (response instanceof List<?> list) {
                List<Email> emails = (List<Email>) list;
                emailList.addAll(emails);
            } else {
                showAlert("Error", "Invalid response from server.", Alert.AlertType.ERROR);
            }
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Connection Error", "Failed to connect to the server.", Alert.AlertType.ERROR);
        }
        updateInboxCounter();
    }

    private void updateInboxCounter() {
        int inboxCounter = emailList.size();
        inbox.setText(Integer.toString(inboxCounter));
    }

    @FXML
    public void onDelete() {
        emailList.removeIf(Email::isSelected);
        updateInboxCounter();
    }

    @FXML
    public void onCompose() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("compose-view.fxml"));
            Parent composeView = loader.load();

            // Create a new window (Stage) for the compose view
            Stage composeStage = new Stage();
            composeStage.setScene(new Scene(composeView));
            composeStage.setTitle("New Email");
            composeStage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open the compose view.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onRefresh() {
        if (isServerAvailable()) {
            refreshMailbox();
        } else {
            showAlert("Server Unavailable", "Cannot connect to the server. Attempting to reconnect...", Alert.AlertType.WARNING);
            if (reconnectionScheduler == null || reconnectionScheduler.isShutdown()) {
                initiateReconnectionMechanism();
            }
        }
    }

    @FXML
    public void selectAllEmails() {
        // Check if all emails are currently selected
        boolean allSelected = emailList.stream().allMatch(Email::isSelected);

        // If all are selected, deselect all; otherwise, select all
        for (Email email : emailList) {
            email.setSelected(!allSelected);
        }
        listView.refresh();
    }


    @FXML
    public void openEmail() {
        try {
            FXMLLoader emailLoader = new FXMLLoader(getClass().getResource("email-view.fxml"));
            Node emailContent = emailLoader.load();
            mailboxPane.getChildren().clear();
            mailboxPane.getChildren().add(emailContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions
        }
    }
}
