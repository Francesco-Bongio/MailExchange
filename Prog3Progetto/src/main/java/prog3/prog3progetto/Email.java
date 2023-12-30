package prog3.prog3progetto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.io.Serializable;
import java.util.List;

public class Email implements Serializable {
    private boolean selected;
    private List<String> recipients;
    private String sender;
    private String subject;
    private String bodyMessage;

    public Email(List<String> recipients, String sender, String subject, String bodyMessage) {
        this.recipients = recipients;
        this.sender = sender;
        this.subject = subject;
        this.bodyMessage = bodyMessage;
        this.selected = false;  // Default to not selected
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

}