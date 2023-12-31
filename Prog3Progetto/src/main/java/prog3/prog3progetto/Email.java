package prog3.prog3progetto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Email implements Serializable {
    private boolean selected;
    private List<String> recipients;
    private String sender;
    private String subject;
    private String bodyMessage;
    private Set<String> recipientsReceived;

    public Email(List<String> recipients, String sender, String subject, String bodyMessage) {
        this.recipients = recipients;
        this.sender = sender;
        this.subject = subject;
        this.bodyMessage = bodyMessage;
        this.selected = false;  // Default to not selected
        recipientsReceived = new HashSet<>();
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
    public boolean hasReceived(String recipient) {
        return recipientsReceived.contains(recipient);
    }
    public void markAsReceived(String recipient) {
        recipientsReceived.add(recipient);
    }
    public Set<String> getRecipientsReceived() {
        return recipientsReceived;
    }
}