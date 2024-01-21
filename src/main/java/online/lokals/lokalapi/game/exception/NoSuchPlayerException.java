package online.lokals.lokalapi.game.exception;

import online.lokals.lokalapi.exception.LokalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class NoSuchPlayerException extends LokalException {
    public NoSuchPlayerException(String message) {
        super(message);
    }
}
