package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PishtiSessionService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PishtiSessionRepository pishtiSessionRepository;

    private final PishtiService pishtiService;

    private final UserService userService;

    List<PishtiSession> availableSessions() {
        return pishtiSessionRepository.findPishtiSessionByStatus(PishtiSessionStatus.WAITING);
    }

    String create(@Nonnull Player homePlayer, PishtiRequest pishtiRequest) {
        Player away = null;
        if (Objects.nonNull(pishtiRequest.getOpponent())) {
            User opponent = userService.findByUsername(pishtiRequest.getOpponent());
            away = opponent.toPlayer();
        }

        PishtiSession pishtiSession = new PishtiSession(homePlayer, away, pishtiRequest.getSettings());

        pishtiSessionRepository.save(pishtiSession);

        return pishtiSession.getId();
    }

    PishtiSession get(@Nonnull String pishtiSessionId) {
        return pishtiSessionRepository.findById(pishtiSessionId).orElseThrow();
    }

    @Transactional
    public void setOpponent(@Nonnull String pishtiSessionId, Player opponentPlayer) {
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
        Map<String, Long> scoreBoard = pishtiSession.getScoreBoard();

        if (scoreBoard.containsValue((long) pishtiSession.getSettings().raceTo())) {
            pishtiSession.setStatus(PishtiSessionStatus.ENDED);

            pishtiSessionRepository.save(pishtiSession);
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

        // notify table (GameSessionEvent)
        simpMessagingTemplate.convertAndSend("/topic/" + pishtiSession.getId(), pishtiSession.getAway());
    }
}

record PishtiPlayRequest(@Nonnull Card card) {}
