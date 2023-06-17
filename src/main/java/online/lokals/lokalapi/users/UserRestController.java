package online.lokals.lokalapi.users;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @GetMapping(value = "/{username}", produces = "application/json;charset=UTF-8")
//    public @ResponseBody User getUserByUsername(@PathVariable String username, @AuthenticationPrincipal User currentUser) throws ResourceNotFoundException {
    public @ResponseBody User getUserByUsername(@PathVariable String username) throws ResourceNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    @PostMapping(value = "/{username}", produces = "application/json;charset=UTF-8")
//    public @ResponseBody User postUserByUsername(@PathVariable String username, @AuthenticationPrincipal User currentUser) throws ResourceNotFoundException {
    public @ResponseBody User postUserByUsername(@PathVariable String username) throws ResourceNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    @GetMapping("/test")
    public String message() {
        return messageSource.getMessage("user.hello", null, LocaleContextHolder.getLocale());
    }

    @PutMapping("/{id}")
    public @ResponseBody User updateUser(@PathVariable String id, @RequestBody User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setProfilePicture(user.getProfilePicture());

        return userRepository.save(existingUser);
    }

}

