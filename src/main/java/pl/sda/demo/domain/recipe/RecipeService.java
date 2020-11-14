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
        recipeRepository.findByRecipeName(recipe.getName())
                .ifPresent(r -> {
                    throw new IllegalStateException("Recipe with same name already exists");
                });
        recipeRepository.addRecipeToDb(recipe);
    }

    public void updateRecipeInDb(Recipe recipe) {
        recipeRepository.findByRecipeId(recipe.getId())
                .filter(r -> !r.getId().equals(recipe.getId()))
                .ifPresent(recipe1 -> {
                    throw new IllegalStateException("Cannot update product with different id");
                });
        recipeRepository.updateRecipeInDb(recipe);
    }

    public void deleteRecipeFromDb(int id) {
        recipeRepository.deleteRecipeFromDb(id);
    }

    public Recipe findByRecipeName(String name) {
        return recipeRepository.findByRecipeName(name).orElseThrow(() -> new IllegalStateException("recipe with given name doesnt exist"));
    }

    public Set<Recipe> findByProducts(List<Product> products) {
        if(products.isEmpty()){
            return new HashSet<>();
        }
        return recipeRepository.findByProducts(products);
    }

    public Recipe getOne(int id){
        return recipeRepository.findByRecipeId(id).orElseThrow(() -> new IllegalStateException("Recipe with given id doesnt exist"));
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.getAllRecipes();
    }
}

