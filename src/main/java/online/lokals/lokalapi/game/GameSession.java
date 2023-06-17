package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.exception.SitAlreadyTakenException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.stream.IntStream;

    // TODO:  protected boolean isTeamGame = false;
@Getter
@Setter
@Document
public class GameSession {

    @Id
    private String id;
    private String lokalId;
    private String tableId;
    private String gameId;
    private int playersAllowed;
    public Player[] players;
    public Integer turn;

    public GameSession(int playerCount) {
        this.playersAllowed = playerCount;
        this.players = new Player[playerCount];
    }

    public void addPlayer(int index, @Nonnull Player player) {
        if (this.players[0] == null) {
            this.players[index] = player;
        }
        else {
            throw new SitAlreadyTakenException(index + ". sit is full. available sits: " + Arrays.toString(availableSits()));
        }
    }

    public void removePlayer(int index) {
        this.players[index] = null;
        // notify table
    }

    public boolean isReady() {
        return availableSits().length == 0;
    }

    public void start() {
        this.turn = decideTurn();
        // notify table

    }

    private Integer decideTurn() {
        return 0;
    }

    private int[] availableSits() {
        return IntStream.rangeClosed(0, 3).filter(i -> this.players[i] == null).toArray();
    }
}
