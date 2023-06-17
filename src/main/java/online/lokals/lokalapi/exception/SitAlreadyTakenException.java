package online.lokals.lokalapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SitAlreadyTakenException extends LokalException {

    public SitAlreadyTakenException(int sitIndex) {
        super("sit[%s] is full".formatted(sitIndex));
    }

    public SitAlreadyTakenException(String message) {
        super(message);
    }

    public SitAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
