package online.lokals.lokalapi.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Document("user")
public class User implements UserDetails {

    public static final SimpleGrantedAuthority OYUNCU_ROLE = new SimpleGrantedAuthority("ROLE_OYUNCU");
    public static final SimpleGrantedAuthority USER_ROLE = new SimpleGrantedAuthority("ROLE_USER");

    public static final String OYUNCU_USERNAME = "oyuncu";
    public static final String ANONYMOUS_PASSWORD = "anonymous_password";

    public static final long validDateStartsAtInMilli = ZonedDateTime.of(2023,9,1,0,0,0,0, ZoneId.systemDefault()).toInstant().toEpochMilli();

    @Id
    private String id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String profilePicture;
    private Long createdAt;
    @NotNull
    private SimpleGrantedAuthority role;

    @JsonIgnore
    private String lokalId;

    public User(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
        this.role = USER_ROLE;
    }

    public User(String id, String username, String encodedPassword) {
        this.id = id;
        this.username = username;
        this.password = encodedPassword;
        this.role = USER_ROLE;
    }

    public static User oyuncu() {
        final User oyuncu = new User(OYUNCU_USERNAME, ANONYMOUS_PASSWORD);

        long now = System.currentTimeMillis();
        oyuncu.setId("oyuncu%d".formatted(now));
        oyuncu.setCreatedAt(System.currentTimeMillis());
        oyuncu.role = OYUNCU_ROLE;

        return oyuncu;
    }

    public static User chirak() {
        return new User("chirak", "çırak", ANONYMOUS_PASSWORD);
    }

    public static boolean isOyuncu(String lokalUserId) {
        if (!lokalUserId.startsWith(OYUNCU_USERNAME)) return false;
        String createdAtStr = lokalUserId.substring(OYUNCU_USERNAME.length());
        try {
            long createdAt = Long.parseLong(createdAtStr);
            Instant createdAtInstant = Instant.ofEpochMilli(createdAt);
            return (createdAtInstant.isAfter(Instant.ofEpochMilli(validDateStartsAtInMilli))
                    &&
                    createdAtInstant.isBefore(Instant.now())
            );
        }
        catch (Exception e) {
            log.warn("unexpected date from lokalUserId: {}", lokalUserId);
            return false;
        }
    }

    public static User getOyuncu(String lokalUserId) {
        final User anonymousUser = new User(OYUNCU_USERNAME, "anonymous_password");

        anonymousUser.setId(lokalUserId);
        anonymousUser.setCreatedAt(Long.parseLong(lokalUserId.substring(OYUNCU_USERNAME.length())));
        anonymousUser.role = OYUNCU_ROLE;

        return anonymousUser;
    }

    @Override
    public String toString() {
        return "User{id='" + id + ", username='" + username + '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Player toPlayer() {
        return new Player(id, username, this.role.getAuthority().equals(OYUNCU_ROLE.getAuthority()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + username.hashCode();
        return result;
    }
}