package pl.sda.demo.domain.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository, passwordEncoder);
    private final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    @Test
    void testShouldFindUserByUsername() {
        //given
        String username = "username";
        //when
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")));
        User result = userService.findByUsername(username);
        //then
        Mockito.verify(userRepository).findByUsername(username);
        assertEquals("username", result.getUsername());
        assertEquals(1, result.getId());
        assertEquals("user", result.getRole());
        assertEquals("password", result.getPassword());

    }

    @Test
    void testShouldThrowExceptionForIncorrectUsername() {
        //given
        String username = "user";
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.findByUsername(username));
        //then
        Mockito.verify(userRepository).findByUsername(username);
        assertEquals("Wrong username", ex.getMessage());
    }

    @Test
    void testShouldCreateUser() {
        //given
        User user = new User(null, "username", "password", new ArrayList<>(), new ArrayList<>(), "user");
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        userService.createUser(user);
        Mockito.verify(userRepository).addUser(userArgumentCaptor.capture());
        User result = userArgumentCaptor.getValue();
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals("username", result.getUsername());
        assertEquals("user", result.getRole());
        assertTrue(passwordEncoder.matches("password", result.getPassword()));
    }

    @Test
    void testShouldThrowExceptionForExistingUser() {
        //given
        User user = new User(null, "username", "password", new ArrayList<>(), new ArrayList<>(), "user");
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.createUser(user));
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals("Username already taken", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).addUser(user);
    }

    @Test
    void testShouldUpdateUser() {
        //given
        User user = new User(1, "username", "newPassword", new ArrayList<>(), new ArrayList<>(), "user");
        //when
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(new User(1, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")));
        userService.updateUser(user);
        Mockito.verify(userRepository).updateUser(userArgumentCaptor.capture());
        User result = userArgumentCaptor.getValue();
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals(1, result.getId());
        assertEquals("username", result.getUsername());
        assertTrue(passwordEncoder.matches("newPassword", result.getPassword()));
    }

    @Test
    void testShouldThrowExceptionIfIdIsDifferent() {
        //given
        User user = new User(1, "username", "newPassword", new ArrayList<>(), new ArrayList<>(), "user");
        //when
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(new User(2, "username", "password", new ArrayList<>(), new ArrayList<>(), "user")));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.updateUser(user));
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals("Cannot update user with different id", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).updateUser(user);
    }


    @Test
    void testShouldThrowExceptionIfProductAlreadyInFridge() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        User user = new User(1, "username", "newPassword", ids, new ArrayList<>(), "user");
        Product product = new Product(1, "Milk");
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.addProductToFridge(product, user));
        //then
        assertEquals("Product already in fridge", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).addProductToFridge(product, user);
    }

    @Test
    void testShouldThrowExceptionIfProductIsNotInFridge() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        User user = new User(1, "username", "newPassword", ids, new ArrayList<>(), "user");
        int id = 1;
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.removeProductFromFridge(id, user));
        //then
        assertEquals("You dont have this product in your fridge", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).removeProductFromFridge(id, user);
    }

    @Test
    void testShouldThrowExceptionIfRecipeAlreadyInFavourites() {
        //given
        List<Integer> recipes = new ArrayList<>();
        recipes.add(1);
        User user = new User(1, "username", "newPassword", new ArrayList<>(), recipes, "user");
        Recipe recipe = new Recipe(1, "recipe", "description", new ArrayList<>());
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.addRecipeToFavourites(recipe, user));
        //then
        assertEquals("Recipe already in favourites", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).addRecipeToFavourites(recipe, user);
    }

    @Test
    void testShouldThrowExceptionIfRecipeIsNotInFavourites() {
        //given
        List<Integer> recipes = new ArrayList<>();
        recipes.add(2);
        User user = new User(1, "username", "newPassword", new ArrayList<>(), recipes, "user");
        int id = 1;
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.deleteRecipeFromFavourites(id, user));
        //then
        assertEquals("Recipe is not in favourites", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).deleteRecipeFromFavourites(id, user);
    }

    @Test
    void testShouldReturnAllProductsFromFridge() {
        //given
        String username = "user";
        List<Product> products = new ArrayList<>();
        Product product1 = new Product(1, "product1");
        Product product2 = new Product(2, "product2");
        products.add(product1);
        products.add(product2);
        //when
        Mockito.when(userRepository.findAllProductsFromUserFridge(username)).thenReturn(products);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        List<Product> result = userService.findAllProductsFromUserFridge(username);
        //then
        Mockito.verify(userRepository).findByUsername(username);
        Mockito.verify(userRepository).findAllProductsFromUserFridge(username);
        assertEquals(products, result);
    }
    @Test
    void testShouldThrowExceptionGettingProductsFromFridgeWhenUsernameIsIncorrect() {
        //given
        String username = "user";
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.findAllProductsFromUserFridge(username));
        //then
        Mockito.verify(userRepository).findByUsername(username);
        assertEquals("User doesnt exist", ex.getMessage());
    }
}