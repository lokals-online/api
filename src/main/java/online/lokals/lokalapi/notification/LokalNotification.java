package online.lokals.lokalapi.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LokalNotification {

    // table_join, table_quit, session_sit, session_first_dice, session_start, etc...
    private LokalNotificationType key;

    private Object payload;

//    private String message;

}
