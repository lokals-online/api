package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class BackgammonPlayer extends Player {

    private static final Map<Integer, Integer> INITIAL_SETUP = Map.of(5, 5, 7, 3, 12, 5, 23, 2);
    private static final Map<Integer, Integer> PICKING_SETUP = Map.of( 0, 5, 3, 4);

    private final Map<Integer, Slot> slots;

    private final Slot hitSlot;

    @Setter
    private Integer firstDice;

    public BackgammonPlayer(Player player) {
        super(player.getId(), player.getUsername());
        this.slots = INITIAL_SETUP.entrySet().stream()
                .map(slotMap -> new Slot(player, slotMap.getKey(), slotMap.getValue()))
                .collect(Collectors.toMap(Slot::getIndex, Function.identity()));
        this.hitSlot = new Slot(player, Backgammon.HIT_SLOT_INDEX, 0);
    }

    @Override
    public String toString() {
        return "BackgammonPlayer{" + this.getUsername() + '}';
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

    public Optional<Slot> getOpponentSlot(int index) {
        return getSlot(23-index);
    }

    public boolean isTargetSlotAvailable(int targetSlotIndex) {
        return getSlot((23 - targetSlotIndex)).filter(slot -> slot.getCount() > 1).isEmpty();
    }

    public void move(BackgammonMove move) {
        if (move.isFromHitSlot()) {
            hitSlot.decrement();
            moveTo(move.to());
        }
        else if (move.isPicking()) {
            Slot slot = getSlot(move.from()).orElseThrow();
            if (slot.getCount() == 1) slots.remove(slot.getIndex());
            else slot.decrement();
        }
        else {
            moveFrom(move.from());
            moveTo(move.to());
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
        getSlot(to).ifPresentOrElse(Slot::incrementCount, () -> slots.put(to, new Slot(this, to, 1)));
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
}
