package pl.sda.demo.external.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaProductRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    void testShouldFindProductsByIdsInList() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("kurczak").build();

        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(productEntity3);
        testEntityManager.persist(productEntity4);

        //when
        Set<ProductEntity> result = jpaProductRepository.findAllProductsByIdInList(ids);
        //then
        assertEquals(ids.size(), result.size());
        assertTrue(result.contains(productEntity1));
        assertTrue(result.contains(productEntity2));
        assertTrue(result.contains(productEntity3));
        assertFalse(result.contains(productEntity4));
    }

    @Test
    void testShouldFindProductById() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();
        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        //when
        Optional<ProductEntity> result = jpaProductRepository.findProductById(2);
        //then
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId());
        assertEquals("chleb", result.get().getName());

    }

    @Test
    void testShouldFindAllProductsIdFromCollection() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("kurczak").build();

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(productEntity3);
        testEntityManager.persist(productEntity4);

        Set<ProductEntity> products = new HashSet<>();
        products.add(productEntity2);
        products.add(productEntity4);

        //when
        List<Integer> result = jpaProductRepository.findAllProductsIdFromCollection(products);
        //then
        assertEquals(2,result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(4));
    }

    @Test
    void testShouldGetProductByName() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();
        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        //when
        Optional<ProductEntity> result = jpaProductRepository.getProductByName("egg");
        //then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("egg", result.get().getName());
    }
}
