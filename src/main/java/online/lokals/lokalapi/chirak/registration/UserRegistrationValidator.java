package online.lokals.lokalapi.chirak.registration;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.users.UserAccountService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
class UserRegistrationValidator implements Validator {

    private final UserAccountService userAccountService;

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UserRegistrationRequest.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        if (errors.hasErrors()) {
            return;
        }

        UserRegistrationRequest userRegistrationRequest = (UserRegistrationRequest) obj;

        if (userAccountService.existsByUsername(userRegistrationRequest.getUsername())) {
            String[] args = new String[] {userRegistrationRequest.getUsername()};
            errors.rejectValue(
                    "username",
                    "taken",
                    messageSource.getMessage("userRegistrationRequest.username.taken", args, LocaleContextHolder.getLocale())
            );
        }
    }
}