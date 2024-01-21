package online.lokals.lokalapi.game.backgammon;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BackgammonSessionRepository extends MongoRepository<BackgammonSession, String> {
    List<BackgammonSession> findBackgammonSessionsByStatusIn(List<BackgammonSessionStatus> backgammonSessionStatus);
}
