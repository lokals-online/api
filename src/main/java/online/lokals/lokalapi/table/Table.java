package online.lokals.lokalapi.table;

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.GameRequest;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.users.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document("table")
public class Table {

    @Id
    private String id;

    private User owner;

    private Set<User> users;

    private GameSession gameSession;

    public Table(User owner) {
        this.owner = owner;
        this.users = new HashSet<>();
        this.users.add(owner);
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}