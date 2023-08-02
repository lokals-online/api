package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class BackgammonPlayer {

    private static final Map<Integer, Integer> INITIAL_SETUP = Map.of(5, 5, 7, 3, 12, 5, 23, 2);
//    private static final Map<Integer, Integer> PICKING_SETUP = Map.of(0, 5, 3, 4);

    @JsonIgnore
    private Player player;

    private Map<Integer, Slot> slots;

    private Slot hitSlot;

    private Integer firstDice;

    public BackgammonPlayer(Player player) {
        this.player = player;
        this.slots = INITIAL_SETUP.entrySet().stream()
                .map(slotMap -> new Slot(slotMap.getKey(), slotMap.getValue()))
                .collect(Collectors.toMap(Slot::getIndex, Function.identity()));
        this.hitSlot = new Slot(Backgammon.HIT_SLOT_INDEX, 0);
    }

    @Override
    public String toString() {
        return "BackgammonPlayer{" + this.getPlayer() + '}';
    }

    public boolean firstDicePlayed() {
        return firstDice != null;
    }

    public boolean isPicking() {
        if (hitSlot.getCount() > 0) return false;

        return slots.keySet().stream().noneMatch(index -> index > 5);
    }

    public Optional<Slot> getSlot(int index) {
        return Optional.ofNullable(slots.get(index));
    }

    public boolean isTargetSlotAvailable(int targetSlotIndex) {
        return getSlot((23 - targetSlotIndex)).filter(slot -> slot.getCount() > 1).isEmpty();
    }

    public void move(BackgammonMove move) {
        if (move.isFromHitSlot()) {
            hitSlot.decrement();
            moveTo(move.getTo());
        }
        else if (move.isPicking()) {
            Slot slot = getSlot(move.getFrom()).orElseThrow();
            if (slot.getCount() == 1) slots.remove(slot.getIndex());
            else slot.decrement();
        }
        else {
            moveFrom(move.getFrom());
            moveTo(move.getTo());
        }
    }

    private void moveFrom(int from) {
        Slot fromSlot = getSlot(from).orElseThrow();
        if (fromSlot.getCount() == 1) {
            slots.remove(from);
        }
        else {
            fromSlot.decrement();
        }
    }

    private void moveTo(int to) {
        getSlot(to).ifPresentOrElse(Slot::incrementCount, () -> slots.put(to, new Slot(to, 1)));
    }

    public void checkHit(Integer to) {
        Optional<Slot> hit = getSlot((23 - to));
        if (hit.isPresent()) {
            slots.remove(hit.get().getIndex());
            hitSlot.incrementCount();
        }
    }

    public boolean hasFinished() {
        return slots.isEmpty() && hitSlot.isEmpty();
    }

    public int getPieceCount() {
        return slots.values().stream().mapToInt(Slot::getCount).sum();
    }

    public boolean hasSlotsGreaterThanDice(int dice) {
        return this.isPicking() && slots.keySet().stream().anyMatch(index -> (index+1) > dice);
    }

    public String getUsername() {
        return this.getPlayer().getUsername();
    }

    public String getId() {
        return getUsername();
    }
}
