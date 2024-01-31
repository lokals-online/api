package online.lokals.lokalapi.game.batak;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import online.lokals.lokalapi.game.batak.event.BatakSessionEvent;
import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.PishtiSettings;
import online.lokals.lokalapi.users.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatakSessionService {

    private static final String BATAK_SESSION_TOPIC_DESTINATION = "/topic/session/batak/";

    private final BatakSessionRepository batakSessionRepository;
    
    private final BatakService batakService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public BatakSession create(
            @Nonnull User user,
            Map<String, Object> gameSettings
    ) {
        BatakSession batakSession = new BatakSession(user.getId(), user, new BatakSettings(gameSettings));

        return batakSessionRepository.save(batakSession);
    }

    public BatakSession get(@Nonnull String batakSessionId) {
        return batakSessionRepository.findById(batakSessionId).orElseThrow();
    }

    @Transactional
    public void sit(@Nonnull String batakSessionId, @Nonnull User user) {
        BatakSession batakSession = batakSessionRepository.findById(batakSessionId).orElseThrow();

        batakSession.addUser(user);
        
        if (batakSession.isReadyToStart()) {
            log.info("batak[" + batakSessionId + "] is ready to start");

            Batak batak = batakService.newMatch(batakSession);

            batakSession.addMatch(batak);
            batakSession.setStatus(BatakSessionStatus.WAITING_BETS);

            BatakSessionEvent startEvent = BatakSessionEvent.start(batakSession);
            simpMessagingTemplate.convertAndSend(BATAK_SESSION_TOPIC_DESTINATION + batakSession.getId(), startEvent);
        }

        batakSessionRepository.save(batakSession);

        BatakSessionEvent sitEvent = BatakSessionEvent.sit(batakSession);
        simpMessagingTemplate.convertAndSend(BATAK_SESSION_TOPIC_DESTINATION + batakSession.getId(), sitEvent);
    }

    public void quit(@Nonnull String batakSessionId, @Nonnull Player player) {
        BatakSession batakSession = get(batakSessionId);

        if (batakSession.getStatus().equals(BatakSessionStatus.ENDED)) {
            return;
        }

        batakSession.removePlayer(player);

        log.info("player[{}] removed", player);
        
        batakSession.setStatus(BatakSessionStatus.WAITING_PLAYERS);

        batakSessionRepository.save(batakSession);

        // BackgammonSessionEvent quitEvent = BackgammonSessionEvent.quit(batakSession);
        // simpMessagingTemplate.convertAndSend(BACKGAMMON_SESSION_TOPIC_DESTINATION + batakSession.getId(), quitEvent);
    }

    public void check(@Nonnull String batakSessionId) {
        BatakSession batakSession = get(batakSessionId);

        log.trace("checking batak session[{}]..", batakSession);
        if (Objects.nonNull(batakSession.getCurrentMatch()) && batakSession.getCurrentMatch().getStatus().equals(BatakStatus.ENDED)) {

            Optional<Map.Entry<String, Integer>> winner = batakSession.getScores().entrySet().stream()
                    .filter(entrySet -> entrySet.getValue() >= batakSession.getSettings().getRaceTo())
                    .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                    .findFirst();

            if (winner.isPresent()) {
                batakSession.setStatus(BatakSessionStatus.ENDED);
            }
            else {
                // create new batak
                Batak newMatch = batakService.newMatch(batakSession);

                batakSession.addMatch(newMatch);
                batakSession.setStatus(BatakSessionStatus.WAITING_BETS);
            }

            batakSessionRepository.save(batakSession);

            simpMessagingTemplate.convertAndSend(BATAK_SESSION_TOPIC_DESTINATION + batakSession.getId(), batakSession);
        }
    }

    public void restart(@Nonnull String batakSessionId) {
        BatakSession batakSession = get(batakSessionId);
        if (batakSession.getCurrentMatch() == null) {
            return;
        }

        Batak batak = batakService.restart(batakSession);
        batakSession.setCurrentMatch(batak);

        batakSessionRepository.save(batakSession);

        simpMessagingTemplate.convertAndSend(BATAK_SESSION_TOPIC_DESTINATION + batakSession.getId(), batakSession);
    }

    public void start(String batakSessionId) {
        BatakSession batakSession = get(batakSessionId);

        batakSessionRepository.save(batakSession);

        simpMessagingTemplate.convertAndSend(BATAK_SESSION_TOPIC_DESTINATION + batakSession.getId(), batakSession);
    }
}