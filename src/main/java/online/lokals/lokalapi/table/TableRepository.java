package online.lokals.lokalapi.table;

import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface TableRepository extends MongoRepository<Table, String> {
    List<Table> findByLokalId(@Nonnull String lokalId);
}