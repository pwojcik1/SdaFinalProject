package pl.sda.demo.external.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaProductRepositoryIntegrationTest {

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    void testShouldFindProductsByIdsInList() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        //when
        Set<ProductEntity> result = jpaProductRepository.findAllProductsByIdInList(ids);
        //then
        assertEquals(ids.size(), result.size());
        assertTrue(result.contains(jpaProductRepository.getOne(1)));
        assertTrue(result.contains(jpaProductRepository.getOne(2)));
        assertTrue(result.contains(jpaProductRepository.getOne(3)));
    }

    @Test
    void testShouldFindAllProductsIdFromCollection() {
        //given
        Set<ProductEntity> products = new HashSet<>();
        products.add(jpaProductRepository.getOne(1));
        products.add(jpaProductRepository.getOne(3));
        //when
        List<Integer> result = jpaProductRepository.findAllProductsIdFromCollection(products);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(3));
    }

    @Test
    void testShouldGetProductByName() {
        //given
        //when
        Optional<ProductEntity> result = jpaProductRepository.findProductByName("Product5");
        //then
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getId());
        assertEquals("Product5", result.get().getName());
    }

    @Test
    void testShouldFindAllProductsFromUserFridge() {
        //given
        //when
        List<ProductEntity> result = jpaProductRepository.findAllProductsFromUserFridge("user1");
        //then
        assertEquals(7, result.size());
        assertTrue(result.contains(jpaProductRepository.getOne(1)));
        assertTrue(result.contains(jpaProductRepository.getOne(2)));
        assertTrue(result.contains(jpaProductRepository.getOne(3)));
        assertTrue(result.contains(jpaProductRepository.getOne(5)));
        assertTrue(result.contains(jpaProductRepository.getOne(6)));
        assertTrue(result.contains(jpaProductRepository.getOne(7)));
        assertTrue(result.contains(jpaProductRepository.getOne(8)));
    }
}
