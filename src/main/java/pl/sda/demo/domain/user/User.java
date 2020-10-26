package pl.sda.demo.domain.user;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class User {
    private Integer id;
    private String username;
    private String password;
    private List<Integer> productId;
    private List<Integer> recipeId;
    private String role;
    public void encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }
}
