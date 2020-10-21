package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
@RequiredArgsConstructor
public class RecipeEndpoint {

    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createRecipe(@RequestBody Recipe recipe) {
        recipeService.addRecipeToDb(recipe);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRecipe(@RequestParam Integer id){
        recipeService.deleteRecipeFromDb(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    void updateRecipe(@RequestBody Recipe recipe){
        recipeService.updateRecipeInDb(recipe);
    }

    @GetMapping
    List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    Recipe getById(@PathVariable Integer id){
        return recipeService.getOne(id);
    }

}
