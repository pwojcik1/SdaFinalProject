package pl.sda.demo.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void addProductToLibrary(Product product);

    void updateProductInLibrary(Product product);

    void deleteProductFromLibrary(int id);

    Optional<Product> findProductByName(String name);

    List<Product> findAllProducts();

    Optional<Product> findProductById(int id);

    List<Product> findListOfProductsByIds(List<Integer> ids);
}
