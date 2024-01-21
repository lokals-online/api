package online.lokals.lokalapi.game.batak;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatakSessionRepository extends MongoRepository<BatakSession, String> {

}