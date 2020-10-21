package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.api.RecipeUserDTO;

@RestController
@RequestMapping("api/favourites")
@RequiredArgsConstructor
public class FavouritesEndpoint {

    private final RecipeService recipeService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addToFavourites(@RequestBody RecipeUserDTO recipeUserDTO){
        Recipe recipe = recipeService.getOne(recipeUserDTO.getRecipeId());
        User user = userService.findByUsername(recipeUserDTO.getUsername());
        userService.addRecipeToFavourites(recipe, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeFromFavourites(@RequestBody RecipeUserDTO recipeUserDTO){
        User user = userService.findByUsername(recipeUserDTO.getUsername());
        userService.deleteRecipeFromFavourites(recipeUserDTO.getRecipeId(),user);
    }
}
