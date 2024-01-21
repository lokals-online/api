package online.lokals.lokalapi.lokal;

import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface LokalRepository extends MongoRepository<Lokal, String> {

    Optional<Lokal> findLokalByName(@Nonnull String name);
}