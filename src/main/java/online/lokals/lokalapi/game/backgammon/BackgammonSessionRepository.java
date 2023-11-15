package online.lokals.lokalapi.game.backgammon;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Nonnull;

import java.util.List;


@Repository
public interface BackgammonSessionRepository extends MongoRepository<BackgammonSession, String> {
    List<BackgammonSession> findBackgammonSessionsByStatusIn(List<BackgammonSessionStatus> backgammonSessionStatus);
}
