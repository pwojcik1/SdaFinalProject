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
    void testShouldCreateProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("NewProduct");
        assertTrue(newProduct.isPresent());
        assertEquals(10, newProduct.get().getId());
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
        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("NewProduct");
        assertTrue(newProduct.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldDeleteProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204));

        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("Product2");
        assertTrue(newProduct.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldNotDeleteProductIfUserIsNotAdmin() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("Product1");
        assertTrue(newProduct.isPresent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShouldUpdateProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
        Optional<ProductEntity> product = jpaProductRepository.findProductByName("NewProductName");
        assertTrue(product.isPresent());
        assertEquals(1, product.get().getId());
        assertEquals("NewProductName", product.get().getName());
    }

    @Test
    @WithMockUser()
    void testShouldNotUpdateProductIfUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403));
        Optional<ProductEntity> product = jpaProductRepository.findProductByName("NewProductName");
        assertTrue(product.isEmpty());
    }

    @Test
    @WithMockUser()
    void testShouldReturnAllProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Product1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Product2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].name").value("Product3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].name").value("Product4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[4].id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[4].name").value("Product5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[5].id").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[5].name").value("Product6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[6].id").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[6].name").value("Product7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[7].id").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[7].name").value("Product8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[8].id").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[8].name").value("Product9"));
    }

    @Test
    @WithMockUser()
    void testShouldReturnProductById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", "7")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Product7"));
    }
}