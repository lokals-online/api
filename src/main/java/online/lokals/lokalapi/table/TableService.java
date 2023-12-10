package online.lokals.lokalapi.table;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.GameRequest;
import online.lokals.lokalapi.game.LokalGames;
import online.lokals.lokalapi.game.backgammon.BackgammonSession;
import online.lokals.lokalapi.game.backgammon.BackgammonSessionService;
import online.lokals.lokalapi.game.batak.BatakSession;
import online.lokals.lokalapi.game.batak.BatakSessionService;
import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.PishtiSessionService;
import online.lokals.lokalapi.users.User;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final BackgammonSessionService backgammonSessionService;
    private final PishtiSessionService pishtiSessionService;
    private final BatakSessionService batakSessionService;

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
        else if (LokalGames.BATAK.getKey().equals(gameRequest.getGameKey())) {
            BatakSession batakSession = batakSessionService.create(table.getId(), owner.toPlayer(), gameRequest.getSettings());
            table.setGameSession(batakSession);
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

        if (tableToQuit.getUsers().isEmpty() || tableToQuit.getOwner().equals(user)) {
            deleteTable(tableToQuit.getId());
        }
        else {
            tableToQuit.removeUser(user);

            tableRepository.save(tableToQuit);

            if (LokalGames.BACKGAMMON.getKey().equals(tableToQuit.getGameSession().getKey())) {
                backgammonSessionService.quit(tableToQuit.getGameSession().getId(), user.toPlayer());
            }
            else if (LokalGames.PISHTI.getKey().equals(tableToQuit.getGameSession().getKey())) {
                // pishtiSessionService.quit(tableToQuit.getGameSession().getId(), user.toPlayer());
            }
            else if (LokalGames.BATAK.getKey().equals(tableToQuit.getGameSession().getKey())) {
                batakSessionService.quit(tableToQuit.getGameSession().getId(), user.toPlayer());
            }
        }
    }

    public void deleteTable(@Nonnull String tableId) {
        Table table = getTableById(tableId);
        tableRepository.delete(table);
    }
}