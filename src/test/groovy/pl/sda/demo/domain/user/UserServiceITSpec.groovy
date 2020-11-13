package pl.sda.demo.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.sda.demo.domain.product.Product
import pl.sda.demo.domain.recipe.Recipe
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
import pl.sda.demo.external.recipe.JpaRecipeRepository
import pl.sda.demo.external.recipe.RecipeEntity
import pl.sda.demo.external.user.DatabaseUserRepository
import pl.sda.demo.external.user.JpaUserRepository
import pl.sda.demo.external.user.UserEntity
import spock.lang.Specification

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceITSpec extends Specification {

    @Autowired
    private JpaUserRepository jpaUserRepository
    @Autowired
    private UserService userService
    @Autowired
    private JpaProductRepository jpaProductRepository
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository
    @Autowired
    private DatabaseUserRepository databaseUserRepository
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    def setup() {
        ProductEntity productEntity1 = new ProductEntity(1, "product1")
        ProductEntity productEntity2 = new ProductEntity(2, "product2")
        ProductEntity productEntity3 = new ProductEntity(3, "product3")
        jpaProductRepository.save(productEntity1)
        jpaProductRepository.save(productEntity2)
        jpaProductRepository.save(productEntity3)

        RecipeEntity recipeEntity1 = new RecipeEntity(1, "recipe1", "desc1", new HashSet<ProductEntity>())
        recipeEntity1.products << productEntity1
        recipeEntity1.products << productEntity2
        RecipeEntity recipeEntity2 = new RecipeEntity(2, "recipe2", "desc2", new HashSet<ProductEntity>())
        recipeEntity2.products << productEntity3
        jpaRecipeRepository.save(recipeEntity1)
        jpaRecipeRepository.save(recipeEntity2)

        UserEntity userEntity = new UserEntity(1, "username", passwordEncoder.encode("password"), "user", new HashSet<RecipeEntity>(), new HashSet<ProductEntity>())
        userEntity.products << productEntity3
        userEntity.products << productEntity2
        userEntity.favourites << recipeEntity1
        jpaUserRepository.save(userEntity)
    }

    def "should return user by username"() {
        when:
        User result = userService.findByUsername("username")
        then:
        result.username == "username"
        result.id == 1
        passwordEncoder.matches("password", result.password)
        result.productId.size() == 2 && result.productId.contains(2) && result.productId.contains(3)
        result.recipeId.size() == 1 && result.recipeId.contains(1)
        result.role == "user"
    }

    def "should throw exception when user with given username doesnt exist"() {
        when:
        userService.findByUsername("user")
        then:
        def e = thrown(IllegalStateException)
        e.message == "Wrong username"
    }

    def "should add new user to db"() {
        given:
        User user = new User(null, "testUsername", "testPass", new ArrayList<Integer>(), new ArrayList<Integer>(), "user")
        when:
        userService.createUser(user)
        UserEntity result = jpaUserRepository.getOne(2)
        then:
        result.id == 2
        result.username == "testUsername"
        passwordEncoder.matches("testPass", result.password)
        result.favourites.isEmpty()
        result.products.isEmpty()
    }

    def "should throw exception if username is taken"() {
        given:
        User user = new User(1, "username", "pass", new ArrayList<Integer>(), new ArrayList<Integer>(), "user")
        when:
        userService.createUser(user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Username already taken"
    }

    def "should update user in db"() {
        given:
        User user = new User(1, "newUsername", "newPassword", new ArrayList<Integer>(), new ArrayList<Integer>(), "user")
        when:
        userService.updateUser(user)
        UserEntity result = jpaUserRepository.getOne(1)
        then:
        result.id == 1
        result.username == "username"
        passwordEncoder.matches("newPassword", result.password)
    }

    def "should delete user from db"() {
        when:
        userService.deleteUser(1)
        Optional<UserEntity> result = jpaUserRepository.getUserByName("username")
        then:
        result.isEmpty()
    }

    def "should add product to fridge"() {
        given:
        User user = userService.findByUsername("username")
        Product product = new Product(1, "product1")
        when:
        userService.addProductToFridge(product, user)
        User result = userService.findByUsername("username")
        then:
        result.productId.size() == 3
        result.productId.contains(1)
        result.productId.contains(2)
        result.productId.contains(3)
    }

    def "should throw exception if product is already in fridge"() {
        given:
        User user = userService.findByUsername("username")
        Product product = new Product(3, "product3")
        when:
        userService.addProductToFridge(product, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product already in fridge"
    }

    def "should remove product from fridge"() {
        given:
        User user = userService.findByUsername("username")
        when:
        userService.removeProductFromFridge(3, user)
        User result = userService.findByUsername("username")
        then:
        user.productId.size() == 2
        user.productId.contains(3)
        user.productId.contains(2)
        result.productId.size() == 1
        result.productId.contains(2)
    }

    def "should throw exception when product is not in fridge"() {
        given:
        User user = userService.findByUsername("username")
        when:
        userService.removeProductFromFridge(1, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "You dont have this product in your fridge"
    }

    def "should add recipe to favourites"() {
        given:
        Recipe recipe = new Recipe(2, "recipe2", "desc2", new ArrayList<Integer>())
        recipe.productId << 3
        User user = userService.findByUsername("username")
        when:
        userService.addRecipeToFavourites(recipe, user)
        User result = userService.findByUsername("username")
        then:
        result.recipeId.size() == 2
        result.recipeId.contains(1)
        result.recipeId.contains(2)
    }

    def "should throw exception if recipe is already in favourites"() {
        given:
        Recipe recipe = new Recipe(1, "recipe2", "desc2", new ArrayList<Integer>())
        recipe.productId << 1
        recipe.productId << 2
        User user = userService.findByUsername("username")
        when:
        userService.addRecipeToFavourites(recipe, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Recipe already in favourites"
    }

    def "should delete recipe from favourites"() {
        given:
        User user = userService.findByUsername("username")
        when:
        userService.deleteRecipeFromFavourites(1, user)
        User result = userService.findByUsername("username")
        then:
        user.recipeId.contains(1)
        result.recipeId.isEmpty()
    }

    def "should throw exception if recipe is not in favourites"() {
        given:
        User user = userService.findByUsername("username")
        when:
        userService.deleteRecipeFromFavourites(2, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Recipe is not in favourites"
    }

    def "should return all products from fridge"() {
        when:
        List<Product> result = userService.getAllProductsFromFridge("username")
        then:
        result.size() == 2
        result.contains(new Product(2, "product2"))
        result.contains(new Product(3, "product3"))
    }

    def "should throw exception when user doesnt exist"() {
        when:
        userService.getAllProductsFromFridge("user")
        then:
        def e = thrown(IllegalStateException)
        e.message == "User doesnt exist"
    }

    def "should return all favourites"() {
        when:
        List<Recipe> result = userService.getAllFavourites("username")
        Recipe recipe = new Recipe(1, "recipe1", "desc1", new ArrayList<Integer>())
        recipe.productId << 1 << 2
        then:
        result.size() == 1
        result.contains(recipe)
    }
}
