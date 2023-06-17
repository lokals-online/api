package online.lokals.lokalapi.table;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lokal/{lokalId}/table")
public class TableRestController {

    private final TableService tableService;
    private final TableValidator tableValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        final DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
        resolver.setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE);

        binder.setMessageCodesResolver(resolver);
        binder.addValidators(tableValidator);
    }

    @GetMapping
    public ResponseEntity<List<Table>> getAllTables(@PathVariable String lokalId) {
        List<Table> tables = tableService.getAllTables(lokalId);
        return ResponseEntity.ok(tables);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTable(@PathVariable String lokalId, @Valid @RequestBody CreateTableRequest tableRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bindingResult.getAllErrors());
        }

        try {
            CreateTableResponse createTableResponse = tableService.createTable(lokalId, tableRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createTableResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Table> getTableById(@PathVariable String id) {
        Table table = tableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable String id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}