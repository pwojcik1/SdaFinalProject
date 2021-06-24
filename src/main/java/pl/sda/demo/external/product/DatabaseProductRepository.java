package pl.sda.demo.external.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        jpaProductRepository.findById(product.getId())
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
    public Optional<Product> findProductByName(String name) {
        return jpaProductRepository.findProductByName(name)
                .map(entityToProduct());
    }

    @Override
    public List<Product> findAllProducts() {
        return jpaProductRepository.findAll()
                .stream()
                .map(entityToProduct())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findProductById(int id) {
        return jpaProductRepository.findById(id)
                .map(entityToProduct());
    }

    @Override
    public List<Product> findListOfProductsByIds(List<Integer> ids) {
        return jpaProductRepository.findAllProductsByIdInList(ids)
                .stream()
                .map(entityToProduct())
                .collect(Collectors.toList());
    }

    private Function<ProductEntity, Product> entityToProduct() {
        return productEntity -> Product.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .build();
    }
}
