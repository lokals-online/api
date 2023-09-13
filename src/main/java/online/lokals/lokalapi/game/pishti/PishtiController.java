package online.lokals.lokalapi.game.pishti;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.GamePreviewResponse;
import online.lokals.lokalapi.users.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/game/pishti-session")
public class PishtiController {

    private final PishtiSessionService pishtiSessionService;
    private final PishtiService pishtiService;

    @GetMapping
    private ResponseEntity<List<GamePreviewResponse>> availableSessions() {
        List<GamePreviewResponse> backgammonResponses = pishtiSessionService.availableSessions()
                .stream()
                .map(pishtiSession -> new GamePreviewResponse(pishtiSession.getId(), "pishti", pishtiSession.getTitle(), pishtiSession.getPlayers()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(backgammonResponses);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<String> newGame(@RequestBody PishtiRequest pishtiRequest, Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        String pishtiSessionId = pishtiSessionService.create(currentUser.toPlayer(), pishtiRequest);

        // return
        return ResponseEntity.ok(pishtiSessionId);
    }

    @GetMapping("/{pishtiSessionId}")
    private ResponseEntity<PishtiSessionResponse> get(@PathVariable String pishtiSessionId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        PishtiSession pishtiSession = pishtiSessionService.get(pishtiSessionId);

        return ResponseEntity.ok(new PishtiSessionResponse(pishtiSession, currentUser.toPlayer().getUsername()));
    }

    @PostMapping("/{pishtiSessionId}/opponent")
    private ResponseEntity<Void> addPlayer(@PathVariable String pishtiSessionId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        pishtiSessionService.setOpponent(pishtiSessionId, currentUser.toPlayer());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{pishtiSessionId}/pishti/{pishtiId}")
    private ResponseEntity<PishtiResponse> getPishtiMatch(@PathVariable String pishtiId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        PishtiResponse pishtiResponse = pishtiService.get(pishtiId, currentUser.toPlayer());

        return ResponseEntity.ok(pishtiResponse);
    }

    @PostMapping("/{pishtiSessionId}/pishti/{pishtiId}")
    private ResponseEntity<Object> play(@PathVariable String pishtiSessionId,
                                      @PathVariable String pishtiId,
                                      @RequestBody PishtiPlayRequest playRequest,
                                      Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            pishtiService.play(pishtiSessionId, pishtiId, playRequest, currentUser.toPlayer());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        pishtiSessionService.checkScore(pishtiSessionId);

        return ResponseEntity.noContent().build();
    }

}

