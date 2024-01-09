package prog3.prog3progetto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ComposeController {
    @FXML
    private TextField recipientsField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;
    private final List<Email> emailsToSend = Collections.synchronizedList(new ArrayList<>());
    private ScheduledExecutorService sendEmailsScheduler;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @FXML
    public void sendEmail() {
        List<String> recipients = Arrays.asList(recipientsField.getText().split("\\s*,\\s*"));
        if (validateEmailAddresses(recipients)) {
            String subject = subjectField.getText();
            String message = messageArea.getText();
            String senderEmail = SessionStore.getInstance().getUserEmail();

            Email email = new Email(recipients, senderEmail, subject, message);
            synchronized (emailsToSend) {
                emailsToSend.add(email);
            }
            sendEmailToServer();
        } else {
            showAlert("Error", "Invalid email address.", AlertType.ERROR);
        }
        closeComposeWindow();
    }

    private void sendEmailToServer() {
        if (isServerAvailable()) {
            try (Socket socket = new Socket("localhost", 12345);
                 ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream())) {

                synchronized (emailsToSend) {
                    objectOut.writeObject(emailsToSend);
                    objectOut.flush();
                    emailsToSend.clear();
                }
                if (!sendEmailsScheduler.isShutdown() || sendEmailsScheduler != null) {
                    sendEmailsScheduler.shutdownNow();
                }

            } catch (IOException e) {
                scheduleEmailSending();
            }
        } else {
            Platform.runLater(() -> showAlert("Connection Error", "Email will be sent later", AlertType.INFORMATION));
            scheduleEmailSending();
        }
    }

    private void scheduleEmailSending() {
        if (sendEmailsScheduler == null || sendEmailsScheduler.isShutdown()) {
            sendEmailsScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        sendEmailsScheduler.scheduleWithFixedDelay(() -> {
            if (!emailsToSend.isEmpty() && isServerAvailable()) {
                try {
                    sendEmailToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 30, 10, TimeUnit.SECONDS);
    }

    private void closeComposeWindow() {
        Stage stage = (Stage) subjectField.getScene().getWindow();
        stage.close();
    }

    private boolean validateEmailAddresses(List<String> emailAddresses) {
        return emailAddresses.stream()
                .allMatch(email -> EMAIL_PATTERN.matcher(email).matches());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setRecipientsField(String subject) {
        recipientsField.setText(subject);
    }

    public String getRecipientsField(){
        return recipientsField.getText();
    }

    public void addRecipientsField(String subject){
        if(Objects.equals(recipientsField.getText(), "")) {
            recipientsField.setText(subject);
        }
        else{
            recipientsField.setText(getRecipientsField()+","+subject);
        }
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

    public void setSubjectField(String subject){
        subjectField.setText(subject);
    }

    public void setMessageArea(String text){
        messageArea.setText(text);
    }
}
