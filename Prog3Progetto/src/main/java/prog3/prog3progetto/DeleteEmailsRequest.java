package prog3.prog3progetto;

import java.io.Serializable;
import java.util.List;

public class DeleteEmailsRequest implements Serializable {
    private final List<Email> emailsToDelete;
    private final String user;

    public DeleteEmailsRequest(List<Email> emailsToDelete, String user) {
        this.emailsToDelete = emailsToDelete;
        this.user = user;
    }

    public List<Email> getEmailsToDelete() {
        return emailsToDelete;
    }

    public String getUser() {
        return user;
    }
}

