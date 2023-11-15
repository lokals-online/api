package online.lokals.lokalapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class UserHasActiveSession extends LokalException {

    public UserHasActiveSession(String message) {
        super(message);
    }
}
