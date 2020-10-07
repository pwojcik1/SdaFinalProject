package pl.sda.demo.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    @PostMapping
    void createUser(@RequestBody @Valid User user) {
        userService.createUser(user);
    }

    @PutMapping
    void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @DeleteMapping
    void deleteUser(@RequestParam int id) {
        userService.deleteUser(id);
    }

    @PostMapping
    void addProductToFridge(@RequestBody @Valid Product product, User user) {
        userService.addProductToFridge(product, user);
    }

    @DeleteMapping
    void removeProductFromFridge(@RequestParam int id, @RequestBody User user) {
        userService.removeProductFromFridge(id, user);
    }

    @PostMapping
    void addRecipeToFavourites(@RequestBody Recipe recipe, User user) {
        userService.addRecipeToFavourites(recipe, user);
    }

    @DeleteMapping
    void deleteRecipeFromFavourites(@RequestParam int id, @RequestBody User user) {
        userService.deleteRecipeFromFavourites(id, user);
    }
}
