package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/games")
public class GameController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvailableGamesResponse> availableGames(@RequestParam(name = "lokalId", defaultValue = "lokal") String lokalId) {
        return ResponseEntity.ok(new AvailableGamesResponse(lokalId, List.of("backgammon", "pishti")));
    }

//    @PostMapping
//    private ResponseEntity<Game> newGame(@RequestBody BackgammonRequest backgammonRequest) {
//        // TODO: validate
//
//        // TODO: get player
//        Player player = new Player(backgammonRequest.getPlayerId(), "first");
//
//        // create
//        Game game = backgammonService.newGame(player);
//
//        // return
//        return ResponseEntity.ok(game);
//    }
//
//    @GetMapping("/{gameId}")
//    private ResponseEntity<Backgammon> get(@PathVariable String gameId) {
//        Backgammon backgammon = backgammonService.get(gameId).orElseThrow();
//
//        return ResponseEntity.ok(backgammon);
//    }
//
//    @PostMapping("/{gameId}/players")
//    private ResponseEntity<Void> addPlayer(@PathVariable String gameId, @RequestBody NewPlayerRequest request) {
//        Player player = new Player(request.getPlayerId(), "second");
//        backgammonService.addOtherPlayer(gameId, player);
//
//        return ResponseEntity.noContent().build();
//    }

}

record AvailableGamesResponse(@Nonnull String lokalId, List<String> games) {};