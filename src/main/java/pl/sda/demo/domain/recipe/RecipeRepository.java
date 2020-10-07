package pl.sda.demo.domain.recipe;

import pl.sda.demo.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeRepository {

    void addRecipeToDb(Recipe recipe);

    void updateRecipeInDb(Recipe recipe);

    void deleteRecipeFromDb(int id);

    Optional<Recipe> findByRecipeName(String name);

    Set<Recipe> findByProducts(List<Product> products);
}
