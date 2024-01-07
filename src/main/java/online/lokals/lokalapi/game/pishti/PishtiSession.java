package online.lokals.lokalapi.game.pishti;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private Player home;

    private Player away;

    private PishtiSettings settings;

    private PishtiSessionStatus status;

    public PishtiSession(@Nonnull String tableId, @Nonnull Player home, @Nullable Player away, PishtiSettings settings) {
        this.tableId = tableId;
        this.home = home;
        this.away = away;
        this.settings = settings;
        this.status = PishtiSessionStatus.WAITING;
    }

    public String getTitle() {
        return home.getUsername() + " vs. " + (Objects.isNull(away) ? "?" : away.getUsername());
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return Arrays.asList(home, away);
    }

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
                        return pishti.getWinner().equals(home);
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
                        return pishti.getWinner().equals(away);
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

    // @Override
    // public String getMatchId() {
    //     if (Objects.nonNull(getCurrentMatch())) return getCurrentMatch().getId();
    //     else return null;
    // }

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

    public boolean playingWithChirak() {
        return Objects.requireNonNull(away).getId().equals("chirak");
    }
}
