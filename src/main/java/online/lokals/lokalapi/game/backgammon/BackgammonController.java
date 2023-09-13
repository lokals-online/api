package online.lokals.lokalapi.game.backgammon;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.BackgammonRequest;
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
@RequestMapping("/game/backgammon-sessions")
public class BackgammonController {

    private final BackgammonSessionService backgammonSessionService;
    private final BackgammonService backgammonService;

    @GetMapping
    private ResponseEntity<List<GamePreviewResponse>> allGames() {
        List<GamePreviewResponse> backgammonResponses = backgammonSessionService.availableSessions().stream()
                .map(session -> new GamePreviewResponse(session.getId(), "backgammon", session.getTitle(), session.getPlayers()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(backgammonResponses);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<String> newGame(@RequestBody BackgammonRequest backgammonRequest, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        String backgammonSessionId = backgammonSessionService.create(currentUser.toPlayer(), backgammonRequest);

        return ResponseEntity.ok(backgammonSessionId);
    }

    @GetMapping("/{backgammonSessionId}")
    private ResponseEntity<BackgammonSessionResponse> get(@PathVariable String backgammonSessionId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        BackgammonSession backgammonSession = backgammonSessionService.get(backgammonSessionId);

        return ResponseEntity.ok(new BackgammonSessionResponse(backgammonSession));
    }

    @PostMapping("/{backgammonSessionId}/opponent")
    private ResponseEntity<Void> addPlayer(@PathVariable String backgammonSessionId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        backgammonSessionService.setOpponent(backgammonSessionId, currentUser.toPlayer());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{backgammonSessionId}/backgammon/{backgammonId}")
    private ResponseEntity<BackgammonResponse> getMatch(
            @PathVariable String backgammonSessionId,
            @PathVariable String backgammonId,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();

        Backgammon backgammon = backgammonService.get(backgammonId);

        return ResponseEntity.ok(new BackgammonResponse(backgammon));
    }

    @PostMapping("/{backgammonSessionId}/backgammon/firstDice")
    private ResponseEntity<Void> firstDice(
            @PathVariable String backgammonSessionId,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();
        // validation: verify authPlayer
        backgammonSessionService.firstDice(backgammonSessionId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{backgammonSessionId}/backgammon/{backgammonId}/rollDice")
    private ResponseEntity<Void> rollDice(
            @PathVariable String backgammonSessionId,
            @PathVariable String backgammonId,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();
        // validation: verify authPlayer


        backgammonService.rollDice(backgammonId, currentUser.toPlayer());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{backgammonSessionId}/backgammon/{backgammonId}/move")
    private ResponseEntity<Void> move(
            @PathVariable String backgammonSessionId,
            @PathVariable String backgammonId,
            @RequestBody BackgammonPlayRequest move,
            Authentication authentication
    ) {

        User currentUser = (User) authentication.getPrincipal();

        // TODO validation: verify authPlayer & assert matches with current turn

        backgammonService.move(backgammonId, move, currentUser.toPlayer());

        // TODO check session. create new or end session!
        backgammonSessionService.checkScore(backgammonSessionId);

        return ResponseEntity.ok().build();
    }

//    @GetMapping("/{gameId}")
//    private ResponseEntity<BackgammonResponse> get(@PathVariable String gameId) {
//        Backgammon backgammon = backgammonService.get(gameId).orElseThrow();
//
//        return ResponseEntity.ok(new BackgammonResponse(backgammon));
////        return ResponseEntity.ok(backgammon);
//    }

//    @PostMapping("/{gameId}/players")
//    private ResponseEntity<Void> addPlayer(@PathVariable String gameId, @RequestBody NewPlayerRequest request) {
//        Player player = new Player(request.getPlayerId(), request.getPlayerId());
//        backgammonService.setOpponent(gameId, player);
//
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{gameId}/firstDice")
//    private ResponseEntity<Void> firstDice(@PathVariable String gameId, @RequestBody FirstDiceRequest request) {
//        // validation: verify authPlayer
//        backgammonService.firstDice(gameId, request.playerId());
//
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/{gameId}/rollDice")
//    private ResponseEntity<Void> rollDice(@PathVariable String gameId) {
//        // validation: verify authPlayer & assert matches with current turn
//        backgammonService.rollDice(gameId);
//
//        return ResponseEntity.ok().build();
//    }
//

}
