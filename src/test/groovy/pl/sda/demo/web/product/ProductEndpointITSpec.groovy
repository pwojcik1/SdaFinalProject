package pl.sda.demo.web.product

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
import spock.lang.Specification

import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ProductEndpointITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JpaProductRepository jpaProductRepository

    @WithMockUser(roles = "ADMIN")
    def "test should add new product"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())

        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("NewProduct")
        newProduct.isPresent()
        newProduct.get().getId() == 2
        newProduct.get().getName() == "NewProduct"
    }

    def "test should not create product if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("NewProduct")
        newProduct.isEmpty()
    }


    @WithMockUser(roles = "ADMIN")
    def "test should delete product"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204))

        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("testProduct")
        newProduct.isEmpty()
    }

    @WithMockUser()
    def "test should not delete product if user is not admin"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<ProductEntity> newProduct = jpaProductRepository.getProductByName("testProduct")
        newProduct.isPresent()
    }

    @WithMockUser(roles = "ADMIN")
    def "test should update product"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))

        Optional<ProductEntity> product = jpaProductRepository.getProductByName("NewProductName")
        product.isPresent()
        product.get().getId() == 1
        product.get().getName() == "NewProductName"
    }

    @WithMockUser()
    def "test should not update product if user is not admin"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct")
        jpaProductRepository.save(productEntity)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<ProductEntity> product = jpaProductRepository.getProductByName("NewProductName")
        product.isEmpty()
    }

    @WithMockUser()
    def "test should return all products"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct1")
        jpaProductRepository.save(productEntity)

        ProductEntity productEntity2 = new ProductEntity(null, "testProduct2")
        jpaProductRepository.save(productEntity2)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].id').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("testProduct1"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].id').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].name').value("testProduct2"))
    }

    @WithMockUser()
    def "test should return product by id"() {
        given:
        ProductEntity productEntity = new ProductEntity(null, "testProduct1")
        jpaProductRepository.save(productEntity)

        ProductEntity productEntity2 = new ProductEntity(null, "testProduct2")
        jpaProductRepository.save(productEntity2)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.id').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name').value("testProduct2"))
    }
}
