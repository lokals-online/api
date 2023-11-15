package online.lokals.lokalapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class WrongBackgammonSettings extends LokalException {

    public WrongBackgammonSettings(String message) {
        super(message);
    }

    public WrongBackgammonSettings(Map<String, Object> settings) {

        this(settings.toString());
    }
}
