package pl.sda.demo.domain.product;

import java.util.Optional;

public interface ProductRepository {
    void addProductToLibrary(Product product);

    void updateProductInLibrary(Product product);

    void deleteProductFromLibrary(int id);

    Optional<Product> getProductByName(String name);
}
