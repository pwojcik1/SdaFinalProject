package pl.sda.demo.domain.user

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pl.sda.demo.domain.product.Product
import pl.sda.demo.domain.recipe.Recipe
import spock.lang.Specification

class UserServiceSpec extends Specification {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()
    def userRepository = Mock(UserRepository)
    UserService userService = new UserService(userRepository, passwordEncoder)


    def "should create user"() {
        given: "new user"
        User user = User.builder().username("testUser").password("password").role("USER").build()
        def pwd

        when: "service creates user"
        userService.createUser(user)

        then: "repository is called with expected parameters"
        1 * userRepository.findByUsername("testUser") >> Optional.empty()
        1 * userRepository.createUser(_ as User) >> { arguments ->
            assert arguments[0].username == "testUser" &&
                    arguments[0].productId == null &&
                    arguments[0].recipeId == null &&
                    arguments[0].role == "USER"
            pwd = arguments[0].password
        }
        passwordEncoder.matches("password", pwd)
    }

    def "should throw exception when user already exists"() {
        given: "new user"
        User user = User.builder().username("testUser").password("password").role("USER").build()
        when: "service tries to create user"
        userService.createUser(user)
        then: "exception is thrown because user with given username already exists"
        1 * userRepository.findByUsername("testUser") >> Optional.of(new User())

        def e = thrown(IllegalStateException)
        e.message == "Username already taken"

        0 * userRepository.createUser(_)
    }

    def "should update user in db"() {
        given: "user"
        User newUser = User.builder().id(1).username("testUser").password("newPassword").role("USER").build()
        User oldUser = User.builder().id(1).username("testUser").password("password").role("USER").build()
        def pwd
        when: "service updates user"
        userService.updateUser(newUser)
        then: "repository is called with expected parameters"
        1 * userRepository.findByUsername("testUser") >> Optional.of(oldUser)
        1 * userRepository.updateUser(_ as User) >> { arguments ->
            assert arguments[0].username == "testUser" &&
                    arguments[0].productId == null &&
                    arguments[0].recipeId == null &&
                    arguments[0].role == "USER"
            pwd = arguments[0].password
        }
        passwordEncoder.matches("newPassword", pwd)
    }

    def "should throw exception when id is different"() {
        given: "user"
        User newUser = User.builder().id(1).username("testUser").password("newPassword").role("USER").build()
        User oldUser = User.builder().id(2).username("testUser").password("password").role("USER").build()
        when: "service tries to update user"
        userService.updateUser(newUser)
        then: "exception is thrown"
        1 * userRepository.findByUsername("testUser") >> Optional.of(oldUser)

        def e = thrown(IllegalStateException)
        e.message == "Cannot update user with different id"

        0 * userRepository.updateUser(_)
    }

    def "should add product to fridge"() {
        given: "existing user and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.productId << 1
        Product product = new Product(2, "Product2")

        when: "service tries to add product"
        userService.addProductToFridge(product, user)

        then: "repository is called"
        1 * userRepository.addProductToFridge(product, user)
    }

    def "should throw exception when product is already in fridge"() {
        given: "existing user and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.productId << 1
        Product product = new Product(1, "Product1")

        when: "service tries to add product"
        userService.addProductToFridge(product, user)

        then: "exception is thrown"
        def e = thrown(IllegalStateException)
        e.message == "Product already in fridge"

        0 * userRepository.addProductToFridge(_, _)
    }

    def "should remove product from fridge"() {
        given: "existing user and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.productId << 1
        Product product = new Product(1, "Product1")

        when: "service tries to remove product"
        userService.removeProductFromFridge(product.id, user)

        then: "repository is called"
        1 * userRepository.removeProductFromFridge(product.id, user)
    }

    def "should throw exception when product is not in fridge"() {
        given: "existing user and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.productId << 1
        Product product = new Product(2, "Product2")

        when: "service tries to remove product"
        userService.removeProductFromFridge(product.id, user)

        then: "exception is thrown"
        def e = thrown(IllegalStateException)
        e.message == "You dont have this product in your fridge"

        0 * userRepository.removeProductFromFridge(_, _)
    }


    def "should add recipe to favourites"() {
        given: "existing recipe and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        Recipe recipe = new Recipe(1, "recipe", "description", new ArrayList<>())

        when: "service tries to add recipe"
        userService.addRecipeToFavourites(recipe, user)

        then: "repository is called"
        1 * userRepository.addRecipeToFavourites(recipe, user)
    }

    def "should throw exception when recipe is already in favourites"() {
        given: "existing recipe and product"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.recipeId << 1
        Recipe recipe = new Recipe(1, "recipe", "description", new ArrayList<>())

        when: "service tries to add recipe"
        userService.addRecipeToFavourites(recipe, user)

        then: "exception is thrown"
        def e = thrown(IllegalStateException)
        e.message == "Recipe already in favourites"

        0 * userRepository.addRecipeToFavourites(_, _)
    }

    def "should remove recipe from favourites"() {
        given: "existing recipe and user"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        user.recipeId << 1
        Recipe recipe = new Recipe(1, "recipe", "description", new ArrayList<>());

        when: "service tries to remove recipe"
        userService.deleteRecipeFromFavourites(recipe.id, user)

        then: "repository is called"
        1 * userRepository.deleteRecipeFromFavourites(recipe.id, user)
    }

    def "should throw exception when recipe is not in favourites"() {
        given: "existing recipe and user"
        User user = new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")
        Recipe recipe = new Recipe(1, "recipe", "description", new ArrayList<>());

        when: "service tries to remove recipe"
        userService.deleteRecipeFromFavourites(recipe.id, user)

        then: "exception is thrown"
        def e = thrown(IllegalStateException)
        e.message == "Recipe is not in favourites"

        0 * userRepository.deleteRecipeFromFavourites(_, _)
    }

    def "should return all products from fridge"(){
        when:
        userService.getAllProductsFromFridge("username")

        then:
        1 * userRepository.findByUsername("username") >> Optional.of(new User())
        1 * userRepository.getAllProductsFromFridge("username")
    }

    def "should throw exception if user doesnt exists"(){
        when: "trying to get products form fridge of nonexistent user"
        userService.getAllProductsFromFridge("username")
        then: "exception is thrown"
        1 * userRepository.findByUsername("username") >> Optional.empty()
        def e = thrown(IllegalStateException)
        e.message == "User doesnt exist"

        when: "trying to get nonexistent user"
        userService.findByUsername("username")
        then:"exception is thrown"
        1 * userRepository.findByUsername("username") >> Optional.empty()
        def ex = thrown(IllegalStateException)
        ex.message == "Wrong username"
    }

    def "should return user by username"(){
        when: "trying to get nonexistent user"
        userService.findByUsername("username")
        then:
        1 * userRepository.findByUsername("username") >> Optional.of(new User())
        notThrown(IllegalStateException)
    }

}
