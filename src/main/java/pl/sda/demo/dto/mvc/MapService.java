package pl.sda.demo.dto.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RecipeService recipeService;
    private final ProductService productService;
    private final UserService userService;

    public List<RecipeProductDTO> getAllRecipesWithProducts() {
        return recipeService.getAllRecipes().stream().map(this::convertToRecipeProductDTO).collect(Collectors.toList());
    }

    public List<RecipeProductDTO> getAllFavouritesWithProducts(String username){
        return userService.getAllFavourites(username).stream().map(this::convertToRecipeProductDTO).collect(Collectors.toList());
    }

    public List<RecipeProductDTO> findByProducts(List<Integer> ids){
        List<Product> products = productService.findAllProductsByIds(ids);
        return recipeService.findByProducts(products).stream().map(this::convertToRecipeProductDTO).collect(Collectors.toList());
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
