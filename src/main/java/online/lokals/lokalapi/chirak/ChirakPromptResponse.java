package online.lokals.lokalapi.chirak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChirakPromptResponse {
    private String responseFor; // (prompt.user && prompt.key && prompt.createdAt)
    private String key;
    private Long responseAt;
}
