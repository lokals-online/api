package online.lokals.lokalapi.game.batak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import online.lokals.lokalapi.users.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.LokalGames;
import online.lokals.lokalapi.game.Player;

@Getter
@Setter
@NoArgsConstructor
@Document("batak_session")
public class BatakSession implements GameSession {

    @MongoId
    private String id;

    private String tableId;

    @DBRef
    private List<Batak> matches = new ArrayList<>();

    @DBRef
    private List<User> users = new ArrayList<>(4);

    private BatakSettings settings;

    private BatakSessionStatus status;

    public BatakSession(@Nonnull String tableId, @Nonnull User user, BatakSettings settings) {
        this.tableId = tableId;
        this.users.add(user);
        this.settings = settings;
        this.status = BatakSessionStatus.WAITING_PLAYERS;
    }

    public boolean isReadyToStart() {
        return users.size() == 4;
    }

    @Override
    public String getKey() {
        return LokalGames.BATAK.getKey();
    }

    public void addUser(User user) {
        if (this.isReadyToStart()) {
            throw new IllegalArgumentException("SIT HAS TAKEN!");
        }

        this.users.add(user);
    }

    public void addMatch(Batak batak) {
        this.matches.add(batak);
    }

    public void removePlayer(Player player) {
        this.users.remove(player);
    }

    public int getDealerIndex() {
        return (this.matches.size() % 4); // first player is the first dealer also.
    }

    public Batak getCurrentMatch() {
        return (!this.matches.isEmpty()) ? this.matches.get(this.matches.size()-1) : null;
    }

    public void setCurrentMatch(Batak batak) {
        this.matches.set(this.matches.size()-1, batak);
    }

    public Map<String, Integer> getScores() {
        return this.matches.stream()
                .filter(Batak::gameEnded)
                .map(Batak::getScores)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }
}
