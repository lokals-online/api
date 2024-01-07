package online.lokals.lokalapi.game.pishti;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import online.lokals.lokalapi.game.pishti.api.PishtiSessionResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;

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

    public PishtiSession create(@Nonnull Player homePlayer, Map<String, Object> gameSettings) {

        PishtiSession pishtiSession = new PishtiSession(homePlayer.getId(), homePlayer, Player.chirak(), new PishtiSettings(gameSettings));

        return pishtiSessionRepository.save(pishtiSession);
    }

    public PishtiSession get(@Nonnull String pishtiSessionId) {
        return pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();
    }

    @Transactional
    public void sit(@Nonnull String pishtiSessionId, Player opponentPlayer) {
        PishtiSession pishtiSession = pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();

        if (Objects.nonNull(pishtiSession.getAway()) && !pishtiSession.playingWithChirak()) {
            throw new IllegalArgumentException("pishti session[{}] has an away player already!");
        }

        pishtiSession.setAway(opponentPlayer);

        createNew(pishtiSession, pishtiSession.getHome());
    }

    public void checkScore(@Nonnull String pishtiSessionId, Player currentPlayer) {
        PishtiSession pishtiSession = get(pishtiSessionId);

        // check total wins
        int raceTo = pishtiSession.getSettings().getRaceTo();

        if (raceTo == pishtiSession.getHomeScore() || raceTo == pishtiSession.getAwayScore()) {
            pishtiSession.setStatus(PishtiSessionStatus.ENDED);

            pishtiSessionRepository.save(pishtiSession);

            PishtiSessionResponse pishtiSessionResponse = new PishtiSessionResponse(pishtiSession, currentPlayer);
            simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSessionResponse);
            return;
        }

        // check continuing match
        Pishti currentMatch = pishtiSession.getCurrentMatch();
        if (currentMatch.checkGameEnded()) {
            createNew(pishtiSession, currentPlayer);
        }
        else {
            PishtiSessionResponse pishtiSessionResponse = new PishtiSessionResponse(pishtiSession, currentPlayer);
            simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSessionResponse);
        }
    }

    private void createNew(PishtiSession pishtiSession, Player currentPlayer) {
        Pishti pishti = pishtiService.newMatch(pishtiSession);

        pishtiSession.addMatch(pishti);
        pishtiSession.setStatus(PishtiSessionStatus.STARTED);

        pishtiSessionRepository.save(pishtiSession);

        // notify table (new match)
        PishtiSessionResponse pishtiSessionResponse = new PishtiSessionResponse(pishtiSession, currentPlayer);
        simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSessionResponse);
    }
}
