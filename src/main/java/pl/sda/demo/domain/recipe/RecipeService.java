package pl.sda.demo.domain.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public void addRecipeToDb(Recipe recipe) {
        recipeRepository.findRecipeByName(recipe.getName())
                .ifPresent(r -> {
                    throw new IllegalStateException("Recipe with same name already exists");
                });
        recipeRepository.addRecipeToDb(recipe);
    }

    public void updateRecipeInDb(Recipe recipe) {
        recipeRepository.findRecipeById(recipe.getId())
                .filter(r -> !r.getId().equals(recipe.getId()))
                .ifPresent(recipe1 -> {
                    throw new IllegalStateException("Cannot update product with different id");
                });
        recipeRepository.updateRecipeInDb(recipe);
    }

    public void deleteRecipeFromDb(int id) {
        recipeRepository.deleteRecipeFromDb(id);
    }

    public Recipe findRecipeByName(String name) {
        return recipeRepository.findRecipeByName(name).orElseThrow(() -> new IllegalStateException("recipe with given name doesnt exist"));
    }

    public Set<Recipe> findRecipeByProducts(List<Product> products) {
        if (products.isEmpty()) {
            return new HashSet<>();
        }
        return recipeRepository.findRecipeByProducts(products);
    }

    public Recipe findRecipeById(int id) {
        return recipeRepository.findRecipeById(id).orElseThrow(() -> new IllegalStateException("Recipe with given id doesnt exist"));
    }

    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAllRecipes();
    }
}

