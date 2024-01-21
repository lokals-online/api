package online.lokals.lokalapi.game.pishti;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PishtiSessionRepository extends MongoRepository<PishtiSession, String> {

    List<PishtiSession> findPishtiSessionByStatus(PishtiSessionStatus status);

}
