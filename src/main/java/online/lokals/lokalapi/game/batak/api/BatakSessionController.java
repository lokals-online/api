package online.lokals.lokalapi.game.batak.api;

import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.api.PishtiSessionResponse;
import online.lokals.lokalapi.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.batak.BatakService;
import online.lokals.lokalapi.game.batak.BatakSession;
import online.lokals.lokalapi.game.batak.BatakSessionService;
import online.lokals.lokalapi.game.card.Card;
import online.lokals.lokalapi.game.card.CardType;
import online.lokals.lokalapi.users.User;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/batak")
public class BatakSessionController {
    
    private final BatakSessionService batakSessionService;
    private final BatakService batakService;
    private final UserService userService;


    @PostMapping
    private ResponseEntity<BatakSessionResponse> create(
            @RequestBody Map<String, Object> settings,
            @AuthenticationPrincipal User currentUser
    ) {
        User home = userService.getOrCreatePlayer(currentUser);

        BatakSession batakSession = batakSessionService.create(home, settings);

        return ResponseEntity.ok(new BatakSessionResponse(batakSession));
    }

    @GetMapping("/{batakSessionId}")
    private ResponseEntity<BatakSessionResponse> get(@PathVariable String batakSessionId, @AuthenticationPrincipal User currentUser) {

        BatakSession batakSession = batakSessionService.get(batakSessionId);
        
        return ResponseEntity.ok(new BatakSessionResponse(batakSession));
    }

    @PostMapping("/{batakSessionId}/sit")
    private ResponseEntity<Void> sit(@PathVariable String batakSessionId, @AuthenticationPrincipal User currentUser) {
        User user = userService.getOrCreatePlayer(currentUser);

        batakSessionService.sit(batakSessionId, user);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{batakSessionId}/game/{batakId}")
    private ResponseEntity<BatakResponse> getBatakMatch(@PathVariable String batakId, @AuthenticationPrincipal User currentUser) {
        BatakResponse batakResponse = batakService.get(batakId, currentUser.toPlayer());

        return ResponseEntity.ok(batakResponse);
    }

    @PostMapping("/{batakSessionId}/game/{batakId}/bid")
    private ResponseEntity<Void> bid(@PathVariable String batakSessionId, @PathVariable String batakId, @AuthenticationPrincipal User currentUser, @RequestBody Integer betValue) {

        batakService.bid(batakId, currentUser.toPlayer(), betValue);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{batakSessionId}/game/{batakId}/chooseTrump")
    private ResponseEntity<Void> chooseTrump(@PathVariable String batakSessionId, @PathVariable String batakId, @AuthenticationPrincipal User currentUser, @RequestBody CardType cardType) {

        batakService.chooseTrump(batakId, currentUser.toPlayer(), cardType);

        batakSessionService.start(batakSessionId);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{batakSessionId}/game/{batakId}/play")
    private ResponseEntity<Void> play(@PathVariable String batakSessionId, @PathVariable String batakId, @AuthenticationPrincipal User currentUser, @RequestBody Card card) {

        batakService.play(batakId, currentUser.toPlayer(), card);

        batakSessionService.check(batakSessionId);
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{batakSessionId}/restart")
    private ResponseEntity<Void> restart(@PathVariable String batakSessionId, @AuthenticationPrincipal User currentUser) {
        batakSessionService.restart(batakSessionId);

        return ResponseEntity.ok().build();
    }

}
