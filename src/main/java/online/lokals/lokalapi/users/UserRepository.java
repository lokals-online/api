package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(@Nonnull String username);

    boolean existsByUsername(@Nonnull String username);
}