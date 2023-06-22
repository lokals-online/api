package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.BackgammonRequest;
import online.lokals.lokalapi.game.NewPlayerRequest;
import online.lokals.lokalapi.game.Player;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/game/backgammon")
public class BackgammonController {

    private final BackgammonService backgammonService;

    @GetMapping
    private ResponseEntity<List<BackgammonResponse>> allGames() {
        List<BackgammonResponse> backgammonResponses = backgammonService.allGames().stream().map(BackgammonResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(backgammonResponses);
    }

    @PostMapping("/restart")
    private ResponseEntity<BackgammonResponse> restart() {
        return ResponseEntity.ok(new BackgammonResponse(backgammonService.restart()));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<BackgammonResponse> newGame(@RequestBody BackgammonRequest backgammonRequest) {
        // TODO: validate

        // TODO: get player
        Player player = new Player(backgammonRequest.getPlayerId(), backgammonRequest.getPlayerId());

        // create
        Backgammon backgammon = backgammonService.newGame(player);

        // return
        return ResponseEntity.ok(new BackgammonResponse(backgammon));
    }

    @GetMapping("/{gameId}")
    private ResponseEntity<BackgammonResponse> get(@PathVariable String gameId) {
        Backgammon backgammon = backgammonService.get(gameId).orElseThrow();

        return ResponseEntity.ok(new BackgammonResponse(backgammon));
    }

    @PostMapping("/{gameId}/players")
    private ResponseEntity<Void> addPlayer(@PathVariable String gameId, @RequestBody NewPlayerRequest request) {
        Player player = new Player(request.getPlayerId(), "second");
        backgammonService.addOtherPlayer(gameId, player);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{gameId}/firstDice")
    private ResponseEntity<Void> firstDice(@PathVariable String gameId, @RequestBody FirstDiceRequest request) {
        // validation: verify authPlayer
        backgammonService.firstDice(gameId, request.playerId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/rollDice")
    private ResponseEntity<Void> rollDice(@PathVariable String gameId) {
        // validation: verify authPlayer & assert matches with current turn
        backgammonService.rollDice(gameId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/move")
    private ResponseEntity<Void> move(@PathVariable String gameId, @RequestBody BackgammonPlayRequest move) {
        // validation: verify authPlayer & assert matches with current turn
        backgammonService.move(gameId, move);

        return ResponseEntity.ok().build();
    }

}

record FirstDiceRequest(@NotNull String playerId){};
