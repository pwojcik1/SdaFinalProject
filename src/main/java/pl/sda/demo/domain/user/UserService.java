package pl.sda.demo.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    void createUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("Username already taken");
                });
        userRepository.createUser(user);
    }

    void updateUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(user1 -> {
                    throw new IllegalStateException("Cannot update user with different id");
                });
        userRepository.updateUser(user);
    }

    void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    void addProductToFridge(Product product, User user) {
        if (user.getProductId().contains(product.getId())) {
            throw new IllegalStateException("Product already in fridge");
        }
        userRepository.addProductToFridge(product, user);
    }

    void removeProductFromFridge(int id, User user) {
        if (!user.getProductId().contains(id)) {
            throw new IllegalStateException("You dont have this product in your fridge");
        }
        userRepository.removeProductFromFridge(id, user);
    }

//    void updateProductInFridge(Product product, User user) {
//        userRepository.getProductFromFridgeByName(product.getName(), user)
//                .filter(p -> !p.getId().equals(product.getId()))
//                .ifPresent(product1 -> {
//                    throw new IllegalStateException("Cannot update product with different id");
//                });
//        userRepository.updateProductInFridge(product, user);
//    }

    void addRecipeToFavourites(Recipe recipe, User user) {
        if (user.getRecipeId().contains(recipe.getId())) {
            throw new IllegalStateException("Recipe already in favourites");
        }
        userRepository.addRecipeToFavourites(recipe, user);
    }

    void deleteRecipeFromFavourites(int id, User user) {
        if (!user.getRecipeId().contains(id)) {
            throw new IllegalStateException("Recipe already in favourites");
        }
        userRepository.deleteRecipeFromFavourites(id, user);
    }
}
