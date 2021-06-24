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
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FavouritesEndpointITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Test
    @WithMockUser(username = "user1")
    void testShouldAddRecipeToFavourites() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favourites")
                .param("id", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        List<RecipeEntity> result = jpaRecipeRepository.findAllUserFavourites("user1");
        assertEquals(3, result.size());
        assertTrue(result.contains(jpaRecipeRepository.getOne(1)));
        assertTrue(result.contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.contains(jpaRecipeRepository.getOne(3)));
    }

    @Test
    @WithMockUser(username = "user1")
    void testShouldRemoveRecipeFromFavourites() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favourites")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        List<RecipeEntity> result = jpaRecipeRepository.findAllUserFavourites("user1");
        assertEquals(1, result.size());
        assertTrue(result.contains(jpaRecipeRepository.getOne(2)));
    }

    @Test
    @WithMockUser(username = "user2")
    void testShouldReturnAllFavourites() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favourites")
        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("description for recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId.size()").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[1]").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[2]").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[3]").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("description for recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId.size()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[0]").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[1]").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[2]").value(7));
    }
}