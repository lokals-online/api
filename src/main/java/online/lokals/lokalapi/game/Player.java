package online.lokals.lokalapi.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

@Getter
@AllArgsConstructor
public class Player {

    private String id;
    private String username;
    private boolean isAnonymous;

    public Player() {
        this.id = null;
        this.username = null;
    }

    public Player(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public static Player chirak() {
        return new Player("chirak", "çırak");
    }

    @Override
    public String toString() {
        return "Player[id:" + id + ", u:" + username + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    

}
