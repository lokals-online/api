package online.lokals.lokalapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.lokal.Lokal;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.table.TableService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TableService tableService;

    @Override
    public void run(String... args) throws Exception {
//        List<Table> allTables = tableService.getAllTables(Lokal.DEFAULT.getId());
//
//        if (allTables.isEmpty()) {
//
//        }
    }
}
