package online.lokals.lokalapi.table;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.lokal.Lokal;
import online.lokals.lokalapi.lokal.LokalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final LokalService lokalService;

    public List<Table> getAllTables(String lokalId) {
        return tableRepository.findByLokalId(lokalId);
    }

    // TODO: @PreAuthorize [currentUser.id == lokal.owner_id]
    public CreateTableResponse createTable(@Nonnull String lokalId, CreateTableRequest tableRequest) {
        Lokal lokal = lokalService.findById(lokalId);

        Table table = new Table();
        table.setLokalId(lokal.getId());
        table.setName(tableRequest.getName());
        table.setGameId(tableRequest.getGameId());

        tableRepository.save(table);

        return null;
        // channel
//        gameChannelService.create();
    }

    public Table getTableById(String id) throws ResourceNotFoundException {
        return tableRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table with id:{%s}".formatted(id)));
    }

    public void deleteTable(String tableId) {
        Table table = getTableById(tableId);
        tableRepository.delete(table);
    }
}