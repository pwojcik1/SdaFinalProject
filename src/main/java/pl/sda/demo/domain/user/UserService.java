package pl.sda.demo.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

   public void createUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("Username already taken");
                });
        user.encodePassword(passwordEncoder, user.getPassword());
        userRepository.createUser(user);
    }

    public void updateUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(user1 -> {
                    throw new IllegalStateException("Cannot update user with different id");
                });
        user.encodePassword(passwordEncoder, user.getPassword());
        userRepository.updateUser(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    public void addProductToFridge(Product product, User user) {
        if (user.getProductId().contains(product.getId())) {
            throw new IllegalStateException("Product already in fridge");
        }
        userRepository.addProductToFridge(product, user);
    }

    public void removeProductFromFridge(int id, User user) {
        if (!user.getProductId().contains(id)) {
            throw new IllegalStateException("You dont have this product in your fridge");
        }
        userRepository.removeProductFromFridge(id, user);
    }

    public void addRecipeToFavourites(Recipe recipe, User user) {
        if (user.getRecipeId().contains(recipe.getId())) {
            throw new IllegalStateException("Recipe already in favourites");
        }
        userRepository.addRecipeToFavourites(recipe, user);
    }

    public void deleteRecipeFromFavourites(int id, User user) {
        if (!user.getRecipeId().contains(id)) {
            throw new IllegalStateException("Recipe is not in favourites");
        }
        userRepository.deleteRecipeFromFavourites(id, user);
    }
}
