module prog3.prog3progetto {
    requires javafx.controls;
    requires javafx.fxml;


    opens prog3.prog3progetto to javafx.fxml;
    exports prog3.prog3progetto;
}