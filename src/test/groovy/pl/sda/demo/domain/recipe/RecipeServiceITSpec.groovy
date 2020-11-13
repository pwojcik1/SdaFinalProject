package pl.sda.demo.domain.recipe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.sda.demo.domain.product.Product
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
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
    private JpaRecipeRepository jpaRecipeRepository

    @Autowired
    private RecipeService recipeService

    def setup() {
        ProductEntity productEntity1 = new ProductEntity(1, "product1")
        ProductEntity productEntity2 = new ProductEntity(2, "product2")
        ProductEntity productEntity3 = new ProductEntity(3, "product3")
        jpaProductRepository.save(productEntity1)
        jpaProductRepository.save(productEntity2)
        jpaProductRepository.save(productEntity3)

        RecipeEntity recipeEntity = new RecipeEntity(null, "testName", "testDescription", new HashSet<ProductEntity>())
        recipeEntity.products << productEntity1
        recipeEntity.products << productEntity2
        recipeEntity.products << productEntity3
        jpaRecipeRepository.save(recipeEntity)
    }

    def "should add recipe to db"() {
        given:
        Recipe recipe = new Recipe(null, "recipeName", "recipeDescription", new ArrayList<Integer>())
        recipe.productId << 1
        recipe.productId << 2

        when:
        recipeService.addRecipeToDb(recipe)
        RecipeEntity result = jpaRecipeRepository.getOne(2)
        then:
        result.id == 2
        result.name == "recipeName"
        result.description == "recipeDescription"
        result.products.size() == 2
        result.products.contains(new ProductEntity(1, "product1"))
        result.products.contains(new ProductEntity(2, "product2"))
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
        result.products.contains(new ProductEntity(3, "product3"))
    }

    def "should delete recipe from db"() {
        when:
        recipeService.deleteRecipeFromDb(1)
        Optional<RecipeEntity> result = jpaRecipeRepository.findRecipeById(1)
        then:
        result.isEmpty()
    }

    def "should return recipe by name"() {
        when:
        Recipe result = recipeService.findByRecipeName("testName")
        then:
        result.id == 1
        result.name == "testName"
        result.description == "testDescription"
        result.productId.size() == 3
        result.productId.contains(1)
        result.productId.contains(2)
        result.productId.contains(3)
    }

    def "should find recipe by products"() {
        given:
        Recipe recipe = new Recipe(2, "recipeName", "recipeDescription", new ArrayList<Integer>())
        recipe.productId << 1
        recipe.productId << 2
        recipeService.addRecipeToDb(recipe)

        Product product1 = new Product(1, "product1")
        Product product2 = new Product(2, "product2")
        List<Product> products = new ArrayList<>()
        products << product1
        products << product2
        when:
        Set<Recipe> result = recipeService.findByProducts(products)
        then:
        result.size() == 1
        result.contains(recipe)
    }

    def "should return recipe by id"() {
        when:
        Recipe result = recipeService.getOne(1)
        then:
        result.id == 1
        result.name == "testName"
        result.description == "testDescription"
        result.productId.size() == 3
        result.productId.contains(1)
        result.productId.contains(2)
        result.productId.contains(3)
    }

    def "should return all recipes"() {
        given:
        Recipe recipe = new Recipe(2, "recipeName", "recipeDescription", new ArrayList<Integer>())
        recipe.productId << 1
        recipe.productId << 2
        recipeService.addRecipeToDb(recipe)

        Recipe recipe1 = new Recipe(1, "testName", "testDescription", new ArrayList<Integer>())
        recipe1.productId << 1
        recipe1.productId << 2
        recipe1.productId << 3
        when:
        List<Recipe> result = recipeService.getAllRecipes()
        then:
        result.size() == 2
        result.contains(recipe)
        result.contains(recipe1)
    }
}
