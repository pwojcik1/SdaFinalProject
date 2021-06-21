package pl.sda.demo.web.recipe

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
import pl.sda.demo.external.recipe.JpaRecipeRepository
import pl.sda.demo.external.recipe.RecipeEntity
import pl.sda.demo.external.user.JpaUserRepository
import pl.sda.demo.external.user.UserEntity
import spock.lang.Specification

import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SearchEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaUserRepository jpaUserRepository

    @Autowired
    private JpaProductRepository jpaProductRepository

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository

    @WithMockUser(username = "user1")
    def "test should return recipes by products"() {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("Recipe4"))
    }
}

