package online.lokals.lokalapi.game.backgammon.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.backgammon.*;
import online.lokals.lokalapi.users.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tavla")
public class BackgammonSessionController {

    private final BackgammonSessionService backgammonSessionService;
    private final BackgammonService backgammonService;

    @PostMapping
    private ResponseEntity<BackgammonSessionResponse> create(
            @RequestBody @Valid NewBackgammonRequest newBackgammonRequest,
            @AuthenticationPrincipal User currentUser
    ) {

        BackgammonSession backgammonSession = backgammonSessionService.create(
                currentUser.toPlayer(),
                newBackgammonRequest.getOpponent(),
                newBackgammonRequest.getSettings()
        );

        return ResponseEntity.ok(new BackgammonSessionResponse(backgammonSession));
    }

    @GetMapping("/{sessionId}")
    private ResponseEntity<BackgammonSessionResponse> fetch(@PathVariable String sessionId) {
        BackgammonSession backgammonSession = backgammonSessionService.get(sessionId);

        return ResponseEntity.ok(new BackgammonSessionResponse(backgammonSession));
    }

    @PostMapping("/{sessionId}/basKonus")
    private ResponseEntity<Void> basKonus(
            @PathVariable String sessionId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        try {
            backgammonSessionService.basKonus(sessionId, currentUser.toPlayer(), file);
        } catch (IOException e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
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

    @PutMapping("/{sessionId}/restart")
    private ResponseEntity<Void> restart(@PathVariable String sessionId, @AuthenticationPrincipal User currentUser) {
        backgammonSessionService.restart(sessionId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
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
