package pl.sda.demo.external.product;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.sda.demo.domain.product.Product;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseProductRepositoryTest {
    private final JpaProductRepository jpaProductRepository = Mockito.mock(JpaProductRepository.class);
    private final DatabaseProductRepository databaseProductRepository = new DatabaseProductRepository(jpaProductRepository);
    private final ArgumentCaptor<ProductEntity> argumentCaptor = ArgumentCaptor.forClass(ProductEntity.class);

    @Test
    void testShouldPersistNewProduct() {
        //given
        Product product = Product.builder()
                .name("Egg")
                .build();
        //when
        databaseProductRepository.addProductToLibrary(product);
        Mockito.verify(jpaProductRepository).save(argumentCaptor.capture());
        ProductEntity result = argumentCaptor.getValue();
        //then
        assertEquals("Egg", result.getName());
    }

    @Test
    void testShouldUpdateProduct() {
        //given
        Product product = new Product(1,"Milk");
        ProductEntity productEntity = new ProductEntity(1,"Egg");
        //when
        Mockito.when(jpaProductRepository.findById(product.getId())).thenReturn(Optional.of(productEntity));
        databaseProductRepository.updateProductInLibrary(product);
        Mockito.verify(jpaProductRepository).save(argumentCaptor.capture());
        ProductEntity result = argumentCaptor.getValue();
        //then
        Mockito.verify(jpaProductRepository).findById(product.getId());
        assertEquals("Milk", result.getName());
    }

    @Test
    void testShouldReturnProductByName(){
        //given
        String name = "Egg";
        ProductEntity productEntity = new ProductEntity(1,"Egg");
        //when
        Mockito.when(jpaProductRepository.findProductByName(name)).thenReturn(Optional.of(productEntity));
        Optional<Product> result = databaseProductRepository.findProductByName(name);
        //then
        assertTrue(result.isPresent());
        assertEquals("Egg", result.get().getName());
        assertEquals(1, result.get().getId());
        assertEquals(Product.class, result.get().getClass());
    }

}
