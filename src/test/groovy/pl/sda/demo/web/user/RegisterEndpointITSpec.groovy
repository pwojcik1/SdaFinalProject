package pl.sda.demo.web.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.sda.demo.external.user.JpaUserRepository
import pl.sda.demo.external.user.UserEntity
import spock.lang.Specification

import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegisterEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaUserRepository jpaUserRepository
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    def "test should register new user"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"username\": \"username\",\n" +
                        "    \"password\": \"password\",\n" +
                        "    \"role\": \"ADMIN\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201))

        UserEntity user = jpaUserRepository.getOne(4)

        user.getId() == 4
        user.getUsername() == "username"
        user.getRole() == "ADMIN"
        passwordEncoder.matches("password", user.getPassword())
    }
}
