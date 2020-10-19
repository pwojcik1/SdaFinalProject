package pl.sda.demo.domain.product;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final ProductService productService = new ProductService(productRepository);
    private final ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

    @Test
    void testShouldAddProductToLibrary() {
        //given
        Product product = new Product(null, "Egg");
        Mockito.when(productRepository.getProductByName("Egg")).thenReturn(Optional.empty());
        //when
        productService.addProductToLibrary(product);
        //then
        Mockito.verify(productRepository).getProductByName("Egg");
        Mockito.verify(productRepository).addProductToLibrary(product);
    }

    @Test
    void testShouldThrowExceptionIfProductAlreadyExists() {
        //given
        Product product = new Product(null, "Egg");
        Mockito.when(productRepository.getProductByName("Egg")).thenReturn(Optional.of(product));
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> productService.addProductToLibrary(product));
        //then
        Mockito.verify(productRepository).getProductByName("Egg");
        assertEquals("Product with same name already exists", ex.getMessage());
        Mockito.verify(productRepository, Mockito.never()).addProductToLibrary(product);
    }

    @Test
    void testShouldUpdateProduct() {
        //given
        Product product = new Product(1, "Eggs");
        //when
        Mockito.when(productRepository.getProductByName(product.getName())).thenReturn(Optional.of(new Product(1, "Egg")));
        productService.updateProductInLibrary(product);

        Mockito.verify(productRepository).updateProductInLibrary(argumentCaptor.capture());
        Product result = argumentCaptor.getValue();
        //then
        Mockito.verify(productRepository).getProductByName("Eggs");
        assertEquals(1, result.getId());
        assertEquals("Eggs", result.getName());
    }

    @Test
    void testShouldThrowExceptionIfIdIsDifferent(){
        //given
        Product product = new Product(1, "Eggs");
        //when
        Mockito.when(productRepository.getProductByName(product.getName())).thenReturn(Optional.of(new Product(2, "Egg")));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->productService.updateProductInLibrary(product));
        //then
        Mockito.verify(productRepository).getProductByName("Eggs");
        assertEquals("Cannot update product with different id", ex.getMessage());
        Mockito.verify(productRepository, Mockito.never()).updateProductInLibrary(product);
    }

    @Test
    void testShouldReturnOneProduct(){
        //given
        int id = 1;
        //when
        Mockito.when(productRepository.getOne(id)).thenReturn(Optional.of(new Product(1,"name")));
        Product result = productService.getOne(id);
        //then
        Mockito.verify(productRepository).getOne(id);
        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
    }

    @Test
    void testShouldThrowExceptionIfProductDoesntExists(){
        //given
        int id = 1;
        //when
        Mockito.when(productRepository.getOne(id)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class, ()-> productService.getOne(id));
        //then
        Mockito.verify(productRepository).getOne(id);
        assertEquals("Product with given id doesnt exist", ex.getMessage());
    }
}