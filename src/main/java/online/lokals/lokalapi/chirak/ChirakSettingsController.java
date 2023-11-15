package online.lokals.lokalapi.chirak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chirak/settings")
public class ChirakSettingsController {

    private final Chirak chirak;

    public ChirakSettingsController(@Autowired Chirak chirak) {
        this.chirak = chirak;
    }

    @PostMapping
    public ChirakSettingsResponse settings(@AuthenticationPrincipal User user) {

//        log.trace(Objects.requireNonNull(promptBody).toString());

//        return chirak.promptByKey("registration");
        return new ChirakSettingsResponse("gameKey");
    }

}

@Data
@AllArgsConstructor
class ChirakSettingsResponse {
    private String gameKey;
//    private GameSession gameSession;
}