package prog3.prog3progetto;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Email implements Serializable {
    private boolean selected;
    private final List<String> recipients;
    private final String sender;
    private final String subject;
    private final String bodyMessage;
    private final Set<String> recipientsReceived;
    private final Set<String> recipientsRemoved;
    @Serial
    private static final long serialVersionUID = 1L;


    public Email(List<String> recipients, String sender, String subject, String bodyMessage) {
        this.recipients = recipients;
        this.sender = sender;
        this.subject = subject;
        this.bodyMessage = bodyMessage;
        this.selected = false;  // Default to not selected
        this.recipientsRemoved = new HashSet<>();
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

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public boolean hasReceived(String recipient) {
        return recipientsReceived.contains(recipient);
    }
    public void markAsReceived(String recipient) {
        recipientsReceived.add(recipient);
    }
    public boolean hasRemoved(String recipient) { return !recipientsRemoved.contains(recipient); }
    public void markAsRemoved(String recipient) {
        recipientsRemoved.add(recipient);
    }
    public boolean isRemovedByAllRecipients() {
        return recipients.size() == recipientsRemoved.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return sender.equals(email.sender) &&
                subject.equals(email.subject) &&
                bodyMessage.equals(email.bodyMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, subject, bodyMessage);
    }
}