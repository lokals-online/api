package online.lokals.lokalapi.game.pishti.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.pishti.PishtiResponse;
import online.lokals.lokalapi.game.pishti.PishtiService;
import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.PishtiSessionService;
import online.lokals.lokalapi.users.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pishti")
public class PishtiSessionController {

    private final PishtiSessionService pishtiSessionService;
    private final PishtiService pishtiService;

    @GetMapping("/{pishtiSessionId}")
    private ResponseEntity<PishtiSessionResponse> get(@PathVariable String pishtiSessionId, @AuthenticationPrincipal User currentUser) {
        PishtiSession pishtiSession = pishtiSessionService.get(pishtiSessionId);

        return ResponseEntity.ok(new PishtiSessionResponse(pishtiSession, currentUser.toPlayer()));
    }

    @PostMapping("/{sessionId}/sit")
    private ResponseEntity<Void> sit(@PathVariable String sessionId, @AuthenticationPrincipal User currentUser) {
        pishtiSessionService.sit(sessionId, currentUser.toPlayer());
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pishtiSessionId}/game/{pishtiId}")
    private ResponseEntity<PishtiResponse> getPishtiMatch(@PathVariable String pishtiId, @AuthenticationPrincipal User currentUser) {
        PishtiResponse pishtiResponse = pishtiService.get(pishtiId, currentUser.toPlayer());

        return ResponseEntity.ok(pishtiResponse);
    }

    @PostMapping("/{pishtiSessionId}/game/{pishtiId}")
    private ResponseEntity<Object> play(@PathVariable String pishtiSessionId,
                                      @PathVariable String pishtiId,
                                      @RequestBody PishtiPlayRequest playRequest,
                                      @AuthenticationPrincipal User currentUser) {

        try {
            pishtiService.play(pishtiSessionId, pishtiId, playRequest, currentUser.toPlayer());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        pishtiSessionService.checkScore(pishtiSessionId);

        return ResponseEntity.noContent().build();
    }

}
