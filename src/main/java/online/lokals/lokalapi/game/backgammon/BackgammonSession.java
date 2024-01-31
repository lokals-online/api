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
import online.lokals.lokalapi.game.batak.Batak;
import online.lokals.lokalapi.users.User;
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

    @DBRef
    private User home;

    @DBRef
    private User away;

    private Integer homeFirstDice;
    private Integer awayFirstDice;

    private BackgammonSettings settings;

    private BackgammonSessionStatus status;

    public BackgammonSession(@Nonnull User home, @Nullable User away, BackgammonSettings settings) {
        this.tableId = home.getId(); // TODO: tables belong to home player!
        this.home = home;
        this.away = away;
        this.settings = settings;
        this.matches = new ArrayList<>();
        this.status = (away == null) ?
                BackgammonSessionStatus.WAITING_OPPONENT :
                BackgammonSessionStatus.WAITING;
    }

    public User getAway() {
        return Objects.requireNonNullElse(away, User.chirak());
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
                    if (Objects.nonNull(backgammon.getWinner()) && backgammon.isGameOver() && Objects.nonNull(getAway())) {
                        return backgammon.getWinner().getId().equals(getAway().getId());
                    }
                    else {
                        return false;
                    }
                })
                .map(Backgammon::getMultiply)
                .reduce(0, Integer::sum);
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

    public void setCurrentMatch(Backgammon backgammon) {
        this.matches.set(this.matches.size()-1, backgammon);
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
        return (homeFirstDice > awayFirstDice) ? home.toPlayer() : getAway().toPlayer();
    }
    public Player getSecondPlayer() {
        if ((homeFirstDice == null) || (awayFirstDice == null)) return null;
        return (awayFirstDice > homeFirstDice) ? home.toPlayer() : getAway().toPlayer();
    }

    @Override
    public String getKey() {
        return LokalGames.BACKGAMMON.getKey();
    }

    public boolean hasEnded() {
        return getHomeScore() >= this.settings.raceTo || getAwayScore() >= this.settings.raceTo;
    }

    public boolean playingWithChirak() {
        return getAway().getId().equals("chirak");
    }
}
