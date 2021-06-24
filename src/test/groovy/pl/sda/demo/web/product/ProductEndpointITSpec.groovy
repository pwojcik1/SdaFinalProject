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
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": null,\n" +
                        "    \"name\": \"NewProduct\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())

        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("NewProduct")
        newProduct.isPresent()
        newProduct.get().getId() == 10
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

        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("NewProduct")
        newProduct.isEmpty()
    }

    @WithMockUser(roles = "ADMIN")
    def "test should delete product"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204))

        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("Product1")
        newProduct.isEmpty()
    }

    @WithMockUser()
    def "test should not delete product if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<ProductEntity> newProduct = jpaProductRepository.findProductByName("Product1")
        newProduct.isPresent()
    }

    @WithMockUser(roles = "ADMIN")
    def "test should update product"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))

        Optional<ProductEntity> product = jpaProductRepository.findProductByName("NewProductName")
        product.isPresent()
        product.get().getId() == 1
        product.get().getName() == "NewProductName"
    }

    @WithMockUser()
    def "test should not update product if user is not admin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"NewProductName\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))

        Optional<ProductEntity> product = jpaProductRepository.findProductByName("NewProductName")
        product.isEmpty()
    }

    @WithMockUser()
    def "test should return all products"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size()').value(9))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].id').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[0].name').value("Product1"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].id').value(2))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[1].name').value("Product2"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].id').value(3))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[2].name').value("Product3"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].id').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[3].name').value("Product4"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[4].id').value(5))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[4].name').value("Product5"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[5].id').value(6))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[5].name').value("Product6"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[6].id').value(7))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[6].name').value("Product7"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[7].id').value(8))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[7].name').value("Product8"))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[8].id').value(9))
                .andExpect(MockMvcResultMatchers.jsonPath('$.[8].name').value("Product9"))
    }

    @WithMockUser()
    def "test should return product by id"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", "4")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.id').value(4))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name').value("Product4"))
    }
}
