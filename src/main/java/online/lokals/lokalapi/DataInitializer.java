package online.lokals.lokalapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.lokal.Lokal;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.table.TableService;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        userService.create(User.chirak());
//        List<Table> allTables = tableService.getAllTables(Lokal.DEFAULT.getId());
//
//        if (allTables.isEmpty()) {
//
//        }
    }
}
