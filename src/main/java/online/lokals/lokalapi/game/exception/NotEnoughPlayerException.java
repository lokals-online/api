package online.lokals.lokalapi.game.exception;

import online.lokals.lokalapi.exception.LokalException;

public class NotEnoughPlayerException extends LokalException {

    public NotEnoughPlayerException(String message) {
        super(message);
    }

    public NotEnoughPlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
