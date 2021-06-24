package pl.sda.demo.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sda.demo.dto.ApiMapService;
import pl.sda.demo.dto.LoginRq;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SecurityDetailsService implements UserDetailsService {

    private final ApiMapService apiMapService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginRq loginRq = apiMapService.convertUserToLoginRq(username);
        return new User(loginRq.getUsername(), loginRq.getPassword(), Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + loginRq.getRole())));
    }
}
