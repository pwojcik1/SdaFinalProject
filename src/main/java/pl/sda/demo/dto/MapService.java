package pl.sda.demo.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final   RecipeService recipeService;

    private final ProductService productService;

    public List<RecipeProductDTO> getAllRecipesWithProducts() {
        return recipeService.getAllRecipes().stream().map(this::convertToRecipeProductDTO).collect(Collectors.toList());
    }

    private RecipeProductDTO convertToRecipeProductDTO(Recipe recipe) {
        RecipeProductDTO recipeProductDTO = new RecipeProductDTO();
        recipeProductDTO.setRecipeId(recipe.getId());
        recipeProductDTO.setRecipeName(recipe.getName());
        recipeProductDTO.setRecipeDescription(recipe.getDescription());
        List<String> products = productService.findAllProductsByIds(recipe.getProductId()).stream().map(Product::getName).collect(Collectors.toList());
        recipeProductDTO.setProductNames(products);
        return recipeProductDTO;
    }
}
