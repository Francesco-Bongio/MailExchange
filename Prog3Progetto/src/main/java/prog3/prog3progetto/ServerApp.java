package prog3.prog3progetto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("server-view.fxml"));
            Parent root = loader.load();
            ServerViewController controller = loader.getController();
            Server server = new Server(controller);

            primaryStage.setTitle("Server Control Panel");
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(event -> server.stopServer());
            primaryStage.show();

            new Thread(server::startServer).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
