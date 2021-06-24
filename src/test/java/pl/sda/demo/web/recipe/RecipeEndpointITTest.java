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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class RecipeEndpointITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldCreateRecipe() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(5);
        assertTrue(recipeById.isPresent());
        assertEquals(5, recipeById.get().getId());
        assertEquals("NewApiRecipe", recipeById.get().getName());
        assertEquals("TestDescription", recipeById.get().getDescription());
        assertEquals(1, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(1)));
    }

    @Test
    @WithMockUser()
    void testShouldNotCreateRecipeIfUserIsNotAdmin() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(5);
        assertTrue(recipeById.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldDeleteRecipe() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1);
        assertTrue(recipeById.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldNotDeleteRecipeIfUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1);
        assertTrue(recipeById.isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldUpdateProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1, 2]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1);
        assertTrue(recipeById.isPresent());
        assertEquals(1, recipeById.get().getId());
        assertEquals("NewApiRecipe", recipeById.get().getName());
        assertEquals("TestDescription", recipeById.get().getDescription());
        assertEquals(2, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(2)));
    }

    @Test
    @WithMockUser()
    void testShouldNotUpdateProductIfUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1, 2]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1);
        assertTrue(recipeById.isPresent());
        assertEquals(1, recipeById.get().getId());
        assertEquals("Recipe1", recipeById.get().getName());
        assertEquals("description for recipe1", recipeById.get().getDescription());
        assertEquals(3, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(4)));
        assertTrue(recipeById.get().getProducts().contains(jpaProductRepository.getOne(6)));
    }

    @Test
    @WithMockUser()
    void testShouldReturnAllRecipes() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Recipe1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("description for recipe1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId.size()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[1]").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[2]").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("description for recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId.size()").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[1]").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[2]").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[3]").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].name").value("Recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].description").value("description for recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].productId.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].productId[0]").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].productId[1]").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].name").value("Recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].description").value("description for recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].productId.size()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].productId[0]").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].productId[1]").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].productId[2]").value(7));
    }

    @Test
    @WithMockUser()
    void testShouldReturnRecipeById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/{id}", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("description for recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId[0]").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId[1]").value(4));
    }
}