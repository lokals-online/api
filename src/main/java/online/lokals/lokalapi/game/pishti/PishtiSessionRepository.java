package online.lokals.lokalapi.game.pishti;

import lombok.Getter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PishtiSessionRepository extends MongoRepository<PishtiSession, String> {

    List<PishtiSession> findPishtiSessionByStatus(PishtiSessionStatus status);

}
