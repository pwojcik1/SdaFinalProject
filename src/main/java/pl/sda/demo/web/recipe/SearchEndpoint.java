package pl.sda.demo.web.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    Set<Recipe> searchByProducts() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        List<Product> allProductsByIds = productService.findListOfProductsByIds(user.getProductId());
        return recipeService.findRecipeByProducts(allProductsByIds);
    }
}
