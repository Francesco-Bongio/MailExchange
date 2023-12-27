package prog3.prog3progetto;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ServerViewController {
    @FXML
    private ListView<String> logView;

    private final ObservableList<String> logMessages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        logView.setItems(logMessages);
    }

    public void logMessage(String message) {
        Platform.runLater(() -> logMessages.add(message));
    }
}
