package online.lokals.lokalapi.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventService {

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        log.info(event.toString());
        // TODO notify table!
    }

    @EventListener
    public void onConnectEvent(SessionConnectEvent event) {
        log.info(event.toString());
    }

    @EventListener
    public void onConnectEvent(SessionConnectedEvent event) {
        log.info(event.toString());
    }

}
