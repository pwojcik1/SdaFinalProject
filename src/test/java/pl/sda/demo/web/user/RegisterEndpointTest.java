package pl.sda.demo.web.user;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.dto.api.LoginRq;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        assertTrue(passwordEncoder.matches("password", userEntity.getPassword()));
        assertTrue(userEntity.getFavourites().isEmpty());
        assertTrue(userEntity.getProducts().isEmpty());


LoginRq loginRq1 = new LoginRq("username", "password");


        HttpEntity<LoginRq> entity2 = new HttpEntity<>(loginRq1);
        ResponseEntity<Void> response2 = testRestTemplate.exchange("/api/login", HttpMethod.GET, entity2, Void.class);
        System.out.println(response2);
    }

    @Test
    @Transactional

    void testShouldLogInUser() throws JSONException {

LoginRq loginRq2 = new LoginRq("user", "user");


        HttpEntity<LoginRq> entity2 = new HttpEntity<>(loginRq2);
        ResponseEntity<String> response2 = testRestTemplate.exchange("/api/login", HttpMethod.GET, entity2, String.class);
        System.out.println(response2);
    }
}