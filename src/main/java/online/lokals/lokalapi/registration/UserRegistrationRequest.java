package online.lokals.lokalapi.registration;

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
class UserRegistrationRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zçğıöşü]+$", message = "{userRegistrationRequest.username.format}")
    @Length(min = 3, max = 100)
    private String username;

    @NotBlank
    @Length(min = 9, max = 100)
    private String password;

    private boolean autoCreateLokal;

}
