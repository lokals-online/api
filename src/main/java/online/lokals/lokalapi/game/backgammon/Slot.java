package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class Slot {

    private String playerId;
    private int count;
    private final boolean hitSlot;

    public Slot(String playerId) {
        this.playerId = playerId;
        this.count = 0;
        this.hitSlot = false;
    }

    public Slot(String playerId, boolean hitSlot) {
        this.playerId = playerId;
        this.hitSlot = true;
    }

    public Slot(String playerId, int count) {
        this(playerId, count, false);
    }

    public static Slot empty() { return new Slot(null); }
    public static Slot hitSlot(String playerId) {return new Slot(playerId, true);}

    @JsonIgnore
    public boolean isEmpty() {
        return this.count == 0 || this.playerId == null;
    }

    public boolean isAHit(@Nonnull String playerId) {
        return !playerId.equals(this.playerId) && this.count == 1;
    }

    public void decrementCount() {
        assert playerId != null;
        this.count--;

        if (this.count == 0 && !isHitSlot()) {
            setPlayerId(null);
        }
    }

    public void incrementCount() {
        assert playerId != null;
        this.count++;
    }

    public void incrementCount(@Nonnull String playerId) {
        if (this.count == 0) {
            setPlayerId(playerId);
        }

        this.count++;
    }

    @Override
    public String toString() {
        return "-["+playerId+":"+count+"]-";
    }

}