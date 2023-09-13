package online.lokals.lokalapi.game.pishti;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Document("pishti_session")
public class PishtiSession {

    @MongoId
    private String id;

    @DBRef
    private List<Pishti> matches = new ArrayList<>();

    private Player home;

    private Player away;

    private PishtiSettings settings;

    private PishtiSessionStatus status;

    public PishtiSession(@Nonnull Player home, @Nullable Player away, PishtiSettings settings) {
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
        return this.matches.get(this.matches.size()-1);
    }

    public Map<String, Long> getScoreBoard() {
        return this.getMatches()
                .stream()
                .filter(pishti -> Objects.nonNull(pishti.getWinner()) && pishti.checkGameEnded())
                .map(p -> p.getWinner().getUsername())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

//        return new int[]{winners.get(home.getUsername()).intValue(), winners.get(away.getUsername()).intValue()};
    }
}
