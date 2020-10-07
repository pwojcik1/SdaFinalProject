package pl.sda.demo.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void addProductToLibrary(Product product) {
        productRepository.getProductByName(product.getName())
                .ifPresent((p -> {
                    throw new IllegalStateException("Product with same name already exists");
                }));
        productRepository.addProductToLibrary(product);
    }

    public void updateProductInLibrary(Product product) {
        productRepository.getProductByName(product.getName())
                .filter(p -> !p.getId().equals(product.getId()))
                .ifPresent(product1 -> {
                    throw new IllegalStateException("Cannot update product with different id");
                });
        productRepository.updateProductInLibrary(product);
    }

    public void deleteProductFromLibrary(int id) {
        productRepository.deleteProductFromLibrary(id);
    }
}
