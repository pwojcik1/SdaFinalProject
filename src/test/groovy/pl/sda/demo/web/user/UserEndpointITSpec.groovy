package pl.sda.demo.web.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
import pl.sda.demo.external.user.JpaUserRepository
import pl.sda.demo.external.user.UserEntity
import spock.lang.Specification

import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaUserRepository jpaUserRepository

    @Autowired
    private JpaProductRepository jpaProductRepository

    @WithMockUser(username = "username")
    def "test should add product to fridge"() {
        given:
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), new HashSet<>())
        jpaUserRepository.save(userEntity)

        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))

        UserEntity user = jpaUserRepository.getOne(1)
        user.getProducts().size() == 1
        user.getProducts().contains(productEntity)
    }

    @WithMockUser(username = "username")
    def "test should remove product from fridge"() {
        given:
        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        Set<ProductEntity> products = new HashSet<>() << productEntity

        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), products)
        jpaUserRepository.save(userEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204))

        UserEntity user = jpaUserRepository.getOne(1)
        user.getProducts().isEmpty()
    }

    @WithMockUser(username = "username")
    def "test should return all products from fridge"() {
        given:
        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        ProductEntity productEntity2 = new ProductEntity(2, "product2")
        jpaProductRepository.save(productEntity2)

        Set<ProductEntity> products = new HashSet<>() << productEntity << productEntity2

        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), products)
        jpaUserRepository.save(userEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].id').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("product"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].id').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].name').value("product2"))
    }
}
