package pl.sda.demo.domain.product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    void addProductToLibrary(Product product);

    void updateProductInLibrary(Product product);

    void deleteProductFromLibrary(int id);

    Optional<Product> getProductByName(String name);

    List<Product> getAllProducts();

    Optional<Product> getOne(int id);

    List<Product> getAllProductsByIds(List<Integer> ids);
}
