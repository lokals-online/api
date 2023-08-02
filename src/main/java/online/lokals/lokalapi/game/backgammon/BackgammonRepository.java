package online.lokals.lokalapi.game.backgammon;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BackgammonRepository extends MongoRepository<Backgammon, String> {
}
