package online.lokals.lokalapi.notification;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.backgammon.event.BackgammonSessionEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LokalNotificationService {

    private static final String MASA_TOPIC_DESTINATION_PREFIX = "/topic/masa/";
    private static final String BACKGAMMON_SESSION_TOPIC_DESTINATION = "/topic/session/";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void notifyTable(@Nonnull String tableId, LokalNotification lokalNotification) {
        String masaTopic = MASA_TOPIC_DESTINATION_PREFIX + tableId;

        simpMessagingTemplate.convertAndSend(masaTopic, lokalNotification);
    }

//    public void notifySession(@Nonnull String sessionId, BackgammonSessionEvent sessionEvent) {
//        String sessionTopic = BACKGAMMON_SESSION_TOPIC_DESTINATION + sessionId;
//
//        simpMessagingTemplate.convertAndSend(sessionTopic, lokalNotification);
//    }

}
