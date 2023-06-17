package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgammonService implements CommandLineRunner {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void run(String... args) throws Exception {

        Player ersan = new Player("ersan", "ersan");
        Player damla = new Player("damla", "damla");

        Backgammon backgammon = new Backgammon(ersan, damla);
        backgammon.start();

        games.add(backgammon);
    }

    // TODO: sorted set!
    private final Set<Backgammon> games = new HashSet<>(1);

    public Set<Backgammon> allGames() {
        return games;
    }

    public Backgammon newGame(@Nonnull Player player) {
        Backgammon backgammon = new Backgammon(player);

        games.add(backgammon);

        return backgammon;
    }

    // @Override
    public Optional<Backgammon> get(@Nonnull String gameId) {
        return games.stream()
                .filter(backgammon -> backgammon.getId().equals(gameId))
                .findFirst();
    }

    public void addOtherPlayer(@Nonnull String gameId, @Nonnull Player player) {
        Backgammon backgammon = get(gameId).orElseThrow();
        backgammon.setSecondPlayer(player);

        backgammon.start();
    }

    // @Override
    public Turn currentTurn(@Nonnull String gameId) {
        Backgammon backgammon = get(gameId).orElseThrow();

        return backgammon.currentTurn();
    }

    // @Override
    public void rollDice(@Nonnull String gameId) {
        Backgammon backgammon = get(gameId).orElseThrow();
        Integer[] rolledDice = backgammon.rollDice();

        BackgammonAction backgammonAction = BackgammonAction.rollDice(backgammon.getId(), backgammon.currentTurn().getPlayerId(), rolledDice);

        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), backgammonAction);
    }

    // @Override
    public void move(@Nonnull String gameId, BackgammonPlayRequest playRequest) {
        Backgammon backgammon = get(gameId).orElseThrow();
        // validate move: checkPlayer, checkHitPieces, checkDestinations

        // persist move
        for (BackgammonMove move : playRequest.moves()) {
            backgammon.move(playRequest.playerId(), move);
        }

        if (backgammon.isTurnOver()) {
            backgammon.changeTurn();
        }

        BackgammonAction action = BackgammonAction.move(gameId, playRequest.playerId(), playRequest.moves());

        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), action);
    }

    public Backgammon restart() {
        games.removeAll(games);

        Player ersan = new Player("ersan", "ersan");
        Player damla = new Player("damla", "damla");

        Backgammon backgammon = new Backgammon(ersan, damla);
        backgammon.start();

        games.add(backgammon);

        return backgammon;
    }
}