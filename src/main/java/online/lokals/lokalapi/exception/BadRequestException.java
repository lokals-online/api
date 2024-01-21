package online.lokals.lokalapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends LokalException {

    public BadRequestException() {
        super("Resource not found!");
    }

    public BadRequestException(String message) {
        super(message);
    }
}