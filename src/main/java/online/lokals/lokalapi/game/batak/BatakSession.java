package online.lokals.lokalapi.game.batak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import static java.util.function.Predicate.not;

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

    private List<Player> players = new ArrayList<>(4);

    private BatakSettings settings;

    private BatakSessionStatus status;

    public BatakSession(@Nonnull String tableId, @Nonnull Player player, BatakSettings settings) {
        this.tableId = tableId;
        this.players.add(player);
        this.settings = settings;
        this.status = BatakSessionStatus.WAITING_PLAYERS;
    }

    public boolean isReadyToStart() {
        return players.size() == 4;
    }

    @Override
    public String getKey() {
        return LokalGames.BATAK.getKey();
    }

    public void addPlayer(Player player) {
        if (this.isReadyToStart()) {
            throw new IllegalArgumentException("SIT HAS TAKEN!");
        }

        this.players.add(player);
    }

    public void addMatch(Batak batak) {
        this.matches.add(batak);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public boolean removePlayerById(String playerId) {
        return this.players.removeIf((Player p) -> p.getId().equals(playerId));
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
