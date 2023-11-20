package online.lokals.lokalapi.game.backgammon.api;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.backgammon.*;
import online.lokals.lokalapi.users.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tavla")
public class BackgammonSessionController {

    private final BackgammonSessionService backgammonSessionService;
    private final BackgammonService backgammonService;

    @GetMapping("/{sessionId}")
    private ResponseEntity<BackgammonSessionResponse> fetch(@PathVariable String sessionId) {
        BackgammonSession backgammonSession = backgammonSessionService.get(sessionId);

        return ResponseEntity.ok(new BackgammonSessionResponse(backgammonSession));
    }

    @PostMapping("/{sessionId}/sit")
    private ResponseEntity<Void> sit(@PathVariable String sessionId, @AuthenticationPrincipal User currentUser) {
        backgammonSessionService.sit(sessionId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/firstDie")
    private ResponseEntity<Void> firstDie(@PathVariable String sessionId, @AuthenticationPrincipal User currentUser) {
        backgammonSessionService.firstDie(sessionId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/game/{backgammonId}")
    private ResponseEntity<BackgammonResponse> getMatch(
            @PathVariable String sessionId,
            @PathVariable String backgammonId
    ) {
        Backgammon backgammon = backgammonService.get(backgammonId);

        return ResponseEntity.ok(new BackgammonResponse(backgammon));
    }

    @PostMapping("/{sessionId}/game/{backgammonId}/rollDice")
    private ResponseEntity<Void> rollDice(
            @PathVariable String sessionId,
            @PathVariable String backgammonId,
            @AuthenticationPrincipal User currentUser
    ) {
        backgammonService.rollDice(backgammonId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/game/{backgammonId}/move")
    private ResponseEntity<Void> move(
            @PathVariable String sessionId,
            @PathVariable String backgammonId,
            @RequestBody BackgammonPlayRequest move,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        // TODO validation: verify authPlayer & assert matches with current turn

        backgammonService.move(backgammonId, move, currentUser.toPlayer());

        // TODO check session. create new or end session!
        backgammonSessionService.checkScore(sessionId);

        return ResponseEntity.ok().build();
    }

}