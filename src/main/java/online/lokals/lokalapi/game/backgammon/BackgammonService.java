package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
//        addGames(5);
    }

    // TODO: sorted set!
    private Set<Backgammon> games = new HashSet<>(10);

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

    public void firstDice(String gameId, String playerId) {
        Backgammon backgammon = get(gameId).orElseThrow();

        int firstDice = backgammon.firstDice(playerId);

        // publish firstDice
        BackgammonPlayerAction backgammonPlayerAction = BackgammonPlayerAction.firstDice(backgammon.getId(), playerId, firstDice);
        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), backgammonPlayerAction);

        if (backgammon.isReadyToPlay()) {
            backgammon.start();

            BackgammonGameEvent startEvent = BackgammonGameEvent.start(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), startEvent);
        }
    }

    // @Override
    @SneakyThrows
    public void rollDice(@Nonnull String gameId) {
        Backgammon backgammon = get(gameId).orElseThrow();
        Integer[] rolledDice = backgammon.rollDice();

        BackgammonPlayerAction backgammonPlayerAction = BackgammonPlayerAction.rollDice(backgammon.getId(), backgammon.getTurn().getPlayer().getId(), rolledDice);
        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), backgammonPlayerAction);

        Set<BackgammonMove> backgammonMoves = backgammon.possibleMoves();
        if (backgammonMoves != null && backgammonMoves.isEmpty()) {
            backgammon.changeTurn();

            Thread.sleep(2000);

            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), turnHasChangedEvent);
        }
    }

    // @Override
    public void move(@Nonnull String gameId, BackgammonPlayRequest playRequest) {
        Backgammon backgammon = get(gameId).orElseThrow();
        // validate move: checkPlayer, checkHitPieces, checkDestinations

        // persist move
        for (BackgammonMove move : playRequest.moves()) {
            backgammon.move(playRequest.playerId(), move);
        }

        if (backgammon.isGameOver()) {
            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.gameOver(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), turnHasChangedEvent);

            return;
        }
        else if (backgammon.isTurnOver() || (backgammon.possibleMoves() == null || backgammon.possibleMoves().isEmpty())) {
            backgammon.changeTurn();
        }

        BackgammonPlayerAction action = BackgammonPlayerAction.move(gameId, playRequest.playerId(), playRequest.moves());

        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), action);
    }

    public void resetGames() {
        games = new HashSet<>();

        addGames(5);
    }

    private void addGames(int count) {
        for (int i = 0; i < count; i++) {
            Player ersan = new Player("ersan" + i, "ersan" + i);
            Player damla = new Player("damla" + i, "damla" + i);

            Backgammon backgammon = new Backgammon(ersan, damla);
//        backgammon.start();

            games.add(backgammon);
        }
    }
}