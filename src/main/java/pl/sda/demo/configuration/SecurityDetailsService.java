package pl.sda.demo.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pl.sda.demo.domain.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(usr -> new User(usr.getUsername(),
                        usr.getPassword(), mapRole(usr.getRole())))
                .orElseThrow(() -> new UsernameNotFoundException("User not exists"));
    }
    private List<GrantedAuthority> mapRole(String role) {
        //dodajemy prefix ROLE_ - jesli sprawdzamy czy uzytkownik ma role to bez prefixu
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return Collections.singletonList(authority);
    }
}
