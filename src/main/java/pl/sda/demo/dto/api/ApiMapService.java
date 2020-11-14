package pl.sda.demo.dto.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.external.user.DatabaseUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiMapService {

    private final DatabaseUserRepository databaseUserRepository;

    public LoginRq convertUserToLoginRq(String username) {
        Optional<User> user = databaseUserRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalStateException("User doesnt exists");
        }
        return new LoginRq(user.get().getUsername(), user.get().getPassword(), user.get().getRole());
    }

    public User convertToUser(LoginRq loginRq) {
        return new User(null, loginRq.getUsername(), loginRq.getPassword(), null, null, loginRq.getRole());
    }
}
