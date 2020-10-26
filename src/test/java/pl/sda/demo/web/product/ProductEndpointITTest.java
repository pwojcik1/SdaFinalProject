package pl.sda.demo.web.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.user.JpaUserRepository;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductEndpointITTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldCreateProduct() {

        //given
        Product product = new Product(null, "name");
        HttpEntity<Product> entity = new HttpEntity<>(product);
        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/product", HttpMethod.POST, entity, Void.class);
        //then
        assertEquals(201, response.getStatusCodeValue());
        List<ProductEntity> all = jpaProductRepository.findAll();
        assertEquals(1,all.size());
        ProductEntity firstProduct = all.get(0);
        assertEquals(1, firstProduct.getId());
        assertEquals("name", firstProduct.getName());
    }

}