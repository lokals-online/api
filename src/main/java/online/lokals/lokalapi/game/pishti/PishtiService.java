package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.pishti.api.PishtiPlayRequest;
import online.lokals.lokalapi.game.pishti.event.PishtiEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PishtiService {

    private static final String PISHTI_TOPIC_DESTINATION = "/topic/game/pishti/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PishtiRepository pishtiRepository;

    Pishti newMatch(PishtiSession pishtiSession) {
        Pishti pishti = new Pishti(pishtiSession.getHome(), pishtiSession.getAway());

        pishti.start();

        return pishtiRepository.save(pishti);
    }

    public PishtiResponse get(@Nonnull String pishtiId, Player player) {
        Pishti pishti = pishtiRepository.findById(pishtiId).orElseThrow();

        return new PishtiResponse(pishti, player);
    }

    @Transactional
    public void play(@Nonnull String pishtiSessionId, @Nonnull String pishtiId, @Nonnull PishtiPlayRequest playRequest, Player player) {
        Pishti pishti = pishtiRepository.findById(pishtiId).orElseThrow();

        if (!pishti.getTurn().equals(player.getId())) {
            throw new IllegalArgumentException(String.format("turn is %s, player: %s", pishti.getTurn(), player.getId()));
        }

        log.trace("[{}] played {}", player.getUsername(), playRequest.card());
        
        pishti.play(player.getId(), playRequest.card());
        
        // publish card (omit for now)
        // simpMessagingTemplate.convertAndSend(PISHTI_TOPIC_DESTINATION + pishtiId, PishtiEvent.cardPlayed(pishtiId, playRequest.card()));

        // CHECK STACK
        if (pishti.checkPishti()) {
            log.trace("{} made pishti by playing {}", player.getUsername(), playRequest.card());
            // publish pishti
        }
        else if (pishti.checkCapture()) {
            log.trace("{} has captured by playing {}", player.getUsername(), playRequest.card());
            // publish capture
        }

        if (pishti.checkGameEnded()) {
            log.info("game ended");
            log.trace("first player score: {}", pishti.getFirstPlayer().getScore());
            log.trace("second player score: {}", pishti.getSecondPlayer().getScore());

            pishti.end();
        }

        // check remaining cards
        if (pishti.checkRoundEnded()) {
            log.trace("round ended....");
            pishti.endRound();

            log.trace("checking game ended!");
            
            pishti.startNewSeries();
        }
        else {
            // check hands
            pishti.dealHands();
            // change turn
            String changedTurn = pishti.changeTurn();
        }

        pishtiRepository.save(pishti);

        simpMessagingTemplate.convertAndSend(PISHTI_TOPIC_DESTINATION + pishtiId, PishtiEvent.changeTurn(pishtiId, pishti.getTurn()));
    }


}
