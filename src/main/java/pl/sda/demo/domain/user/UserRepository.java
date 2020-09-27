package pl.sda.demo.domain.user;

import pl.sda.demo.domain.product.Product;

public interface UserRepository {
    void addProductToFridge(Product product);

    void removeProductFromFridge(Integer id);

    void updateProduct(Product product);

    void addRecipeToFavourites(Integer id);

    boolean isProductInFridge(Integer id);

    boolean isAlreadyFavourite(Integer id);
}
