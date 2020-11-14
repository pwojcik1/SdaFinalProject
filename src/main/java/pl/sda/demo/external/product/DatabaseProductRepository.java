package pl.sda.demo.external.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductRepository;

import java.util.List;
import java.util.Optional;
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

    @Override
    public List<Product> getAllProducts() {
        return jpaProductRepository.findAll()
                .stream()
                .map(ent -> new Product(ent.getId(), ent.getName())).collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getOne(int id) {
        return jpaProductRepository.findById(id)
                .map(ent -> new Product(ent.getId(), ent.getName()));
    }

    @Override
    public List<Product> getAllProductsByIds(List<Integer> ids) {
        return jpaProductRepository.findAllProductsByIdInList(ids).stream().map(productEntity -> Product.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .build()).collect(Collectors.toList());
    }
}
