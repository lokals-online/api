package online.lokals.lokalapi.game;

public class Player {
    private final String id;
    private final String username;

    public Player() {
        this.id = null;
        this.username = null;
    }

    public Player(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

//    public abstract boolean isLoggedIn();

    @Override
    public String toString() {
        return "Player[id:" + id + ", u:" + username + "]";
    }
}
