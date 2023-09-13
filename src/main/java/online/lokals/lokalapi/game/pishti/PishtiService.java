package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PishtiService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PishtiRepository pishtiRepository;

    Pishti newMatch(PishtiSession pishtiSession) {
        Pishti pishti = new Pishti(pishtiSession.getHome(), pishtiSession.getAway());

        pishti.start();

        return pishtiRepository.save(pishti);
    }

    PishtiResponse get(@Nonnull String pishtiId, Player player) {
        Pishti pishti = pishtiRepository.findById(pishtiId).orElseThrow();

        return new PishtiResponse(pishti, player.getUsername());
    }

    @Transactional
    public void play(@Nonnull String pishtiSessionId, @Nonnull String pishtiId, @Nonnull PishtiPlayRequest playRequest, Player player) {
        Pishti pishti = pishtiRepository.findById(pishtiId).orElseThrow();

        if (!pishti.getTurn().equals(player.getUsername())) {
            throw new IllegalArgumentException(String.format("turn is %s, player: %s", pishti.getTurn(), player.getUsername()));
        }

        log.trace("[{}] played {}", player.getUsername(), playRequest.card());
        pishti.play(player.getUsername(), playRequest.card());
        // simpMessagingTemplate.convertAndSend("/topic/" + pishtiId, PishtiEvent.cardPlayed(pishtiId, playRequest.card()));
        // publish card

        // CHECK STACK
        if (pishti.checkPishti()) {
            log.trace("{} made pishti by playing {}", player.getUsername(), playRequest.card());
            // publish pishti
        }
        else if (pishti.checkCapture()) {
            log.trace("{} has captured by playing {}", player.getUsername(), playRequest.card());
            // publish capture
        }

        // check remaining cards
        if (pishti.checkRoundEnded()) {
            log.trace("round ended....");
            pishti.endRound();

            if (pishti.checkGameEnded()) {
                pishti.end();
            } else {
                pishti.startNewSeries();
            }
        }
        else {
            // check hands
            pishti.dealHands();
            // change turn
            String changedTurn = pishti.changeTurn();

            simpMessagingTemplate.convertAndSend("/topic/" + pishtiId, PishtiEvent.changeTurn(pishtiId, changedTurn));
        }

        pishtiRepository.save(pishti);
    }


}
