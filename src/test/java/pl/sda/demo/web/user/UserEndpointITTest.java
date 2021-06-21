package pl.sda.demo.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserEndpointITTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    @WithMockUser(username = "user2")
    void testShouldAddProductToFridge() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .param("id", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        UserEntity user = jpaUserRepository.getOne(2);
        assertEquals(3, user.getProducts().size());
        assertTrue(user.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(user.getProducts().contains(jpaProductRepository.getOne(2)));
        assertTrue(user.getProducts().contains(jpaProductRepository.getOne(5)));
    }

    @Test
    @WithMockUser(username = "user2")
    void testShouldRemoveProductFromFridge() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        UserEntity user = jpaUserRepository.getOne(2);
        assertEquals(1, user.getProducts().size());
        assertTrue(user.getProducts().contains(jpaProductRepository.getOne(2)));
    }

    @Test
    @WithMockUser(username = "user2")
    void testShouldReturnAllProductsFromFridge() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Product1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Product2"));
    }
}