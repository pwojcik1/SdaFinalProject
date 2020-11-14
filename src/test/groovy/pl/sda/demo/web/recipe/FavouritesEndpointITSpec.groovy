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
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FavouritesEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaProductRepository jpaProductRepository

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository

    @Autowired
    private JpaUserRepository jpaUserRepository

    @WithMockUser(username = "username")
    def "test should add recipe to favourites"() {
        given:
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), new HashSet<>())
        jpaUserRepository.save(userEntity)

        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        Set<ProductEntity> productEntities = new HashSet<>() << productEntity

        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities)
        jpaRecipeRepository.save(recipeEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favourites")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))

        List<RecipeEntity> result = jpaRecipeRepository.findAllFavourites("username")
        result.size() == 1
        result.contains(recipeEntity)
    }

    @WithMockUser(username = "username")
    def "test should remove recipe from favourites"() {
        given:
        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        Set<ProductEntity> productEntities = new HashSet<>() << productEntity

        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities)
        jpaRecipeRepository.save(recipeEntity)

        Set<RecipeEntity> favourites = new HashSet<>() << recipeEntity

        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", favourites, new HashSet<>())
        jpaUserRepository.save(userEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favourites")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204))

        List<RecipeEntity> result = jpaRecipeRepository.findAllFavourites("username")
        result.isEmpty()
    }

    @WithMockUser(username = "username")
    def "test should return all favourites"() {
        given:
        ProductEntity productEntity = new ProductEntity(1, "product")
        jpaProductRepository.save(productEntity)

        Set<ProductEntity> productEntities = new HashSet<>() << productEntity

        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities)
        jpaRecipeRepository.save(recipeEntity)

        Set<RecipeEntity> favourites = new HashSet<>() << recipeEntity

        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", favourites, new HashSet<>())
        jpaUserRepository.save(userEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favourites")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].id').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].description').value("desc"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId.size()').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId[0]').value(1))
    }

}
