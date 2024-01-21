// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import lombok.RequiredArgsConstructor;
// import online.lokals.lokalapi.game.backgammon.BackgammonSession;
// import online.lokals.lokalapi.game.backgammon.BackgammonSessionQueryService;
// import online.lokals.lokalapi.game.backgammon.BackgammonSessionResponse;
// import online.lokals.lokalapi.users.User;

// // @RequiredArgsConstructor
// // @RestController
// // @RequestMapping("/tavla/query")
// // public class BackgammonSessionQueryController {

// //     private final BackgammonSessionQueryService queryService;

// //     @GetMapping("/between")
// //     private ResponseEntity<BetweenQueryResponse> betweenStatistics(
// //         @RequestParam("opponentId") String opponentId, 
// //         @AuthenticationPrincipal User currentUser
// //     ) {
// //         queryService.betweenStatistics(currentUser.getId(), opponentId);

// //         return ResponseEntity.ok().build();
// //     }
// // }