package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
public class SearchEndpoint {

    private final UserService userService;
    private final RecipeService recipeService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Set<Recipe> searchByProducts(@RequestParam String username) {
        User user = userService.findByUsername(username);
        List<Product> allProductsByIds = productService.findAllProductsByIds(user.getProductId());
        return recipeService.findByProducts(allProductsByIds);
    }
}
