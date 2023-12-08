package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ServerViewController {
    @FXML
    private ListView<String> logView;

    private void addLog(String log){
        logView.getItems().add(log);
    }
}
