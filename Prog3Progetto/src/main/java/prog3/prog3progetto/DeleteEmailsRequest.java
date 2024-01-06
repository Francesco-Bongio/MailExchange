package prog3.prog3progetto;

import java.io.Serializable;
import java.util.List;

public class DeleteEmailsRequest implements Serializable {
    private final List<Email> emailsToDelete;

    public DeleteEmailsRequest(List<Email> emailsToDelete) {
        this.emailsToDelete = emailsToDelete;
    }

    public List<Email> getEmailsToDelete() {
        return emailsToDelete;
    }
}

