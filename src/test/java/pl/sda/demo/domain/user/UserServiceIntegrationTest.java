package pl.sda.demo.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.user.DatabaseUserRepository;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JpaProductRepository jpaProductRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private DatabaseUserRepository databaseUserRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testShouldCreateUser() {
        //given
        User user = new User(null, "username", "password", null, null, "user");
        //when
        userService.createUser(user);
        //then
        UserEntity result = jpaUserRepository.getOne(4);
        assertEquals(4, result.getId());
        assertEquals("username", result.getUsername());
        assertEquals("user", result.getRole());
        assertTrue(passwordEncoder.matches("password", result.getPassword()));
        assertTrue(result.getFavourites().isEmpty());
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void updateUser() {
        //given
        User user = new User(1, "newUsername", "newPassword", null, null, "user");
        //when
        userService.updateUser(user);
        //then
        UserEntity result = jpaUserRepository.getOne(1);
        assertEquals(1, result.getId());
        assertEquals("user1", result.getUsername());
        assertTrue(passwordEncoder.matches("newPassword", result.getPassword()));

        assertEquals(2, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(1)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(2)));

        assertEquals(7, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(3)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(5)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(6)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(7)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(8)));
    }

    @Test
    void deleteUser() {
        //given
        //when
        userService.deleteUser(1);
        //then
        Optional<UserEntity> result = jpaUserRepository.findById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void addProductToFridge() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("user2");
        Product product = productService.findProductById(5);
        //when
        assertTrue(user.isPresent());
        userService.addProductToFridge(product, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(2);

        assertEquals(2, result.getId());
        assertEquals("user2", result.getUsername());
        assertEquals("password2", result.getPassword());

        assertEquals(2, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(4)));

        assertEquals(3, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(5)));
    }

    @Test
    void removeProductFromFridge() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("user2");
        //when
        assertTrue(user.isPresent());
        userService.removeProductFromFridge(1, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(2);

        assertEquals(2, result.getId());
        assertEquals("user2", result.getUsername());
        assertEquals("password2", result.getPassword());

        assertEquals(2, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(4)));

        assertEquals(1, result.getProducts().size());
        assertFalse(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
    }

    @Test
    void addRecipeToFavourites() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("user2");
        Recipe recipe = recipeService.findRecipeById(1);
        //when
        assertTrue(user.isPresent());
        userService.addRecipeToFavourites(recipe, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(2);

        assertEquals(2, result.getId());
        assertEquals("user2", result.getUsername());
        assertEquals("password2", result.getPassword());

        assertEquals(3, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(1)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(4)));

        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));

    }

    @Test
    void deleteRecipeFromFavourites() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("user2");
        //when
        assertTrue(user.isPresent());
        userService.deleteRecipeFromFavourites(2, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(2);

        assertEquals(2, result.getId());
        assertEquals("user2", result.getUsername());
        assertEquals("password2", result.getPassword());

        assertEquals(1, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(jpaRecipeRepository.getOne(4)));

        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
    }
}