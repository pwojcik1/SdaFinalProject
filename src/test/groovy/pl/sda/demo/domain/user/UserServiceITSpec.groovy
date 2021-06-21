package pl.sda.demo.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.sda.demo.domain.product.Product
import pl.sda.demo.domain.product.ProductService
import pl.sda.demo.domain.recipe.Recipe
import pl.sda.demo.domain.recipe.RecipeService
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.recipe.JpaRecipeRepository
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
    private ProductService productService
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository
    @Autowired
    private RecipeService recipeService
    @Autowired
    private DatabaseUserRepository databaseUserRepository
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()


    def "should return user by username"() {
        when:
        User result = userService.findByUsername("user2")
        then:
        result.username == "user2"
        result.id == 2
        result.productId.size() == 2 && result.productId.contains(1) && result.productId.contains(2)
        result.recipeId.size() == 2 && result.recipeId.contains(2) && result.recipeId.contains(4)
        result.role == "USER"
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
        UserEntity result = jpaUserRepository.getOne(4)
        then:
        result.id == 4
        result.username == "testUsername"
        passwordEncoder.matches("testPass", result.password)
        result.favourites.isEmpty()
        result.products.isEmpty()
    }

    def "should throw exception if username is taken"() {
        given:
        User user = new User(1, "user1", "pass", new ArrayList<Integer>(), new ArrayList<Integer>(), "user")
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
        result.username == "user1"
        passwordEncoder.matches("newPassword", result.password)
    }

    def "should delete user from db"() {
        when:
        userService.deleteUser(1)
        Optional<UserEntity> result = jpaUserRepository.findUserByName("user1")
        then:
        result.isEmpty()
    }

    def "should add product to fridge"() {
        given:
        User user = userService.findByUsername("user2")
        Product product = productService.findProductById(5)
        when:
        userService.addProductToFridge(product, user)
        User result = userService.findByUsername("user2")
        then:
        result.productId.size() == 3
        result.productId.contains(1)
        result.productId.contains(2)
        result.productId.contains(5)
    }

    def "should throw exception if product is already in fridge"() {
        given:
        User user = userService.findByUsername("user2")
        Product product = productService.findProductById(2)
        when:
        userService.addProductToFridge(product, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product already in fridge"
    }

    def "should remove product from fridge"() {
        given:
        User user = userService.findByUsername("user2")
        when:
        userService.removeProductFromFridge(1, user)
        User result = userService.findByUsername("user2")
        then:
        user.productId.size() == 2
        user.productId.contains(1)
        user.productId.contains(2)
        result.productId.size() == 1
        result.productId.contains(2)
    }

    def "should throw exception when product is not in fridge"() {
        given:
        User user = userService.findByUsername("user2")
        when:
        userService.removeProductFromFridge(5, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "You dont have this product in your fridge"
    }

    def "should add recipe to favourites"() {
        given:
        User user = userService.findByUsername("user1")
        Recipe recipe = recipeService.findRecipeById(4)
        when:
        userService.addRecipeToFavourites(recipe, user)
        User result = userService.findByUsername("user1")
        then:
        result.recipeId.size() == 3
        result.recipeId.contains(1)
        result.recipeId.contains(2)
        result.recipeId.contains(4)
    }

    def "should throw exception if recipe is already in favourites"() {
        given:
        Recipe recipe = recipeService.findRecipeById(2)
        User user = userService.findByUsername("user1")
        when:
        userService.addRecipeToFavourites(recipe, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Recipe already in favourites"
    }

    def "should delete recipe from favourites"() {
        given:
        User user = userService.findByUsername("user1")
        when:
        userService.deleteRecipeFromFavourites(1, user)
        User result = userService.findByUsername("user1")
        then:
        user.recipeId.size() == 2
        user.recipeId.contains(1)
        user.recipeId.contains(2)
        result.recipeId.size() == 1
        result.recipeId.contains(2)
    }

    def "should throw exception if recipe is not in favourites"() {
        given:
        User user = userService.findByUsername("user1")
        when:
        userService.deleteRecipeFromFavourites(3, user)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Recipe is not in favourites"
    }

    def "should return all products from fridge"() {
        when:
        List<Product> result = userService.findAllProductsFromUserFridge("user2")
        then:
        result.size() == 2
        result.contains(productService.findProductById(1))
        result.contains(productService.findProductById(2))
    }

    def "should throw exception when user doesnt exist"() {
        when:
        userService.findAllProductsFromUserFridge("user")
        then:
        def e = thrown(IllegalStateException)
        e.message == "User doesnt exist"
    }

    def "should return all favourites"() {
        when:
        List<Recipe> result = userService.findAllUserFavourites("user1")
        then:
        result.size() == 2
        result.contains(recipeService.findRecipeById(1))
        result.contains(recipeService.findRecipeById(2))
    }
}
