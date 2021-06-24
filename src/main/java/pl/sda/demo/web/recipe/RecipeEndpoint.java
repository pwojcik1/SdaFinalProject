package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.dto.ApiMapService;
import pl.sda.demo.dto.RecipeDto;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
@RequiredArgsConstructor
public class RecipeEndpoint {

    private final RecipeService recipeService;
    private final ApiMapService apiMapService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    void createRecipe(@RequestBody Recipe recipe) {
        recipeService.addRecipeToDb(recipe);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteRecipe(@RequestParam Integer id) {
        recipeService.deleteRecipeFromDb(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    void updateRecipe(@RequestBody Recipe recipe) {
        recipeService.updateRecipeInDb(recipe);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<RecipeDto> getAllRecipes() {
        List<Recipe> allRecipes = recipeService.findAllRecipes();
        return apiMapService.mapToRecipeDto(allRecipes);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Recipe getById(@PathVariable Integer id) {
        return recipeService.findRecipeById(id);
    }

}
