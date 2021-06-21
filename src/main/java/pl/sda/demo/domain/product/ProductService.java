package pl.sda.demo.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void addProductToLibrary(Product product) {
        productRepository.findProductByName(product.getName())
                .ifPresent((p -> {
                    throw new IllegalStateException("Product with same name already exists");
                }));
        productRepository.addProductToLibrary(product);
    }

    public void updateProductInLibrary(Product product) {
        productRepository.findProductById(product.getId())
                .filter(p -> !p.getId().equals(product.getId()))
                .ifPresent(product1 -> {
                    throw new IllegalStateException("Cannot update product with different id");
                });
        productRepository.updateProductInLibrary(product);
    }

    public void deleteProductFromLibrary(int id) {
        productRepository.deleteProductFromLibrary(id);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAllProducts();
    }

    public Product findProductById(int id) {
        return productRepository.findProductById(id).orElseThrow(() -> new IllegalStateException("Product with given id doesnt exist"));
    }

    public List<Product> findListOfProductsByIds(List<Integer> ids) {
        return productRepository.findListOfProductsByIds(ids);
    }
}
