package pl.sda.demo.domain.recipe;

import pl.sda.demo.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeRepository {

    void addRecipeToDb(Recipe recipe);

    void updateRecipeInDb(Recipe recipe);

    void deleteRecipeFromDb(int id);

    Optional<Recipe> findRecipeByName(String name);

    Optional<Recipe> findRecipeById(int id);

    Set<Recipe> findRecipeByProducts(List<Product> products);

    List<Recipe> findAllRecipes();
}
