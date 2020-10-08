package pl.sda.demo.domain.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);
    private final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    @Test
    void testShouldCreateUser() {
        //given
        User user = new User(null, "username", "password", new ArrayList<>(), new ArrayList<>());
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        userService.createUser(user);
        Mockito.verify(userRepository).createUser(userArgumentCaptor.capture());
        User result = userArgumentCaptor.getValue();
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals("username", result.getUsername());
        assertEquals("password", result.getPassword());
    }

    @Test
    void testShouldThrowExceptionForExistingUser() {
        //given
        User user = new User(null, "username", "password", new ArrayList<>(), new ArrayList<>());
        //when
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.createUser(user));
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals("Username already taken", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).createUser(user);
    }

    @Test
    void testShouldUpdateUser() {
        //given
        User user = new User(1, "username", "newPassword", new ArrayList<>(), new ArrayList<>());
        //when
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(new User(1, "username", "password", new ArrayList<>(), new ArrayList<>())));
        userService.updateUser(user);
        Mockito.verify(userRepository).updateUser(userArgumentCaptor.capture());
        User result = userArgumentCaptor.getValue();
        //then
        Mockito.verify(userRepository).findByUsername("username");
        assertEquals(1, result.getId());
        assertEquals("username", result.getUsername());
        assertEquals("newPassword", result.getPassword());
    }

    @Test
    void testShouldThrowExceptionIfIdIsDifferent() {
        //given
        User user = new User(1, "username", "newPassword", new ArrayList<>(), new ArrayList<>());
        //when
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(new User(2, "username", "password", new ArrayList<>(), new ArrayList<>())));
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
        User user = new User(1, "username", "newPassword", ids, new ArrayList<>());
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
        User user = new User(1, "username", "newPassword", ids, new ArrayList<>());
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
        User user = new User(1, "username", "newPassword", new ArrayList<>(), recipes);
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
        User user = new User(1, "username", "newPassword", new ArrayList<>(), recipes);
        int id = 1;
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.deleteRecipeFromFavourites(id, user));
        //then
        assertEquals("Recipe is not in favourites", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).deleteRecipeFromFavourites(id, user);
    }
}