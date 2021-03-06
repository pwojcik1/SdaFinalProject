package pl.sda.demo.domain.user;

import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void addProductToFridge(Product product, User user);

    void removeProductFromFridge(int id, User user);

    void addRecipeToFavourites(Recipe recipe, User user);

    void deleteRecipeFromFavourites(int id, User user);

    Optional<User> findByUsername(String username);

    List<Product> findAllProductsFromUserFridge(String username);

    List<Recipe> findAllUserFavourites(String username);
}
