package online.lokals.lokalapi.chirak.table;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.GameRequest;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.table.TableService;
import online.lokals.lokalapi.users.User;

@Slf4j
@RestController
@RequestMapping("/chirak/table")
@RequiredArgsConstructor
public class ChirakTableController {

    private final TableService tableService;

    @GetMapping
    public List<Table> tables(@AuthenticationPrincipal User currentUser) {

        return tableService.getAllTables();
    }

    @PostMapping
    public TableResponse newTable(@RequestBody GameRequest gameRequest, @AuthenticationPrincipal User currentUser) {
        log.trace("new table request by {}", currentUser);

        Table table = tableService.createTable(currentUser, gameRequest);

        return new TableResponse(table);
    }

    @GetMapping("/{tableId}")
    public TableResponse fetch(@PathVariable String tableId, @AuthenticationPrincipal User currentUser) {
        log.trace("new table request by {}", currentUser);

        Table table = tableService.getTableById(tableId);

        return new TableResponse(table);
    }

    @GetMapping("/player")
    public TableResponse fetchByUser(@AuthenticationPrincipal User currentUser) {
        log.trace("table by user: {}", currentUser);
        System.out.println(String.format("table by user: {}", currentUser));

        Table table = tableService
                .findByUser(currentUser)
                .orElse(null);

        return new TableResponse(table);
    }

    @PostMapping("/{tableId}/join")
    public TableResponse joinTable(@PathVariable String tableId, @AuthenticationPrincipal User currentUser) {
        log.trace("user {} requested to join table[{}]", currentUser, tableId);

        if (tableService.findByUser(currentUser).isPresent()) {
            throw new IllegalStateException("cannot join multiple tables");
        }

        Table joined = tableService.join(tableId, currentUser);

        return new TableResponse(joined);
    }

    @PostMapping("/{tableId}/quit")
    public void quitTable(@PathVariable String tableId, @AuthenticationPrincipal User currentUser) {
        log.trace("user {} requested to quit table[{}]", currentUser, tableId);

        tableService.quit(tableId, currentUser);
    }

}

