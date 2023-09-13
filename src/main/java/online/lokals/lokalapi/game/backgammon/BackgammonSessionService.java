package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.BackgammonRequest;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.backgammon.event.BackgammonGameEvent;
import online.lokals.lokalapi.game.backgammon.event.BackgammonSessionEvent;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgammonSessionService {

    private static final String BACKGAMMON_SESSION_TOPIC_DESTINATION = "/topic/session/";
    private static final String DUKKAN_TOPIC_DESTINATION = "/topic/dukkan/";

    private final BackgammonSessionRepository backgammonSessionRepository;
    private final BackgammonService backgammonService;
    private final UserService userService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    List<BackgammonSession> availableSessions() {
        List<BackgammonSessionStatus> availableSessions = List.of(BackgammonSessionStatus.WAITING, BackgammonSessionStatus.STARTED);
        return backgammonSessionRepository.findBackgammonSessionsByStatusIn(availableSessions);
    }

    String create(@Nonnull Player homePlayer, BackgammonRequest backgammonRequest) {
        Player away = null;
        if (Objects.nonNull(backgammonRequest.getOpponent())) {
            User opponent = userService.findByUsername(backgammonRequest.getOpponent());
            away = opponent.toPlayer();
        }

        BackgammonSession backgammonSession = new BackgammonSession(homePlayer, away, backgammonRequest.getSettings());

        backgammonSessionRepository.save(backgammonSession);

        BackgammonTableEvent sessionCreated = BackgammonTableEvent.created(backgammonSession);
        simpMessagingTemplate.convertAndSend(DUKKAN_TOPIC_DESTINATION, sessionCreated);

        return backgammonSession.getId();
    }

    public BackgammonSession get(String backgammonSessionId) {
        return backgammonSessionRepository.findById(backgammonSessionId).orElseThrow();
    }

    public void setOpponent(String backgammonSessionId, Player opponent) {
        BackgammonSession backgammonSession = backgammonSessionRepository.findById(backgammonSessionId).orElseThrow();

        if (Objects.nonNull(backgammonSession.getAway())) {
            throw new IllegalArgumentException("backgammon session[{}] has an away player already!");
        }

        backgammonSession.setAway(opponent);
        backgammonSessionRepository.save(backgammonSession);

        BackgammonSessionEvent event = BackgammonSessionEvent.sit(backgammonSession);
        simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), event);
    }

    @SneakyThrows
    public void firstDice(String backgammonSessionId, Player player) {
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
        // publish firstDice
        BackgammonSessionEvent event = BackgammonSessionEvent.firstDice(backgammonSession);
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
        Map<String, Long> scoreBoard = backgammonSession.getScoreBoard();

        if (!backgammonSession.getStatus().equals(BackgammonSessionStatus.ENDED) &&
                scoreBoard.containsValue((long) backgammonSession.getSettings().raceTo())) {

            backgammonSession.setStatus(BackgammonSessionStatus.ENDED);
            backgammonSessionRepository.save(backgammonSession);
            return;
        }

        // check continuing match
        Backgammon currentMatch = backgammonSession.getCurrentMatch();
        if (currentMatch.isGameOver()) {
            startNew(backgammonSession);
        }
    }

    private Backgammon startNew(BackgammonSession backgammonSession) {
        Backgammon backgammon = backgammonService.newMatchForSession(backgammonSession);

        backgammonSession.addMatch(backgammon);
        backgammonSession.setStatus(BackgammonSessionStatus.STARTED);

        backgammonSessionRepository.save(backgammonSession);

        return backgammon;
        // notify table (GameSessionEvent)
        // simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + backgammonSession.getId(), backgammonSession.getAway());
    }
}
