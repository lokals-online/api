package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.pishti.Pishti;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Document("backgammon_session")
public class BackgammonSession {

    @MongoId
    private String id;

    @DBRef
    private List<Backgammon> matches = new ArrayList<>();

    private Player home;

    private Player away;

    private Integer homeFirstDice;
    private Integer awayFirstDice;

    private BackgammonSettings settings;

    private BackgammonSessionStatus status;

    public BackgammonSession(@Nonnull Player home, @Nullable Player away, BackgammonSettings settings) {
        this.home = home;
        this.away = away;
        this.settings = settings;
        this.status = BackgammonSessionStatus.WAITING;
    }

    public void addMatch(Backgammon backgammon) {
        this.matches.add(backgammon);
    }

    public String getTitle() {
        return home.getUsername() + " vs. " + (Objects.isNull(away) ? "?" : away.getUsername());
    }

    public int getHomeScore() {
        return ((int) this.getMatches()
                .stream()
                .filter(backgammon -> {
                    if (Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver()) {
                        return backgammon.getWinner().equals(home);
                    }
                    else {
                        return false;
                    }
                })
                .count());
    }

    public int getAwayScore() {
        return ((int) this.getMatches()
                .stream()
                .filter(backgammon -> {
                    if (Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver()) {
                        return backgammon.getWinner().equals(away);
                    }
                    else {
                        return false;
                    }
                })
                .count());
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return Arrays.asList(home, away);
    }

    public Map<String, Long> getScoreBoard() {
        return this.getMatches()
                .stream()
                .filter(backgammon -> Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver())
                .map(p -> p.getWinner().getUsername())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

//        return new int[]{winners.get(home.getUsername()).intValue(), winners.get(away.getUsername()).intValue()};
    }

    public Backgammon getCurrentMatch() {
        return this.matches.get(this.matches.size()-1);
    }

    @Nullable
    public Player getPreviousWinner() {
        if (this.matches == null || this.matches.isEmpty()) return null;

        for (int i = (this.matches.size()-1); i >= 0; i--) {
            Backgammon backgammon = this.matches.get(i);

            if ((backgammon.isGameOver()) && (backgammon.getWinner() != null)) {
                return backgammon.getWinner();
            }
        }

        return null;
    }

    public boolean isReady() {
        return homeFirstDice != null &&
                awayFirstDice != null &&
                homeFirstDice != 0 &&
                awayFirstDice != 0 &&
                !homeFirstDice.equals(awayFirstDice);
    }

    public Player getFirstPlayer() {
        return (homeFirstDice > awayFirstDice) ? home : away;
    }
    public Player getSecondPlayer() {
        return (awayFirstDice > homeFirstDice) ? home : away;
    }
}
