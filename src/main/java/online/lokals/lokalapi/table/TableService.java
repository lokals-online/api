package online.lokals.lokalapi.table;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.LokalGames;
import online.lokals.lokalapi.game.backgammon.BackgammonSession;
import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.notification.LokalNotificationService;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.GameRequest;
import online.lokals.lokalapi.game.backgammon.BackgammonSessionService;
import online.lokals.lokalapi.game.pishti.PishtiSessionService;
import online.lokals.lokalapi.users.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final BackgammonSessionService backgammonSessionService;
    private final PishtiSessionService pishtiSessionService;

    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    // TODO: @PreAuthorize [currentUser.id == lokal.owner_id]
    public Table createTable(User owner, GameRequest gameRequest) {

        // TODO: validate gameRequest!
        // TODO: validate owner is allowed to create!

        final Table table = tableRepository.save(new Table(owner));

        // fetch opponent(s)

        if (LokalGames.BACKGAMMON.getKey().equals(gameRequest.getGameKey())) {
            BackgammonSession backgammonSession = backgammonSessionService.create(table.getId(), owner.toPlayer(), null, gameRequest.getSettings());
            table.setGameSession(backgammonSession);
        }
        else if (LokalGames.PISHTI.getKey().equals(gameRequest.getGameKey())) {
            PishtiSession pishtiSession = pishtiSessionService.create(table.getId(), owner.toPlayer(), null, gameRequest.getSettings());
            table.setGameSession(pishtiSession);
        }
        else throw new IllegalArgumentException("");

        tableRepository.save(table);

        return table;
    }

    public Table getTableById(@Nonnull String id) throws ResourceNotFoundException {
        return tableRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table with id:{%s}".formatted(id)));
    }

    public Optional<Table> findByUser(@Nonnull User user) {
        return tableRepository.findTableByUsersContains(user);
    }

    public Table join(String tableId, User user) {
        Table tableToJoin = getTableById(tableId);

        tableToJoin.addUser(user);

        return tableRepository.save(tableToJoin);

        //  TODO: lokalNotificationService.notifyTable(tableId, new LokalNotification(user.getUsername() + " joined"));
    }

    public void quit(String tableId, User user) {
        Table tableToQuit = getTableById(tableId);

        tableToQuit.removeUser(user);

        if (tableToQuit.getUsers().isEmpty()) {
            deleteTable(tableToQuit.getId());
        }
        else {
            tableRepository.save(tableToQuit);

            if (LokalGames.BACKGAMMON.getKey().equals(tableToQuit.getGameSession().getKey())) {
                backgammonSessionService.quit(tableToQuit.getGameSession().getId(), user.toPlayer());
            }
        }
    }

    public void deleteTable(@Nonnull String tableId) {
        Table table = getTableById(tableId);
        tableRepository.delete(table);
    }
}