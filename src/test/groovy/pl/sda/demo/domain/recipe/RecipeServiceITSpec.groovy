package pl.sda.demo.domain.recipe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.sda.demo.domain.product.Product
import pl.sda.demo.domain.product.ProductService
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.recipe.JpaRecipeRepository
import pl.sda.demo.external.recipe.RecipeEntity
import spock.lang.Specification

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecipeServiceITSpec extends Specification {

    @Autowired
    private JpaProductRepository jpaProductRepository
    @Autowired
    private ProductService productService

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository

    @Autowired
    private RecipeService recipeService

    def "should add recipe to db"() {
        given:
        Recipe recipe = new Recipe(null, "recipeName", "recipeDescription", new ArrayList<Integer>())
        recipe.productId << 1
        recipe.productId << 2

        when:
        recipeService.addRecipeToDb(recipe)
        RecipeEntity result = jpaRecipeRepository.getOne(5)
        then:
        result.id == 5
        result.name == "recipeName"
        result.description == "recipeDescription"
        result.products.size() == 2
        result.products.contains(jpaProductRepository.getOne(1))
        result.products.contains(jpaProductRepository.getOne(2))
    }

    def "should update recipe in db"() {
        given:
        Recipe updateRecipe = new Recipe(1, "newName", "new Description", new ArrayList<Integer>())
        updateRecipe.productId << 3
        when:
        recipeService.updateRecipeInDb(updateRecipe)
        RecipeEntity result = jpaRecipeRepository.getOne(1)
        then:
        result.id == 1
        result.name == "newName"
        result.description == "new Description"
        result.products.size() == 1
        result.products.contains(jpaProductRepository.getOne(3))
    }

    def "should delete recipe from db"() {
        when:
        recipeService.deleteRecipeFromDb(1)
        Optional<RecipeEntity> result = jpaRecipeRepository.findById(1)
        then:
        result.isEmpty()
    }

    def "should return recipe by name"() {
        when:
        Recipe result = recipeService.findRecipeByName("Recipe4")
        then:
        result.id == 4
        result.name == "Recipe4"
        result.description == "description for recipe4"
        result.productId.size() == 3
        result.productId.contains(3)
        result.productId.contains(5)
        result.productId.contains(7)
    }

    def "should find recipe by products"() {
        given:
        List<Product> products = new ArrayList<>()
        products << productService.findProductById(1)
        products << productService.findProductById(4)
        products << productService.findProductById(6)
        when:
        Set<Recipe> result = recipeService.findRecipeByProducts(products)
        then:
        result.size() == 1
        result.contains(recipeService.findRecipeById(1))
    }

    def "should return recipe by id"() {
        when:
        Recipe result = recipeService.findRecipeById(3)
        then:
        result.id == 3
        result.name == "Recipe3"
        result.description == "description for recipe3"
        result.productId.size() == 2
        result.productId.contains(2)
        result.productId.contains(4)
    }

    def "should return all recipes"() {
        when:
        List<Recipe> result = recipeService.findAllRecipes()
        then:
        result.size() == 4
        result.contains(recipeService.findRecipeById(1))
        result.contains(recipeService.findRecipeById(2))
        result.contains(recipeService.findRecipeById(3))
        result.contains(recipeService.findRecipeById(4))
    }
}
