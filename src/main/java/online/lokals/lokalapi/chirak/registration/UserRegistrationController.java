package online.lokals.lokalapi.chirak.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserAccountService;
import online.lokals.lokalapi.users.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserService userService;

    private final UserRegistrationValidator userRegistrationValidator;

    private final PasswordEncoder passwordEncoder;
    
    private final UserAccountService userAccountService;

//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        final DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
//        resolver.setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE);
//
//        binder.setMessageCodesResolver(resolver);
//        binder.addValidators(userRegistrationValidator);
//    }

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

    @PostMapping("/chirak/registerAnonymous")
    public ResponseEntity<Object> updateUsername(
            @Valid @RequestBody RegisterAnonymousRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser
    ) {
        if (bindingResult.hasErrors() || currentUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        try {
            userService.findByIdOptional(request.getId())
                .ifPresentOrElse(user -> {
                    user.setUsername(request.getUsername());

                    userService.update(user);
                }, () -> {
                    String encodedPassword = passwordEncoder.encode(User.ANONYMOUS_PASSWORD);
                    userService.register(request.getId(), request.getUsername(), encodedPassword);
                });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

