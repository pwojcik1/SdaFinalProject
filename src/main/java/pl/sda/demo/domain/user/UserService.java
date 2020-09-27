package pl.sda.demo.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.demo.domain.product.Product;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void addProductToFridge(Product product) {
        userRepository.addProductToFridge(product);
    }

    public void removeProductFromFridge(Integer id) {
        if (!userRepository.isProductInFridge(id)) {
            userRepository.removeProductFromFridge(id);
        }
        throw new IllegalArgumentException("You dont have this product in fridge");
    }

    public void addRecipeToFavourites(Integer id) {
        if (userRepository.isAlreadyFavourite(id)) {
            throw new IllegalStateException("Already favourite");
        }
        userRepository.addRecipeToFavourites(id);
    }
}
