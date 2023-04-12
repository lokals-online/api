package online.lokals.lokalapi.users;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.common.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public @ResponseBody User getUserByUsername(@PathVariable String username) throws ResourceNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    @PutMapping("/{id}")
    public @ResponseBody User updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setProfilePicture(user.getProfilePicture());

        return userRepository.save(existingUser);
    }

}

