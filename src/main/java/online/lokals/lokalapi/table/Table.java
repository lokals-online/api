package online.lokals.lokalapi.table;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.lokal.Lokal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("table")
public class Table {

    @Id
    private String id;

    private String name;

    private String lokalId;

    private String gameId;

    public Table(@Nonnull String name, @Nonnull Lokal lokal, @Nonnull String gameId) {
        this.name = name;
        this.lokalId = lokal.getId();
        this.gameId = gameId;
    }

    public Table(@Nonnull Lokal lokal) {
        this.lokalId = lokal.getId();
    }

    public String getName() {
        return name == null ? "#" + this.id : name;
    }
}