package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.LokalGames;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Document("backgammon_session")
public class BackgammonSession implements GameSession {

    @MongoId
    private String id;

    private String tableId;

    @DBRef
    private List<Backgammon> matches = new ArrayList<>();

    private Player home;

    private Player away;

    private Integer homeFirstDice;
    private Integer awayFirstDice;

    private BackgammonSettings settings;

    private BackgammonSessionStatus status;

    public BackgammonSession(@Nonnull String tableId, @Nonnull Player home, @Nullable Player away, BackgammonSettings settings) {
        this.tableId = tableId;
        this.home = home;
        this.away = away;
        this.settings = settings;
        this.matches = new ArrayList<>();
        this.status = (away == null) ?
                BackgammonSessionStatus.WAITING_OPPONENT :
                BackgammonSessionStatus.WAITING;
    }

    public void addMatch(Backgammon backgammon) {
        this.matches.add(backgammon);
    }

    public int getHomeScore() {
        return this.getMatches()
                .stream()
                .filter(backgammon -> {
                    if (Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver() && Objects.nonNull(this.home)) {
                        return backgammon.getWinner().getId().equals(home.getId());
                    }
                    else {
                        return false;
                    }
                })
                .map(Backgammon::getMultiply)
                .reduce(0, Integer::sum);
    }

    public int getAwayScore() {
        return this.getMatches()
                .stream()
                .filter(backgammon -> {
                    if (Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver() && Objects.nonNull(this.away)) {
                        return backgammon.getWinner().getId().equals(away.getId());
                    }
                    else {
                        return false;
                    }
                })
                .map(Backgammon::getMultiply)
                .reduce(0, Integer::sum);
    }

    public boolean isNotEnded() {
        if (this.getStatus() == null) return false;

        return this.getStatus().equals(BackgammonSessionStatus.ENDED);
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return Arrays.asList(home, away);
    }

    public Map<String, Integer> getScoreBoard() {
        Map<String, Integer> scoreBoard = new HashMap<String, Integer>();
        
        scoreBoard.put(this.home.getId(), this.getHomeScore());
        
        if (Objects.nonNull(this.away)) {
            scoreBoard.put(this.away.getId(), this.getAwayScore());
        }
        
        return scoreBoard;
    }

    @Nullable
    public Backgammon getCurrentMatch() {
        if (Objects.nonNull(this.matches) && !this.matches.isEmpty()) {
            return this.matches.get(this.matches.size()-1);
        }
        else {
            return null;
        }
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

    @Nullable
    public Player getFirstPlayer() {
        if ((homeFirstDice == null) || (awayFirstDice == null)) return null;
        return (homeFirstDice > awayFirstDice) ? home : away;
    }
    public Player getSecondPlayer() {
        if ((homeFirstDice == null) || (awayFirstDice == null)) return null;
        return (awayFirstDice > homeFirstDice) ? home : away;
    }

    @Override
    public String getKey() {
        return LokalGames.BACKGAMMON.getKey();
    }

    // @Override
    public boolean removePlayer(@NotNull String playerId) {
        if (home != null && home.getId().equals(playerId)) {
            home = null;
            return true;
        }
        else if (away != null && away.getId().equals(playerId)) {
            away = null;
            return true;
        }
        else return false;
    }

    public boolean hasEnded() {
        return getHomeScore() >= this.settings.raceTo || getAwayScore() >= this.settings.raceTo;
        
        // scoreBoard.values().stream().anyMatch((Integer i) -> i >= backgammonSession.getSettings().getRaceTo());
    }

}
