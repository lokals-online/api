package online.lokals.lokalapi.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.GameRequest;
import online.lokals.lokalapi.users.User;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    private User user;

    private GameRequest gameRequest;

}
