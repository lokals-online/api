package online.lokals.lokalapi.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("user")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String profilePicture;

    @JsonIgnore
    private String lokalId;

    public User(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
    }

    @Override
    public String toString() {
        return "User{id='" + id + ", username='" + username + '}';
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }

//    public Player toPlayer() {
//        if (this.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
//            return new AnonymousPlayer();
//        }
//        else {
//            return new LoggedPlayer(id, username, password);
//        }
//    }
}