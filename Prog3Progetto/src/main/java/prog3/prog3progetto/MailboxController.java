package prog3.prog3progetto;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.io.IOException;

public class MailboxController implements Initializable {

    @FXML
    private Button composeButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refresh;
    @FXML
    private Label inbox;
    @FXML
    private ListView<Email> listView;
    @FXML
    private Pane mailboxPane; // This is the container for the mailbox content

    private void updateInboxCounter() {
        int inboxCounter = listView.getItems().size(); // Update counter based on the number of emails
        inbox.setText(String.valueOf(inboxCounter)); // Update the Label displaying the counter
    }
    @FXML
    public void onDelete() {
        ObservableList<Email> emails = listView.getItems();
        // Using iterator to safely remove selected emails
        Iterator<Email> iterator = emails.iterator();
        while (iterator.hasNext()) {
            Email email = iterator.next();
            if (email.isSelected()) {
                iterator.remove(); // Safely remove the email using the iterator
            }
        }
        updateInboxCounter();
    }
    @FXML
    public void onCompose(){
        // Logic for composing email
    }
    @FXML
    public void onRefresh(){
        // Logic to handle the selection
    }
    @FXML
    public void selectAllEmails(){
        // Logic to select emails
    }

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
    }

    @FXML
    public void openEmail() {
        try {
            FXMLLoader emailLoader = new FXMLLoader(getClass().getResource("email-view.fxml"));
            Node emailContent = emailLoader.load();

            // Get the controller associated with the email view if needed
            EmailController emailController = emailLoader.getController();

            // Replace ListView with emailContent
            mailboxPane.getChildren().clear();
            mailboxPane.getChildren().add(emailContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential exceptions
        }
    }
}
