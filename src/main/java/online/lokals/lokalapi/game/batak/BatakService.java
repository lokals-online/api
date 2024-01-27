package online.lokals.lokalapi.game.batak;

import java.util.Objects;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.batak.api.BatakResponse;
import online.lokals.lokalapi.game.batak.event.BatakEvent;
import online.lokals.lokalapi.game.card.Card;
import online.lokals.lokalapi.game.card.CardType;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatakService {
    
    private static final String BATAK_TOPIC_DESTINATION = "/topic/game/batak/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final BatakRepository batakRepository;

    public BatakResponse get(@Nonnull String pishtiId, Player player) {
        Batak batak = batakRepository.findById(pishtiId).orElseThrow();

        return new BatakResponse(batak, player);
    }

    public Batak newMatch(BatakSession batakSession) {

        Batak batak = new Batak(batakSession.getPlayers(), batakSession.getDealerIndex());
        
        batakRepository.save(batak);
        
        return batak;
    }

    public void bid(String batakId, Player player, Integer betValue) {
        Batak batak = batakRepository.findById(batakId).orElseThrow();

        if (!batak.getStatus().equals(BatakStatus.BIDDING)) {
            // throw
            return;
        }

        if (Objects.nonNull(betValue)) {
            batak.bid(player, betValue);
        } 

        // change turn!
        batak.changeBidTurn();

        if (batak.getTurn().equals(batak.getBid().getPlayerId())) {
            log.debug("bidding completed. starting game..");

            batak.setStatus(BatakStatus.WAITING_TRUMP);
        }

        batakRepository.save(batak);

        // publish
        simpMessagingTemplate.convertAndSend(BATAK_TOPIC_DESTINATION + batakId, BatakEvent.changeTurn(batakId, batak.getTurn()));
    }

    public void chooseTrump(String batakId, Player player, CardType cardType) {
        Batak batak = batakRepository.findById(batakId).orElseThrow();

        if (!batak.getStatus().equals(BatakStatus.WAITING_TRUMP)) {
            // throw
            return;
        }

        batak.setTrump(cardType);

        batak.start();

        batakRepository.save(batak);

        simpMessagingTemplate.convertAndSend(BATAK_TOPIC_DESTINATION + batakId, BatakEvent.changeTurn(batakId, batak.getTurn()));
    }

    public void play(String batakId, Player player, Card card) {
        Batak batak = batakRepository.findById(batakId).orElseThrow();

        log.trace("player[{}] played card[{}]", player, card);

        // validate(player, card);
         if (batak.getCurrentTrick().getMoves().stream().anyMatch(batakMove -> batakMove.getPlayerId().equals(player.getId()))) {
             return;
         }
        // (stack.last < card) then check player hand
        // (stack.first.type != card.type) 
        // (first card && trump card) then check trump is played before

        batak.play(player.getId(), card);
        batakRepository.save(batak);

//        simpMessagingTemplate.convertAndSend(BATAK_TOPIC_DESTINATION + batakId, BatakEvent.changeTurn(batakId, batak.getTurn()));

        if (batak.currentTrickEnded()) {
            batak.endTrick();

            if (batak.gameEnded()) {
                batak.setStatus(BatakStatus.ENDED);
            }
            else {
                batak.newTrick();
            }
        }
        else {
            batak.changeTurn();
        }

        batakRepository.save(batak);
        simpMessagingTemplate.convertAndSend(BATAK_TOPIC_DESTINATION + batakId, BatakEvent.changeTurn(batakId, batak.getTurn()));
    }

    public Batak restart(BatakSession batakSession) {
        Batak batak = new Batak(batakSession.getPlayers(), batakSession.getDealerIndex()-1);

        batakRepository.save(batak);

        return batak;
    }
}
