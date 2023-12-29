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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
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
        listView.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
            @Override
            public ListCell<Email> call(ListView<Email> emailListView) {
                return new ListCell<Email>() {
                    private final CheckBox checkBox = new CheckBox();
                    private final Label senderLabel = new Label();
                    private final Label subjectLabel = new Label();
                    private final Region spacer = new Region();

                    {
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        HBox hbox = new HBox(10, checkBox, senderLabel, spacer, subjectLabel);
                        setGraphic(hbox);
                    }

                    @Override
                    protected void updateItem(Email item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            senderLabel.setText(item.getSender());
                            subjectLabel.setText(item.getSubject());
                            checkBox.setSelected(item.isSelected());
                            checkBox.setOnAction(event -> item.setSelected(checkBox.isSelected()));
                        }
                    }
                };
            }
        });
        updateInboxCounter();
        initiateReconnectionMechanism();
    }

    private void initiateReconnectionMechanism() {
        reconnectionScheduler = Executors.newSingleThreadScheduledExecutor();
        reconnectionScheduler.scheduleAtFixedRate(() -> {
            if (!isServerAvailable()) {
                Platform.runLater(() -> showAlert("Reconnection Attempt", "Trying to reconnect to the server...", Alert.AlertType.INFORMATION));
            } else {
                reconnectionScheduler.shutdown();
                Platform.runLater(() -> {
                    // showAlert("Reconnected", "Reconnected to the server successfully.", Alert.AlertType.INFORMATION);
                    refreshMailbox(); // Refresh the mailbox upon reconnection
                });
            }
        }, 0, 30, TimeUnit.SECONDS); // Attempt to reconnect every 30 seconds
    }

    private boolean isServerAvailable() {
        try (Socket socket = new Socket("localhost", 12345)) {
            return true;
        } catch (IOException e) {
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
            if (response instanceof List) {
                List<Email> emails = (List<Email>) response;
                emailList.setAll(emails);
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
        inbox.setText("Inbox (" + inboxCounter + ")");
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
        boolean allSelected = emailList.stream().allMatch(Email::isSelected);
        emailList.forEach(email -> email.setSelected(!allSelected));
        listView.refresh();
    }

    @FXML
    public void openEmail() {
        try {
            FXMLLoader emailLoader = new FXMLLoader(getClass().getResource("email-view.fxml"));
            Node emailContent = emailLoader.load();

            EmailController emailController = emailLoader.getController();

            mailboxPane.getChildren().clear();
            mailboxPane.getChildren().add(emailContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions
        }
    }
}
