package prog3.prog3progetto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StartController {
    @FXML
    private TextField enterEmailText;
    @FXML
    private Label insertEmail;
    @FXML
    private Label displayError;

    @FXML
    protected void onSubmission() {
        displayError.setText("Email already in use");
        // Logic for email submission handling
    }
}