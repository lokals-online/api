package online.lokals.lokalapi.table;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.users.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface TableRepository extends MongoRepository<Table, String> {

    Optional<Table> findTableByUsersContains(@Nonnull User user);

}