package pl.sda.demo.domain.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductServiceIntegrationTest {

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private ProductService productService;

    @Test
    void testShouldAddProductToLibrary() {
        //given
        Product product = new Product(null, "product");
        //when
        productService.addProductToLibrary(product);
        //then
        ProductEntity result = jpaProductRepository.getOne(1);
        assertEquals("product", result.getName());
        assertEquals(1, result.getId());
    }

    @Test
    void testShouldUpdateProductInLibrary() {
        //given
        Product product = new Product(1, "newProduct");
        ProductEntity productEntity = new ProductEntity(null, "product");
        jpaProductRepository.save(productEntity);
        //when
        productService.updateProductInLibrary(product);
        //then
        ProductEntity result = jpaProductRepository.getOne(1);
        assertEquals("newProduct", result.getName());
    }

    @Test
    void testShouldDeleteProductFromLibrary() {
        //given
        ProductEntity productEntity = new ProductEntity(1, "product");
        jpaProductRepository.save(productEntity);
        //when
        productService.deleteProductFromLibrary(1);
        //then
        Optional<ProductEntity> result = jpaProductRepository.findById(1);
        assertTrue(result.isEmpty());
    }
}