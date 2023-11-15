package online.lokals.lokalapi.chirak.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.users.User;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TableResponse {

    private String id;
    private Set<Player> players;
    private String gameSessionId;
    private String gameSessionKey;

    public TableResponse(Table table) {
        this.id = table.getId();
        this.players = table.getUsers().stream().map(User::toPlayer).collect(Collectors.toSet());
        this.gameSessionId = table.getGameSession().getId();
        this.gameSessionKey = table.getGameSession().getKey();
    }
}
