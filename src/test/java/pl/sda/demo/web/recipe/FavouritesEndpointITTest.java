package pl.sda.demo.web.recipe;

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
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FavouritesEndpointITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @WithMockUser(username = "username")
    void testShouldAddRecipeToFavourites() throws Exception {
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", new HashSet<>(), new HashSet<>());
        jpaUserRepository.save(userEntity);
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/favourites")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        List<RecipeEntity> result = jpaRecipeRepository.findAllFavourites("username");
        assertEquals(1, result.size());
        assertTrue(result.contains(recipeEntity));
    }

    @Test
    @WithMockUser(username = "username")
    void testShouldRemoveRecipeFromFavourites() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);
        Set<RecipeEntity> favourites = new HashSet<>();
        favourites.add(recipeEntity);
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", favourites, new HashSet<>());
        jpaUserRepository.save(userEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favourites")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        List<RecipeEntity> result = jpaRecipeRepository.findAllFavourites("username");
        assertTrue(result.isEmpty());
    }

    @Test
    @WithMockUser(username = "username")
    void testShouldReturnAllFavourites() throws Exception{
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(1, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);
        Set<RecipeEntity> favourites = new HashSet<>();
        favourites.add(recipeEntity);
        UserEntity userEntity = new UserEntity(null, "username", "pass", "USER", favourites, new HashSet<>());
        jpaUserRepository.save(userEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/favourites")
        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("desc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[0]").value(1));
    }

}