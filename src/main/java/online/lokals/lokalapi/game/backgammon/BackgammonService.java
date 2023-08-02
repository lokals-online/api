package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgammonService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final BackgammonRepository backgammonRepository;

    public List<Backgammon> allGames() {
        return backgammonRepository.findAll();
    }

    public Backgammon newGame(@Nonnull Player player) {
        Backgammon backgammon = new Backgammon(player);

        backgammonRepository.save(backgammon);

        return backgammon;
    }

    public Optional<Backgammon> get(@Nonnull String gameId) {
        return backgammonRepository.findById(gameId);
    }

    public void setOpponent(@Nonnull String gameId, @Nonnull Player player) {
        Backgammon backgammon = get(gameId).orElseThrow();
        backgammon.setSecondPlayer(player);

        backgammonRepository.save(backgammon);
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

        backgammonRepository.save(backgammon);
    }

    // @Override
    @SneakyThrows
    public void rollDice(@Nonnull String gameId) {
        Backgammon backgammon = get(gameId).orElseThrow();
        Integer[] rolledDice = backgammon.rollDice();

        backgammonRepository.save(backgammon);

        BackgammonPlayerAction backgammonPlayerAction = BackgammonPlayerAction.rollDice(backgammon.getId(), backgammon.currentTurn().getPlayer().getId(), rolledDice);
        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), backgammonPlayerAction);

        Thread.sleep(2000);

        Set<BackgammonMove> backgammonMoves = backgammon.possibleMoves();
        if (backgammonMoves != null && backgammonMoves.isEmpty()) {
            backgammon.changeTurn();
            backgammonRepository.save(backgammon);

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

        if (backgammon.isTurnOver() || (backgammon.possibleMoves() == null || backgammon.possibleMoves().isEmpty())) {
            backgammon.changeTurn();
        }

        backgammonRepository.save(backgammon);

        BackgammonPlayerAction action = BackgammonPlayerAction.move(gameId, playRequest.playerId(), playRequest.moves());

        simpMessagingTemplate.convertAndSend("/topic/" + backgammon.getId(), action);
    }
}