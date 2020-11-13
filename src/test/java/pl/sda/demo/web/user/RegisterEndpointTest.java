package pl.sda.demo.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sda.demo.dto.api.LoginRq;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
class RegisterEndpointTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @Transactional
    void testShouldRegisterUser() {
        //given
        LoginRq loginRq = new LoginRq("username", "password", "ADMIN");

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<LoginRq> entity = new HttpEntity<>(loginRq, headers);
        //when

        ResponseEntity<Void> response = testRestTemplate.exchange("/api/register", HttpMethod.POST, entity, Void.class);
        //then
        assertEquals(201, response.getStatusCodeValue());
        List<UserEntity> all = jpaUserRepository.findAll();
        assertEquals(1, all.size());
        UserEntity userEntity = all.get(0);
        assertEquals(1, userEntity.getId());
        assertEquals("username", userEntity.getUsername());
        assertEquals("ADMIN", userEntity.getRole());
        assertTrue(passwordEncoder.matches("password", userEntity.getPassword()));
        assertTrue(userEntity.getFavourites().isEmpty());
        assertTrue(userEntity.getProducts().isEmpty());
    }
}