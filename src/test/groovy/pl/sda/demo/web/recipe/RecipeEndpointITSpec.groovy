package pl.sda.demo.web.recipe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
import pl.sda.demo.external.recipe.JpaRecipeRepository
import pl.sda.demo.external.recipe.RecipeEntity
import spock.lang.Specification

import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class RecipeEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaProductRepository jpaProductRepository

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository

    @WithMockUser(roles = "ADMIN")
    def "test should create new recipe"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(5)
        recipeById.isPresent()
        recipeById.get().getId() == 5
        recipeById.get().getName() == "NewApiRecipe"
        recipeById.get().getDescription() == "TestDescription"
        recipeById.get().getProducts().size() == 1
        recipeById.get().getProducts().contains(jpaProductRepository.getOne(1))
    }

    @WithMockUser()
    def "test should not create new recipe if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findRecipeByName("NewRecipe")
        recipeById.isEmpty()
    }

    @WithMockUser(roles = "ADMIN")
    def "test should delete recipe"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204))

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1)
        recipeById.isEmpty()
    }

    @WithMockUser
    def "test should not delete recipe if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipe")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1)
        recipeById.isPresent()
    }

    @WithMockUser(roles = "ADMIN")
    def "test should update recipe"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1, 2]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(1)
        recipeById.isPresent()
        recipeById.get().getId() == 1
        recipeById.get().getName() == "NewRecipe"
        recipeById.get().getDescription() == "TestDescription"
        recipeById.get().getProducts().size() == 2
        recipeById.get().getProducts().contains(jpaProductRepository.getOne(1))
        recipeById.get().getProducts().contains(jpaProductRepository.getOne(2))
    }

    @WithMockUser()
    def "test should not update recipe if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 3,\n" +
                        "    \"name\": \"NewApiRecipe\",\n" +
                        "    \"description\": \"TestDescription\",\n" +
                        "    \"productId\": [1, 2]\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<RecipeEntity> recipeById = jpaRecipeRepository.findById(3)
        recipeById.isPresent()
        recipeById.get().getId() == 3
        recipeById.get().getName() == "Recipe3"
        recipeById.get().getDescription() == "description for recipe3"
        recipeById.get().getProducts().size() == 2
        recipeById.get().getProducts().contains(jpaProductRepository.getOne(2))
        recipeById.get().getProducts().contains(jpaProductRepository.getOne(4))
    }

    @WithMockUser()
    def "test should return all recipes"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].id').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("Recipe1"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].description').value("description for recipe1"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId.size()').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId[0]').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId[1]').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].productId[2]').value(6))

                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].id').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].name').value("Recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].description').value("description for recipe2"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].productId.size()').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].productId[0]').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].productId[1]').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].productId[2]').value(8))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].productId[3]').value(9))

                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].id').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].name').value("Recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].description').value("description for recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].productId.size()').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].productId[0]').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].productId[1]').value(4))

                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].id').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].name').value("Recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].description').value("description for recipe4"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].productId.size()').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].productId[0]').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].productId[1]').value(5))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].productId[2]').value(7))
    }

    @WithMockUser()
    def "test should return recipe by id"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/{id}", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.id').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name').value("Recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description').value("description for recipe3"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.productId.size()').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.productId[0]').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.productId[1]').value(4))
    }
}
