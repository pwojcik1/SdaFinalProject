package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.ApiMapService;
import pl.sda.demo.dto.RecipeDto;

import java.util.List;

@RestController
@RequestMapping("api/favourites")
@RequiredArgsConstructor
public class FavouritesEndpoint {

    private final RecipeService recipeService;
    private final UserService userService;
    private final ApiMapService apiMapService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addToFavourites(@RequestParam Integer id) {
        Recipe recipe = recipeService.findRecipeById(id);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        userService.addRecipeToFavourites(recipe, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeFromFavourites(@RequestParam Integer id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        userService.deleteRecipeFromFavourites(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<RecipeDto> getAllFavourites() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        List<Recipe> allRecipes = userService.findAllUserFavourites(user.getUsername());
        return apiMapService.mapToRecipeDto(allRecipes);

    }
}
