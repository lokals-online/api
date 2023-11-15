package online.lokals.lokalapi.chirak.registration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserService userService;

    private final UserRegistrationValidator userRegistrationValidator;

    private final PasswordEncoder passwordEncoder;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        final DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
        resolver.setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE);

        binder.setMessageCodesResolver(resolver);
        binder.addValidators(userRegistrationValidator);
    }

    @PostMapping("/chirak/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        try {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            userService.register(request.getUsername(), encodedPassword);

            return ResponseEntity.created(URI.create("/login")).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

