package online.lokals.lokalapi.lokal;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LokalService {

    public static final String DEFAULT_LOKAL = "lokal";

    private final LokalRepository lokalRepository;

    public Lokal findById(@Nonnull String id) {
        return lokalRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lokal with id:{%s}".formatted(id)));
    }

    public Lokal createLokal(@Nonnull String name, User owner) {
        long now = System.currentTimeMillis();
        Lokal lokal = new Lokal();
        lokal.setName(name);
        lokal.setOwnerId(owner.getId());
        lokal.setCreatedAt(now);
        lokal.setUpdatedAt(now);
        return lokalRepository.save(lokal);
    }

    public void updateLokal(@Nonnull String lokalId, @Nonnull String name) {
        Lokal lokal = findById(lokalId);

        long now = System.currentTimeMillis();
        lokal.setName(name);
        lokal.setUpdatedAt(now);
        lokalRepository.save(lokal);
    }

    public void deleteLokal(@Nonnull String lokalId) {
        lokalRepository.deleteById(lokalId);
    }

    public @Nonnull Lokal lokal() {
        return lokalRepository
                .findLokalByName(DEFAULT_LOKAL)
                .orElse(Lokal.DEFAULT);
    }
}