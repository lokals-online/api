package online.lokals.lokalapi.game.backgammon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Slot {

    private int index;
    private int count;

    public void decrement() {
        assert count != 0;
        this.count--;
    }

    public void incrementCount() {
        this.count++;
    }

    @Override
    public String toString() {
        return "-["+count+"]-";
    }

    public boolean isEmpty() {
        return count == 0;
    }
}
