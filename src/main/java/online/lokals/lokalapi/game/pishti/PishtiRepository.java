package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PishtiRepository extends MongoRepository<Pishti, String> {

}
