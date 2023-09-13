package online.lokals.lokalapi.game.backgammon;

import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.PishtiSessionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackgammonSessionRepository extends MongoRepository<BackgammonSession, String> {
    List<BackgammonSession> findBackgammonSessionsByStatusIn(List<BackgammonSessionStatus> backgammonSessionStatus);
}
