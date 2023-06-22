package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

@Getter
@Setter
@AllArgsConstructor
class Slot {

    @JsonIgnore
    private Player player;
    private int index;
    private int count;

    public String getPlayerId() {
        return player.getId();
    }

    public void decrement() {
        assert player != null && count != 0;
        this.count--;
    }

    public void incrementCount() {
        assert player != null;
        this.count++;
    }

    @Override
    public String toString() {
        return "-["+player+":"+count+"]-";
    }

    public boolean isEmpty() {
        return count == 0;
    }
}