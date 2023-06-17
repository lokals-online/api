package online.lokals.lokalapi.lokal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lokal")
public class LokalController {

    private final LokalService lokalService;

    public LokalController(LokalService lokalService) {
        this.lokalService = lokalService;
    }

    @GetMapping
    public ResponseEntity<Lokal> defaultLokal() {
        Lokal lokal = lokalService.lokal();

        return ResponseEntity.ok(lokal);
    }

//    @PostMapping
//    public ResponseEntity<Lokal> create(@RequestBody CreateLokalRequest request, @AuthenticationPrincipal User currentUser) {
//        Lokal lokal = lokalService.createLokal(request.name(), currentUser);
//
//        return ResponseEntity.created(URI.create("/lokal/%s".formatted(lokal.getId()))).build();
//    }
//
//    @GetMapping("/{lokalId}")
//    public ResponseEntity<Lokal> get(@PathVariable String lokalId) {
//        Lokal lokal = lokalService.findById(lokalId);
//
//        return ResponseEntity.ok(lokal);
//    }

//    @PutMapping("/{lokalId}")
//    public ResponseEntity<Void> update(@PathVariable String lokalId, @RequestBody UpdateLokalRequest request, @AuthenticationPrincipal User currentUser) {
//        lokalService.updateLokal(lokalId, request.name());
//
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{lokalId}")
//    public ResponseEntity<Void> delete(@PathVariable String lokalId) {
//        lokalService.deleteLokal(lokalId);
//
//        return ResponseEntity.noContent().build();
//    }

}
   
