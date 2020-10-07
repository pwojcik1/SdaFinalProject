package pl.sda.demo.external.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseProductRepository implements ProductRepository {
    private final JpaProductRepository jpaProductRepository;

    @Override
    public void addProductToLibrary(Product product) {
        ProductEntity productEntity = ProductEntity.builder()
                .name(product.getName())
                .build();
        jpaProductRepository.save(productEntity);
    }

    @Override
    public void updateProductInLibrary(Product product) {
        jpaProductRepository.findProductById(product.getId())
                .ifPresent(productEntity -> {
                    productEntity.updateFromDomain(product);
                    jpaProductRepository.save(productEntity);
                });
    }
    @Override
    public void deleteProductFromLibrary(int id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public Optional<Product> getProductByName(String name) {
        return jpaProductRepository.getProductByName(name)
                .map(productEntity -> Product.builder()
                        .id(productEntity.getId())
                        .name(productEntity.getName())
                        .build());
    }
}
