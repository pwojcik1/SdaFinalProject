package pl.sda.demo.domain.user;

import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

import java.util.Optional;

public interface UserRepository {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void addProductToFridge(Product product, User user);

    void removeProductFromFridge(int id, User user);

//    void updateProductInFridge(Product product, User user);

    void addRecipeToFavourites(Recipe recipe, User user);

    void deleteRecipeFromFavourites(int id, User user);

    Optional<User> findByUsername(String username);

    Optional<Product> getProductFromFridgeByName(String name, User user);
}