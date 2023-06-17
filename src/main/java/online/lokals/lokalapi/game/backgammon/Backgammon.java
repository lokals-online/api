package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import online.lokals.lokalapi.game.Game;
import online.lokals.lokalapi.game.Move;
import online.lokals.lokalapi.game.Player;

import java.util.*;

//@Document("backgammon_classic")
@Getter
public class Backgammon implements Game {

    private static final byte SLOT_CAPACITY = 24;
    private static final Map<Integer, Integer> INITIAL_SETUP = Map.of(5, 5, 7, 3, 12, 5, 23, 2);

//    @Id
    private String id;

    private Player firstPlayer;

    private Player secondPlayer;

    private Slot firstPlayerHitSlot;

    private Slot secondPlayerHitSlot;

    private final Slot[] slots = new Slot[SLOT_CAPACITY];

    private final List<Turn> turns = new ArrayList<>();

    private GameStatus status;

    private Backgammon() {
    }

    public Backgammon(Player firstPlayer) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.firstPlayer = firstPlayer;
        this.firstPlayerHitSlot = new Slot(firstPlayer.getId());
    }

    public Backgammon(Player firstPlayer, Player secondPlayer) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.firstPlayer = firstPlayer;
        this.firstPlayerHitSlot = Slot.hitSlot(getFirstPlayer().getId());
        this.secondPlayer = secondPlayer;
        this.secondPlayerHitSlot = Slot.hitSlot(getSecondPlayer().getId());
    }

    @Override
    @Nonnull
    public String getName() {
        return "backgammon";
    }

    @Override
    @Nonnull
    public Player[] getPlayers() {
        return new Player[] {firstPlayer, secondPlayer};
    }

    public void setSecondPlayer(@Nonnull Player secondPlayer) {
        this.secondPlayer = secondPlayer;
        this.secondPlayerHitSlot = new Slot(secondPlayer.getId());
    }

    public Slot[] getBoard() {
        return slots;
    }

    @Nullable
    public Turn currentTurn() {
        if (!GameStatus.STARTED.equals(this.status)) {
            // TODO: throw checked exception!
            return null;
        }

        return this.turns.get(this.turns.size()-1);
    }

    public void start() {
        assert Objects.nonNull(firstPlayer) && Objects.nonNull(secondPlayer);

        for (int i = 0; i < slots.length; i++) {
            if (INITIAL_SETUP.containsKey(i)) {
                slots[i] = new Slot(firstPlayer.getId(), INITIAL_SETUP.get(i));
            } else if (INITIAL_SETUP.containsKey((SLOT_CAPACITY - 1) - i)) {
                slots[i] = new Slot(secondPlayer.getId(), INITIAL_SETUP.get((SLOT_CAPACITY - 1) - i));
            } else {
                slots[i] = Slot.empty();
            }
        }

        this.status = GameStatus.STARTED;
        this.turns.add(Turn.firstTurn(firstPlayer.getId()));
    }

    public Integer[] rollDice() {
        return Objects.requireNonNull(this.currentTurn()).rollDice();
    }

    public void move(@NotNull String playerId, BackgammonMove move) {
        // validate
        // get Player

        // check if it is a HIT!
        if (slots[move.to()].isAHit(playerId)) {
            if (this.firstPlayer.getId().equals(playerId)) {
                secondPlayerHitSlot.incrementCount();
                slots[move.to()].decrementCount();
            } else {
                firstPlayerHitSlot.incrementCount();
                slots[move.to()].decrementCount();
            }
        }

        // check if it is from hit slot
        if (move.from() == 24 || move.from() == -1) {
            if (move.from() == 24 && this.firstPlayer.getId().equals(playerId)) {
                firstPlayerHitSlot.decrementCount();
                slots[move.to()].incrementCount(playerId);
            } else if (move.from() == -1 && this.secondPlayer.getId().equals(playerId)) {
                secondPlayerHitSlot.decrementCount();
                slots[move.to()].incrementCount(playerId);
            }
        }
        else {
            slots[move.from()].decrementCount();
            slots[move.to()].incrementCount(playerId);
        }

        Objects.requireNonNull(currentTurn()).addMove(move);
    }

    public boolean isTurnOver() {
        if (turns.isEmpty()) {
            return false;
        }
        else {
            return currentTurn().isOver();
        }
    }

    public void changeTurn() {
        String newTurn = firstPlayer.getId().equals(currentTurn().getPlayerId()) ? secondPlayer.getId() : firstPlayer.getId();
        this.turns.add(new Turn(newTurn));
    }
}

//  public void printBoard() {
//        IntStream.of(25,24,23,22,21,20,19).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("|||");
//        IntStream.of(18,17,16,15,14,13).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.println("\n\n\n");
//        IntStream.of(0,1,2,3,4,5,6).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("|||");
//        IntStream.of(7,8,9,10,11,12).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("\n");
//  }