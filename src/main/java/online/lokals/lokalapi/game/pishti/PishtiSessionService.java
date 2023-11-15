package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.backgammon.event.BackgammonSessionEvent;
import online.lokals.lokalapi.users.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PishtiSessionService {

    private static final String PISHTI_SESSION_TOPIC_DESTINATION = "/topic/session/pishti/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PishtiSessionRepository pishtiSessionRepository;

    private final PishtiService pishtiService;


    List<PishtiSession> availableSessions() {
        return pishtiSessionRepository.findPishtiSessionByStatus(PishtiSessionStatus.WAITING);
    }

    public PishtiSession create(
            @Nonnull String tableId,
            @Nonnull Player homePlayer,
            @Nullable Player awayPlayer,
            Map<String, Object> gameSettings
    ) {

        PishtiSession pishtiSession = new PishtiSession(tableId, homePlayer, awayPlayer, new PishtiSettings(gameSettings));

        return pishtiSessionRepository.save(pishtiSession);
    }

    public PishtiSession get(@Nonnull String pishtiSessionId) {
        return pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();
    }

    @Transactional
    public void sit(@Nonnull String pishtiSessionId, Player opponentPlayer) {
        PishtiSession pishtiSession = pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();

        if (Objects.nonNull(pishtiSession.getAway())) {
            throw new IllegalArgumentException("pishti session[{}] has an away player already!");
        }

        pishtiSession.setAway(opponentPlayer);

        createNew(pishtiSession);
    }

    public void checkScore(@Nonnull String pishtiSessionId) {
        PishtiSession pishtiSession = get(pishtiSessionId);

        // check total wins
        int raceTo = pishtiSession.getSettings().getRaceTo();

        if (raceTo == pishtiSession.getHomeScore() || raceTo == pishtiSession.getAwayScore()) {
            pishtiSession.setStatus(PishtiSessionStatus.ENDED);

            pishtiSessionRepository.save(pishtiSession);

            simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSession);
            return;
        }

        // check continuing match
        Pishti currentMatch = pishtiSession.getCurrentMatch();
        if (currentMatch.checkGameEnded()) {
            createNew(pishtiSession);
        }
    }

    private void createNew(PishtiSession pishtiSession) {
        Pishti pishti = pishtiService.newMatch(pishtiSession);

        pishtiSession.addMatch(pishti);
        pishtiSession.setStatus(PishtiSessionStatus.STARTED);

        pishtiSessionRepository.save(pishtiSession);

        // notify table (new match)
        simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSession);
    }
}
