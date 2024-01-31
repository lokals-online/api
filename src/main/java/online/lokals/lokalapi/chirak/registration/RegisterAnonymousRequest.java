package online.lokals.lokalapi.chirak.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class RegisterAnonymousRequest {

    @NotBlank
    private String id;

    @NotBlank
    @Pattern(regexp = "^[a-zçğıöşü]+$", message = "{registerAnonymousRequest.username.format}")
    @Length(min = 3, max = 100)
    private String username;

    private String gameSessionKey;
    private String gameSessionId;

}
