package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.backgammon.event.BackgammonGameEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgammonService {

    private static final String BACKGAMMON_TOPIC_DESTINATION = "/topic/game/backgammon/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final BackgammonRepository backgammonRepository;

    Backgammon newMatchForSession(BackgammonSession backgammonSession) {
        Integer[] firstDices = {backgammonSession.getHomeFirstDice(), backgammonSession.getAwayFirstDice()};
        String winnerId = (backgammonSession.getCurrentMatch() != null && backgammonSession.getCurrentMatch().getWinner() != null) ? 
            backgammonSession.getCurrentMatch().getWinner().getId() : null;

        Backgammon backgammon = new Backgammon(backgammonSession.getFirstPlayer(), backgammonSession.getSecondPlayer(), firstDices, winnerId);

        return backgammonRepository.save(backgammon);
    }

    public Backgammon get(@Nonnull String gameId) {
        return backgammonRepository.findById(gameId).orElseThrow();
    }

    // @Override
    @SneakyThrows
    public void rollDice(@Nonnull String gameId, Player player) {
        log.trace("player[{}] is rolling dice", player.toString());
        Backgammon backgammon = get(gameId);
        
        assert Objects.equals(Objects.requireNonNull(backgammon.currentTurn()).getPlayerId(), player.getId());
        
        Integer[] rolledDice = backgammon.rollDice();

        log.trace("player[{}] has rolled dice[]", player.toString(), rolledDice.toString());

        backgammonRepository.save(backgammon);

        BackgammonGameEvent event = BackgammonGameEvent.rollDice(backgammon.getId(), backgammon);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);

        Thread.sleep(2000);

        Set<BackgammonMove> backgammonMoves = backgammon.possibleMoves();
        if (backgammonMoves != null && backgammonMoves.isEmpty()) {
            Turn changedTurn = backgammon.changeTurn();
            backgammonRepository.save(backgammon);

            Thread.sleep(2000);

            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), turnHasChangedEvent);
        }
    }

    // @Override
    @SneakyThrows
    public void move(@Nonnull String gameId, BackgammonPlayRequest playRequest, Player player) {
        Backgammon backgammon = get(gameId);

        // validate move: checkPlayer, checkHitPieces, checkDestinations
        if (backgammon.possibleMoves() != null && !backgammon.possibleMoves().isEmpty()) {

            boolean isValid = playRequest.moves().stream()
                    .anyMatch(backgammonMove -> Objects.requireNonNull(backgammon.possibleMoves())
                            .stream()
                            .anyMatch(backgammonMove1 -> backgammonMove1.isSame(backgammonMove)));

            if (!isValid) throw new IllegalArgumentException(String.format("it is not a valid move %s", playRequest.moves()));
        }

        // persist move
        for (BackgammonMove move : playRequest.moves()) {
            backgammon.move(player, move);
        }

        if (backgammon.isGameOver()) {
            backgammonRepository.save(backgammon);

            BackgammonGameEvent gameOverEvent = BackgammonGameEvent.gameOver(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), gameOverEvent);

            return;
        }

        if (backgammon.isTurnOver() || (backgammon.possibleMoves() == null || backgammon.possibleMoves().isEmpty())) {
            Turn changedTurn = backgammon.changeTurn();

            backgammonRepository.save(backgammon);

           // Thread.sleep(2000);

           // buggy! this should be sent after 'MOVE' event!
            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), turnHasChangedEvent);
        }

        backgammonRepository.save(backgammon);

        BackgammonGameEvent event = BackgammonGameEvent.move(gameId, backgammon);

        simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);
    }
}