package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.backgammon.event.BackgammonGameEvent;
import online.lokals.lokalapi.game.backgammon.event.BackgammonMoveEvent;
import online.lokals.lokalapi.game.batak.Batak;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        log.trace("{} is rolling dice", player.toString());
        Backgammon backgammon = get(gameId);
        
        assert Objects.equals(Objects.requireNonNull(backgammon.currentTurn()).getPlayerId(), player.getId());
        
        Integer[] rolledDice = backgammon.rollDice();

        log.trace("{} has rolled dice: {}", player, Arrays.toString(rolledDice));

        backgammonRepository.save(backgammon);

        BackgammonGameEvent event = BackgammonGameEvent.rollDice(backgammon.getId(), backgammon);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);

        Thread.sleep(1000);

        Set<BackgammonMove> possibleMoves = backgammon.possibleMoves();
        if (possibleMoves != null && possibleMoves.isEmpty()) {
            Turn changedTurn = backgammon.changeTurn();
            backgammonRepository.save(backgammon);

            Thread.sleep(1000);

            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), turnHasChangedEvent);
        }
    }

    @SneakyThrows
    public void move(@Nonnull String gameId, BackgammonPlayRequest playRequest, Player player) {
        Backgammon backgammon = get(gameId);

        // validate move: checkPlayer, checkHitPieces, checkDestinations
        Set<BackgammonMove> possibleMoves = backgammon.possibleMoves();
        if (possibleMoves != null && !possibleMoves.isEmpty()) {

            boolean isValid = playRequest.moves().stream()
                    .anyMatch(backgammonMove -> Objects.requireNonNull(possibleMoves)
                            .stream()
                            .anyMatch(backgammonMove1 -> backgammonMove1.isSame(backgammonMove)));

            if (!isValid) throw new IllegalArgumentException(String.format("it is not a valid move %s", playRequest.moves()));
        }

        // persist move
        for (BackgammonMove move : playRequest.moves()) {
            backgammon.move(player, move);

            // TODO: fix!
            backgammonRepository.save(backgammon);

            BackgammonGameEvent event = BackgammonGameEvent.move(gameId, backgammon);

            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);

            Thread.sleep(500);
        }

        if (backgammon.isGameOver()) {
            backgammonRepository.save(backgammon);

            BackgammonGameEvent gameOverEvent = BackgammonGameEvent.gameOver(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), gameOverEvent);

            return;
        }

        if (backgammon.isTurnOver() || backgammon.possibleMoves() == null || Objects.requireNonNull(backgammon.possibleMoves()).isEmpty()) {
            Turn changedTurn = backgammon.changeTurn();

            log.info("turn has changed to: {}", changedTurn.getPlayerId());

            backgammonRepository.save(backgammon);
            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), turnHasChangedEvent);


            if (Player.chirak().getId().equals(changedTurn.getPlayerId())) {
                Thread.sleep(1000);

                this.playForChirak(gameId);
            }
        }

//        backgammonRepository.save(backgammon);

//        BackgammonGameEvent event = BackgammonGameEvent.move(gameId, backgammon);
//
//        simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);
    }

    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void playForChirak(@Nonnull String backgammonId) {
        Backgammon backgammon = get(backgammonId);
        if (Objects.requireNonNull(backgammon.currentTurn()).getPlayerId().equals(Player.chirak().getId())) {

            log.debug("playing for chirak turn[{}]", backgammon.currentTurn());
            backgammon.rollDice();
            backgammonRepository.save(backgammon);

            BackgammonGameEvent event = BackgammonGameEvent.rollDice(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), event);

            Thread.sleep(1000);

            while (!backgammon.isTurnOver() && !Objects.requireNonNull(backgammon.possibleMoves()).isEmpty()) {
                backgammon = get(backgammonId);
                Optional<BackgammonMove> firstMove = backgammon.possibleMoves().stream().findFirst();
                Backgammon finalBackgammon = backgammon;
                firstMove.ifPresent(backgammonMove -> finalBackgammon.move(Player.chirak(), backgammonMove));
                log.debug("selected move : [{}]", firstMove.get());

                backgammonRepository.save(backgammon);

                BackgammonGameEvent moveEvent = BackgammonGameEvent.move(backgammonId, backgammon);
                simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), moveEvent);
                log.debug("sent moveEvent {}", moveEvent);

                Thread.sleep(1000);
            }

            Turn changedTurn = backgammon.changeTurn();
            backgammonRepository.save(backgammon);

            BackgammonGameEvent turnHasChangedEvent = BackgammonGameEvent.turnHasChanged(backgammon.getId(), backgammon);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_TOPIC_DESTINATION + backgammon.getId(), turnHasChangedEvent);
        }
    }

    public Backgammon restart(BackgammonSession backgammonSession) {
        return newMatchForSession(backgammonSession);
    }
}