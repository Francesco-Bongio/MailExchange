package prog3.prog3progetto;

public class SessionStore {
    private static SessionStore instance;
    private String userEmail;

    private SessionStore() {}

    public static synchronized SessionStore getInstance() {
        if (instance == null) {
            instance = new SessionStore();
        }
        return instance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
