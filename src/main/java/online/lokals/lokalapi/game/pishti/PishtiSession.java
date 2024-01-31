package online.lokals.lokalapi.game.pishti;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import online.lokals.lokalapi.users.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.LokalGames;
import online.lokals.lokalapi.game.Player;

@Getter
@Setter
@NoArgsConstructor
@Document("pishti_session")
public class PishtiSession implements GameSession {

    @MongoId
    private String id;

    private String tableId;

    @DBRef
    private List<Pishti> matches = new ArrayList<>();

    @DBRef
    private User home;

    @DBRef
    private User away;

    private PishtiSettings settings;

    private PishtiSessionStatus status;

    public PishtiSession(@Nonnull String tableId, @Nonnull User home, @Nullable User away, PishtiSettings settings) {
        this.tableId = tableId;
        this.home = home;
        this.away = away;
        this.settings = settings;
        this.status = PishtiSessionStatus.WAITING;
    }

//    public User getAway() {
//        return Objects.requireNonNullElse(away, User.chirak());
//    }

    public void addMatch(Pishti pishti) {
        this.matches.add(pishti);
    }

    public Pishti getCurrentMatch() {
        return (this.matches != null && !this.matches.isEmpty()) ? this.matches.get(this.matches.size()-1) : null;
    }

    // @Override
    public int getHomeScore() {
        return ((int) this.getMatches()
                .stream()
                .filter(pishti -> {
                    if (Objects.nonNull(pishti.getWinner()) && pishti.checkGameEnded()) {
                        return pishti.getWinner().equals(home.toPlayer());
                    }
                    else {
                        return false;
                    }
                })
                .count());
    }

    // @Override
    public int getAwayScore() {
        return ((int) this.getMatches()
                .stream()
                .filter(pishti -> {
                    if (Objects.nonNull(pishti.getWinner()) && pishti.checkGameEnded()) {
                        return pishti.getWinner().equals(getAway().toPlayer());
                    }
                    else {
                        return false;
                    }
                })
                .count());
    }

    @Override
    public String getKey() {
        return LokalGames.PISHTI.getKey();
    }

    public boolean playingWithChirak() {
        return Objects.nonNull(away) && getAway().getId().equals("chirak");
    }
}
