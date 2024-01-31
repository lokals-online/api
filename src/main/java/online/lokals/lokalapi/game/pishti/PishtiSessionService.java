package online.lokals.lokalapi.game.pishti;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import online.lokals.lokalapi.game.pishti.api.PishtiSessionResponse;
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
public class PishtiSessionService {

    private static final String PISHTI_SESSION_TOPIC_DESTINATION = "/topic/session/pishti/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PishtiSessionRepository pishtiSessionRepository;

    private final PishtiService pishtiService;


    List<PishtiSession> availableSessions() {
        return pishtiSessionRepository.findPishtiSessionByStatus(PishtiSessionStatus.WAITING);
    }

    public PishtiSession create(@Nonnull User homePlayer, @NotNull String opponent, Map<String, Object> gameSettings) {

//        Player awayPlayer = Player.chirak().getId().equals(opponent) ? Player.chirak() : null;
        User away = User.chirak().getId().equals(opponent) ? User.chirak() : null;

        PishtiSession pishtiSession = new PishtiSession(homePlayer.getId(), homePlayer, away, new PishtiSettings(gameSettings));

        pishtiSessionRepository.save(pishtiSession);

        if (pishtiSession.playingWithChirak()) {
            createNew(pishtiSession);
        }

        return pishtiSession;
    }

    public PishtiSession get(@Nonnull String pishtiSessionId) {
        return pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();
    }

    @Transactional
    public void sit(@Nonnull String pishtiSessionId, User opponent) {
        PishtiSession pishtiSession = pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();

        if (Objects.nonNull(pishtiSession.getAway()) && !pishtiSession.playingWithChirak()) {
            throw new IllegalArgumentException("pishti session[{}] has an away player already!");
        }

        pishtiSession.setAway(opponent);

        createNew(pishtiSession);
    }

    public void checkScore(@Nonnull String pishtiSessionId, Player currentPlayer) {
        PishtiSession pishtiSession = get(pishtiSessionId);

        // check total wins
        int raceTo = pishtiSession.getSettings().getRaceTo();

        if (raceTo == pishtiSession.getHomeScore() || raceTo == pishtiSession.getAwayScore()) {
            pishtiSession.setStatus(PishtiSessionStatus.ENDED);

            pishtiSessionRepository.save(pishtiSession);

            PishtiSessionResponse pishtiSessionResponse = new PishtiSessionResponse(pishtiSession);
            simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSessionResponse);
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

        if (pishtiSession.playingWithChirak() && Player.chirak().getId().equals(Objects.requireNonNull(pishti.getTurn()))) {
            pishti.start();

            pishtiService.playForChirak(pishti.getId());
        }

        // notify table (new match)
        PishtiSessionResponse pishtiSessionResponse = new PishtiSessionResponse(pishtiSession);
        simpMessagingTemplate.convertAndSend(PISHTI_SESSION_TOPIC_DESTINATION + pishtiSession.getId(), pishtiSessionResponse);
    }
}
