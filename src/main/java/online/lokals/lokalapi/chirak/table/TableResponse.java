package online.lokals.lokalapi.chirak.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.users.User;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TableResponse {

    public static final TableResponse NONE = new TableResponse();
    private String id;
    private Player owner;
    private Set<Player> players;
    private String gameSessionId;
    private String gameSessionKey;

    protected TableResponse() {

    }

    public TableResponse(Table table) {
        if (Objects.isNull(table)) {
            return;
        }
        this.id = table.getId();
        this.owner = table.getOwner().toPlayer();
        this.players = table.getUsers().stream().map(User::toPlayer).collect(Collectors.toSet());
        this.gameSessionId = table.getGameSession().getId();
        this.gameSessionKey = table.getGameSession().getKey();
    }
}
