package online.lokals.lokalapi.table;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class TableValidator implements Validator {

    private final MessageSource messageSource;

    public TableValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object obj, Errors errors) {

        CreateTableRequest request = (CreateTableRequest) obj;

        String gameId = request.getGameId();
        if (!Objects.equals(gameId, "backgammon")) {
            errors.rejectValue(
                    "gameId",
                    "invalid",
                    messageSource.getMessage("createTableRequest.gameId.invalid", new String[] {gameId}, LocaleContextHolder.getLocale()));
        }
    }

}
