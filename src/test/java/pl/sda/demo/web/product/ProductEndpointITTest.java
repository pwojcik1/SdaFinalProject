package pl.sda.demo.web.product;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ProductEndpointITTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    void testShouldCreateProduct() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct");
        jpaProductRepository.save(productEntity);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("NewProduct");
        assertTrue(newProduct.isPresent());
        assertEquals(2, newProduct.get().getId());
        assertEquals("NewProduct", newProduct.get().getName());
    }

    @Test
    @WithMockUser()
    void testShouldNotCreateProductIfUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("NewProduct");
        assertTrue(newProduct.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldDeleteProduct() throws Exception {

        ProductEntity productEntity = new ProductEntity(null, "testProduct");
        jpaProductRepository.save(productEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("testProduct");
        assertTrue(newProduct.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldNotDeleteProductIfUserIsNotAdmin() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct");
        jpaProductRepository.save(productEntity);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("testProduct");
        assertTrue(newProduct.isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldUpdateProduct() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct");
        jpaProductRepository.save(productEntity);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
        Optional<ProductEntity> product = jpaProductRepository.getProductByName("NewProductName");
        assertTrue(product.isPresent());
        assertEquals(1, product.get().getId());
        assertEquals("NewProductName", product.get().getName());
    }

    @Test
    @WithMockUser()
    void testShouldNotUpdateProductIfUserIsNotAdmin() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct");
        jpaProductRepository.save(productEntity);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<ProductEntity> product = jpaProductRepository.getProductByName("NewProductName");
        assertTrue(product.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldReturnAllProducts() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct1");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(null, "testProduct2");
        jpaProductRepository.save(productEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("testProduct1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("testProduct2"));
    }

    @Test
    @WithMockUser()
    void testShouldReturnProductById() throws Exception {
        ProductEntity productEntity = new ProductEntity(null, "testProduct1");
        jpaProductRepository.save(productEntity);
        ProductEntity productEntity2 = new ProductEntity(null, "testProduct2");
        jpaProductRepository.save(productEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testProduct2"));
    }
}