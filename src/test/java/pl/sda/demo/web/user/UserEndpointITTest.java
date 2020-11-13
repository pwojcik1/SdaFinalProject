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
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Set;

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
    @WithMockUser(username = "username")
    void testShouldAddProductToFridge() throws Exception {
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), new HashSet<>());
        jpaUserRepository.save(userEntity);
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        UserEntity user = jpaUserRepository.getOne(1);
        assertEquals(1, user.getProducts().size());
        assertTrue(user.getProducts().contains(productEntity));
    }

    @Test
    @WithMockUser(username = "username")
    void testShouldRemoveProductFromFridge() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> products = new HashSet<>();
        products.add(productEntity);
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), products);
        jpaUserRepository.save(userEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        UserEntity user = jpaUserRepository.getOne(1);
        assertTrue(user.getProducts().isEmpty());
    }

    @Test
    @WithMockUser(username = "username")
    void testShouldReturnAllProductsFromFridge(){

        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(2, "produc2");
        jpaProductRepository.save(productEntity2);
        Set<ProductEntity> products = new HashSet<>();
        products.add(productEntity);
        products.add(productEntity2);
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), products);
        jpaUserRepository.save(userEntity);


    }
}