package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;

import java.util.List;

@RestController
@RequestMapping("api/favourites")
@RequiredArgsConstructor
public class FavouritesEndpoint {

    private final RecipeService recipeService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addToFavourites(@RequestParam Integer id) {
        Recipe recipe = recipeService.getOne(id);
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.getUsername());
        userService.addRecipeToFavourites(recipe, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeFromFavourites(@RequestParam Integer id) {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.getUsername());
        userService.deleteRecipeFromFavourites(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Recipe> getAllFavourites() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.getUsername());
        return userService.getAllFavourites(user.getUsername());
    }
}
