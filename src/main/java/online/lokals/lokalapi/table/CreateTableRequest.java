package online.lokals.lokalapi.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    private String name;

    private String gameId;

    public boolean hasName() {
        return StringUtils.hasText(name);
    }

}
