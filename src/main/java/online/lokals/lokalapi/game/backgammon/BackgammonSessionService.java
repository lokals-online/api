package online.lokals.lokalapi.game.backgammon;

import java.util.Map;
import java.util.Objects;

import online.lokals.lokalapi.game.batak.Batak;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.backgammon.event.BackgammonSessionEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgammonSessionService {

    private static final String BACKGAMMON_SESSION_TOPIC_DESTINATION = "/topic/session/backgammon/";

    private final BackgammonSessionRepository backgammonSessionRepository;
    private final BackgammonService backgammonService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public BackgammonSession create(
            @Nonnull Player homePlayer,
            @Nullable Player awayPlayer,
            Map<String, Object> gameSettings
    ) {

        BackgammonSession backgammonSession = new BackgammonSession(homePlayer, awayPlayer, new BackgammonSettings(gameSettings));

        backgammonSessionRepository.save(backgammonSession);

        return backgammonSession;
    }

    public BackgammonSession create(@Nonnull Player homePlayer, Map<String, Object> gameSettings) {

        BackgammonSession backgammonSession = new BackgammonSession(homePlayer, Player.chirak(), new BackgammonSettings(gameSettings));

        backgammonSessionRepository.save(backgammonSession);

        return backgammonSession;
    }

    public BackgammonSession get(@Nonnull String backgammonSessionId) {
        return backgammonSessionRepository.findById(backgammonSessionId).orElseThrow();
    }

    public void sit(@Nonnull String sessionId, @Nonnull Player opponent) {
        BackgammonSession backgammonSession = backgammonSessionRepository.findById(sessionId).orElseThrow();

        if (Objects.nonNull(backgammonSession.getAway()) && !backgammonSession.playingWithChirak()) {
            throw new IllegalArgumentException("backgammon session[{}] has an away player already!");
        }

        backgammonSession.setAway(opponent);
        if (backgammonSession.getMatches().isEmpty()) {
            // then it is a new session!
            backgammonSession.setStatus(BackgammonSessionStatus.WAITING);
        }
        else {
            backgammonSession.setStatus(BackgammonSessionStatus.STARTED);
        }
        backgammonSessionRepository.save(backgammonSession);

        BackgammonSessionEvent event = BackgammonSessionEvent.sit(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), event);
    }

    @SneakyThrows
    public void firstDie(@Nonnull String backgammonSessionId, @Nonnull Player player) {
        BackgammonSession backgammonSession = get(backgammonSessionId);

        if (Objects.equals(backgammonSession.getHome().getId(), player.getId())) {
            int dice = (int) (Math.random() * 6 + 1);
            if (Objects.nonNull(backgammonSession.getAwayFirstDice()) && backgammonSession.getAwayFirstDice().equals(dice)) {
                boolean dicesEqual = true;
                while (dicesEqual) {
                    int rolledAgain = (int) (Math.random() * 6 + 1);
                    if (rolledAgain != dice) {
                        dice = rolledAgain;
                        dicesEqual = false;
                    }
                }
            }
            backgammonSession.setHomeFirstDice(dice);

            if (backgammonSession.playingWithChirak()) {
                int chirakDice = (int) (Math.random() * 6 + 1);
                while (chirakDice == dice) {
                    int rolledAgain = (int) (Math.random() * 6 + 1);
                    if (rolledAgain != chirakDice) {
                        chirakDice = rolledAgain;
                    }
                }
                backgammonSession.setAwayFirstDice(chirakDice);
            }
        }
        else if (Objects.nonNull(backgammonSession.getAway()) && Objects.equals(backgammonSession.getAway().getId(), player.getId())) {
            int dice = (int) (Math.random() * 6 + 1);
            if (Objects.nonNull(backgammonSession.getHomeFirstDice()) && backgammonSession.getHomeFirstDice().equals(dice)) {
                boolean dicesEqual = true;
                while (dicesEqual) {
                    int rolledAgain = (int) (Math.random() * 6 + 1);
                    if (rolledAgain != dice) {
                        dice = rolledAgain;
                        dicesEqual = false;
                    }
                }
            }
            backgammonSession.setAwayFirstDice(dice);
        }

        backgammonSessionRepository.save(backgammonSession);

        // publish firstDie
        BackgammonSessionEvent event = BackgammonSessionEvent.firstDie(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), event);

        if (backgammonSession.isReady()) {
            Backgammon backgammon = startNew(backgammonSession);

            backgammonSessionRepository.save(backgammonSession);

            BackgammonSessionEvent startEvent = BackgammonSessionEvent.start(backgammonSession);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), startEvent);
        }
    }

    public void checkScore(String backgammonSessionId) {
        BackgammonSession backgammonSession = get(backgammonSessionId);

        // check total wins
        // Map<String, Integer> scoreBoard = backgammonSession.getScoreBoard();

        if (backgammonSession.hasEnded()) {

            backgammonSession.setStatus(BackgammonSessionStatus.ENDED);
            backgammonSessionRepository.save(backgammonSession);
            
            BackgammonSessionEvent endEvent = BackgammonSessionEvent.end(backgammonSession);
            simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), endEvent);

            // simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId() + "/scoreBoard", scoreBoard);

            return;
        }

        // check continuing match
        Backgammon currentMatch = backgammonSession.getCurrentMatch();
        if (currentMatch.isGameOver()) {
            startNew(backgammonSession);
        }

    }

    public void quit(@Nonnull String backgammonSessionId, @Nonnull Player player) {
        BackgammonSession backgammonSession = get(backgammonSessionId);

        if (backgammonSession.getStatus().equals(BackgammonSessionStatus.ENDED)) {
            return;
        }

        if (player.equals(backgammonSession.getHome())) {
            backgammonSession.setHome(null);
        }
        else if (player.equals(backgammonSession.getAway())) {
            backgammonSession.setAway(null);
        }
        backgammonSession.setStatus(BackgammonSessionStatus.WAITING_OPPONENT);

        if (Objects.nonNull(backgammonSession.getCurrentMatch())) {
            backgammonSession.getCurrentMatch().quitPlayer(player);
        }

        backgammonSessionRepository.save(backgammonSession);

        BackgammonSessionEvent quitEvent = BackgammonSessionEvent.quit(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), quitEvent);
    }

    public void restart(String backgammonSessionId, Player player) {
        BackgammonSession backgammonSession = get(backgammonSessionId);

        if (backgammonSession.getCurrentMatch() == null) {
            return;
        }

        Backgammon backgammon = backgammonService.restart(backgammonSession);
        backgammonSession.setCurrentMatch(backgammon);

        backgammonSessionRepository.save(backgammonSession);

        BackgammonSessionEvent quitEvent = BackgammonSessionEvent.quit(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), quitEvent);
    }

    private Backgammon startNew(BackgammonSession backgammonSession) {
        Backgammon backgammon = backgammonService.newMatchForSession(backgammonSession);

        backgammonSession.addMatch(backgammon);
        backgammonSession.setStatus(BackgammonSessionStatus.STARTED);

        backgammonSessionRepository.save(backgammonSession);

        if (backgammonSession.playingWithChirak() &&
                Player.chirak().getId().equals(Objects.requireNonNull(backgammon.currentTurn()).getPlayerId())) {
            backgammon.start();

            backgammonService.playForChirak(backgammon.getId());
        }

        BackgammonSessionEvent startEvent = BackgammonSessionEvent.start(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), startEvent);

        return backgammon;
    }
}
