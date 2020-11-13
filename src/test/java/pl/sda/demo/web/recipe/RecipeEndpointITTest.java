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
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);

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

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isPresent());
        assertEquals(1, recipeById.get().getId());
        assertEquals("NewApiRecipe", recipeById.get().getName());
        assertEquals("TestDescription", recipeById.get().getDescription());
        assertEquals(1, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(productEntity));
    }

    @Test
    @WithMockUser()
    void testShouldNotCreateRecipeIfUserIsNotAdmin() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);

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

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldDeleteRecipe() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldNotDeleteRecipeIfUserIsNotAdmin() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldUpdateProduct() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        jpaProductRepository.save(productEntity2);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);

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
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isPresent());
        assertEquals(1, recipeById.get().getId());
        assertEquals("NewApiRecipe", recipeById.get().getName());
        assertEquals("TestDescription", recipeById.get().getDescription());
        assertEquals(2, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(productEntity));
        assertTrue(recipeById.get().getProducts().contains(productEntity2));
    }

    @Test
    @WithMockUser()
    void testShouldNotUpdateProductIfUserIsNotAdmin() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        jpaProductRepository.save(productEntity2);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);

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
        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeById(1);
        assertTrue(recipeById.isPresent());
        assertEquals(1, recipeById.get().getId());
        assertEquals("name", recipeById.get().getName());
        assertEquals("desc", recipeById.get().getDescription());
        assertEquals(1, recipeById.get().getProducts().size());
        assertTrue(recipeById.get().getProducts().contains(productEntity));
    }

    @Test
    @WithMockUser()
    void testShouldReturnAllRecipes() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        jpaProductRepository.save(productEntity2);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);
        Set<ProductEntity> productEntities2 = new HashSet<>();
        productEntities2.add(productEntity2);
        RecipeEntity recipeEntity2 = new RecipeEntity(null, "name2", "desc2", productEntities2);
        jpaRecipeRepository.save(recipeEntity2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("desc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("name2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("desc2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId[0]").value(2));
    }

    @Test
    @WithMockUser()
    void testShouldReturnRecipeById() throws Exception {
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity);
        RecipeEntity recipeEntity = new RecipeEntity(null, "name", "desc", productEntities);
        jpaRecipeRepository.save(recipeEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("desc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId[0]").value(1));
    }

}