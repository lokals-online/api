package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class BackgammonPlayer extends Player {

    private static final Map<Integer, Integer> NORMAL_SETUP = Map.of(5, 5, 7, 3, 12, 5, 23, 2);
    private static final Map<Integer, Integer> PICKING_SETUP = Map.of(5, 2, 15, 2, 23, 2);
    private static final Map<Integer, Integer> PICKING_2_SETUP = Map.of(0, 2, 12, 2);
    private static final Map<Integer, Integer> PICKING_3_SETUP = Map.of(0, 3, 3, 3);
    private static final Map<Integer, Integer> PICKING_4_SETUP = Map.of(0, 2);

    private static final Map<Integer, Integer> INITIAL_SETUP = NORMAL_SETUP;

    private Map<Integer, Integer> checkers;

    private int hitCheckers;

    public BackgammonPlayer(Player player) {
        super(player.getId(), player.getUsername(), player.isAnonymous());
        
        this.checkers = Map.copyOf(INITIAL_SETUP);
        this.hitCheckers = 0;
    }

    public boolean isPicking() {
        if (hitCheckers > 0) return false;

        return checkers.keySet().stream().noneMatch(index -> index > 5);
    }

    public Integer getCheckers(int index) {
        return checkers.getOrDefault(index, 0);
    }

    public boolean isTargetPointAvailable(int targetIndex) {
        return getCheckers((23 - targetIndex)) <= 1;
    }

    public boolean hasPickedAny() {
        var total = INITIAL_SETUP.values().stream().reduce(0, Integer::sum);
        var current = checkers.values().stream().reduce(0, Integer::sum);

        return total != current;
    }

    /**
     * assuming it's a valid move
     * @param move
     */
    public void move(BackgammonMove move) {
        if (move.isFromHitSlot()) {
            log.trace("from hit slot. to: {}", move.getTo());
            hitCheckers--;
            this.moveTo(move.getTo());
        }
        else if (move.isPicking()) {
            log.trace("picking from: {}", move.getTo());
            this.moveFrom(move.getFrom());
        }
        else {
            log.trace("from: {} to: {}", move.getFrom(), move.getTo());
            this.moveFrom(move.getFrom());
            this.moveTo(move.getTo());
        }
    }

    private void moveFrom(int from) {
        Integer checkersCount = getCheckers(from);
        if (checkersCount == 1) {
            checkers.remove(from);
        }
        else {
            checkers.merge(from, -1, Integer::sum);
        }
    }

    private void moveTo(int to) {
        checkers.merge(to, 1, Integer::sum);
        //.ifPresentOrElse(Slot::incrementCount, () -> checkers.put(to, new Slot(to, 1)));
    }

    public void checkHit(Integer to) {
        int targetIndex = 23 - to;
        Integer checkersCount = getCheckers(targetIndex);
        log.trace("checking hit possibility for player {}. hit is empty: {}", this, checkersCount == 0);
        if (checkersCount > 0) {
            log.trace("hit index: {}", targetIndex);
            checkers.remove(targetIndex);
            hitCheckers++;
        }
    }

    public boolean hasFinished() {
        return checkers.isEmpty() && hitCheckers == 0;
    }

    public int totalCheckersCount() {
        return checkers.values().stream().reduce(0, Integer::sum);
    }

    public boolean hasGreaterOrEqualCheckers(int dice) {
        return checkers.keySet().stream().anyMatch(pointIndex -> (pointIndex+1) > dice);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
