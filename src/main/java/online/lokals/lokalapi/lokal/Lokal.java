package online.lokals.lokalapi.lokal;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.users.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("lokal")
public class Lokal {

    public static final Lokal DEFAULT = new Lokal("lokal");

    @Id
    private String id;

    @Field()
    private String name;

    private long createdAt;

    private long updatedAt;

    private String ownerId;

    public Lokal(@Nonnull String name) {
        this.name = name;
    }

    public Lokal(@Nonnull String name, @Nonnull User owner) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.ownerId = owner.getId();
    }

    public Lokal(String id, String name, User owner, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.ownerId = owner.getId();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}