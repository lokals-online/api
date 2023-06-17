package online.lokals.lokalapi.game;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.backgammon.Backgammon;
import online.lokals.lokalapi.game.backgammon.BackgammonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/game")
//public class GameController {
//
//    private final BackgammonService backgammonService;
//
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
//
//}
//
