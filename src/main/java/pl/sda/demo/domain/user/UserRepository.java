package pl.sda.demo.domain.user;

import pl.sda.demo.domain.product.Product;

public interface UserRepository {
    void addProduct(Product product);

    void deleteProduct(Integer id);

    void updateProduct(Product product);

    void addToFavourites(Integer id);
}
