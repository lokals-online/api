package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackgammonMove {

    @NotNull
    private Integer from;

    @NotNull
    private Integer to;

    private Long movedAt;

    public BackgammonMove(@NotNull Integer from, @NotNull Integer to) {
        this.from = from;
        this.to = to;
    }

    @JsonIgnore
    public int getDice() {
        return from-to;
    }

    @JsonIgnore
    public boolean isFromHitSlot() {return from == Backgammon.HIT_CHECKER_POINT_INDEX;}

    @JsonIgnore
    public boolean isPicking() {
        return to == Backgammon.PICKING_POINT_INDEX;
    }

    public boolean isSame(BackgammonMove backgammonMove) {
        return (this.getFrom().equals(backgammonMove.getFrom()) && this.getTo().equals(backgammonMove.getTo()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackgammonMove that = (BackgammonMove) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        return Objects.equals(movedAt, that.movedAt);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + (movedAt != null ? movedAt.hashCode() : 0);
        return result;
    }
}
