package prog3.prog3progetto;

import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.util.Callback;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
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

    private ScheduledExecutorService reconnectionScheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                    showAlert("Reconnected", "Reconnected to the server successfully.", Alert.AlertType.INFORMATION);
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

    private void refreshMailbox() {
        // Logic to refresh the mailbox
    }

    private void updateInboxCounter() {
        int inboxCounter = listView.getItems().size();
        inbox.setText(String.valueOf(inboxCounter));
    }

    @FXML
    public void onDelete() {
        ObservableList<Email> emails = listView.getItems();
        Iterator<Email> iterator = emails.iterator();
        while (iterator.hasNext()) {
            Email email = iterator.next();
            if (email.isSelected()) {
                iterator.remove();
            }
        }
        updateInboxCounter();
    }

    @FXML
    public void onCompose() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ComposeView.fxml"));
            Parent composeView = loader.load();  // Directly cast it here

            // For opening in the same window
            mailboxPane.getChildren().clear();
            mailboxPane.getChildren().add(composeView);

            // For opening in a new window, uncomment the following:
            Stage stage = new Stage();
            stage.setScene(new Scene(composeView));
            stage.setTitle("New Email");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open the compose view.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onRefresh() {
        if (isServerAvailable()) {
            refreshMailbox(); // Refresh the mailbox if the server is available
        } else {
            showAlert("Server Unavailable", "Cannot connect to the server. Attempting to reconnect...", Alert.AlertType.WARNING);
            if (reconnectionScheduler == null || reconnectionScheduler.isShutdown()) {
                initiateReconnectionMechanism();
            }
        }
    }
    
    @FXML
    public void selectAllEmails() {
        ObservableList<Email> emails = listView.getItems();
        boolean allSelected = emails.stream().allMatch(Email::isSelected);

        for (Email email : emails) {
            email.setSelected(!allSelected); // If all are selected, deselect them, otherwise select all
        }

        listView.refresh(); // Refresh the ListView to update the UI
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
