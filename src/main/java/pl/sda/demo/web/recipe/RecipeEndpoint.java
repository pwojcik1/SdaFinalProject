package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeEndpoint {

    private final RecipeService recipeService;

    @PostMapping
    void addRecipeToDb(@RequestBody Recipe recipe) {
        recipeService.addRecipeToDb(recipe);
    }

    @PutMapping
    void updateRecipeInDb(@RequestBody Recipe recipe) {
        recipeService.updateRecipeInDb(recipe);
    }

    @DeleteMapping
    void deleteRecipeFromDb(@RequestParam int id) {
        recipeService.deleteRecipeFromDb(id);

    }

    @GetMapping("/{name}")
    Recipe findByRecipeName(@RequestParam String name) {
        return recipeService.findByRecipeName(name);
    }

    @GetMapping
    Set<Recipe> findByProducts(@RequestParam List<Product> products) {
        return recipeService.findByProducts(products);
    }
}
