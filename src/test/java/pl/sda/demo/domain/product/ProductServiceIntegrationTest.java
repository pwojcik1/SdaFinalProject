package pl.sda.demo.domain.product;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import java.util.ArrayList;
import java.util.List;
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

    @Test
    void testShouldReturnAllProducts(){
        //given
        ProductEntity productEntity1 = new ProductEntity(1, "product1");
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        ProductEntity productEntity3 = new ProductEntity(3, "product3");
        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);
        Product product1 = new Product(1, "product1");
        Product product2 = new Product(2, "product2");
        Product product3 = new Product(3, "product3");
        //when
        List<Product> result = productService.getAllProducts();
        //then
        assertEquals(3, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        assertTrue(result.contains(product3));
    }

    @Test
    void testShouldReturnAllProductsByIds(){
        //given
        ProductEntity productEntity1 = new ProductEntity(1, "product1");
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        ProductEntity productEntity3 = new ProductEntity(3, "product3");
        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);
        Product product1 = new Product(1, "product1");
        Product product2 = new Product(2, "product2");
        Product product3 = new Product(3, "product3");
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(3);
        //when
        List<Product> result = productService.findAllProductsByIds(ids);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertFalse(result.contains(product2));
        assertTrue(result.contains(product3));
    }

    @Test
    void testShouldGetOneProductById(){
        //given
        ProductEntity productEntity1 = new ProductEntity(1, "product1");
        ProductEntity productEntity2 = new ProductEntity(2, "product2");
        ProductEntity productEntity3 = new ProductEntity(3, "product3");
        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);
        //when
        Product result = productService.getOne(2);
        //then
        assertEquals(2,result.getId());
        assertEquals("product2",result.getName());
    }

    @Test
    void testShouldThrowExceptionWhenIdIsIncorrect(){
        //given
        ProductEntity productEntity1 = new ProductEntity(1, "product1");
        jpaProductRepository.save(productEntity1);
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class,()-> productService.getOne(2));
        //then
        assertEquals("Product with given id doesnt exist",ex.getMessage());
    }
}