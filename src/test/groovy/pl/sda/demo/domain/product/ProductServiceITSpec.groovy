package pl.sda.demo.domain.product

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.sda.demo.external.product.JpaProductRepository
import pl.sda.demo.external.product.ProductEntity
import spock.lang.Specification

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductServiceITSpec extends Specification {
    @Autowired
    private JpaProductRepository jpaProductRepository

    @Autowired
    private ProductService productService

    def "should add product to db"(){
        given:
        Product product = new Product(null, "productName")
        when:
        productService.addProductToLibrary(product)
        ProductEntity result = jpaProductRepository.getOne(10)
        then:
        result.id == 10
        result.name == "productName"
    }
    def "should throw exception if product already exists"(){
        given:
        Product product = new Product(null, "Product1")
        when:
        productService.addProductToLibrary(product)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product with same name already exists"

    }
    def "should update product in db"(){
        given:
        Product product = new Product(1, "newProductName")
        when:
        productService.updateProductInLibrary(product)
        def result = jpaProductRepository.getOne(1)
        then:
        result.name == "newProductName"
        result.id == 1
    }
    def "should delete product from db"(){
        given:
        when:
        productService.deleteProductFromLibrary(1)
        Optional<ProductEntity> result = jpaProductRepository.findById(1)
        then:
        result.isEmpty()
    }

    def "should return all products"(){
        given:

        when:
        List<Product> result = productService.findAllProducts()
        then:
        result.size() == 9
        result.contains(new Product(1,"Product1"))
        result.contains(new Product(2,"Product2"))
        result.contains(new Product(3,"Product3"))
        result.contains(new Product(4,"Product4"))
        result.contains(new Product(5,"Product5"))
        result.contains(new Product(6,"Product6"))
        result.contains(new Product(7,"Product7"))
        result.contains(new Product(8,"Product8"))
        result.contains(new Product(9,"Product9"))
    }
    def "should return product by id"(){
        given:
        when:
        Product result = productService.findProductById(2)
        then:
        result.id == 2
        result.name == "Product2"
    }

    def "should throw exception if product with given id doesnt exist"(){
        when:
        Product result = productService.findProductById(11)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product with given id doesnt exist"
    }
    def "should return all products by ids"(){
        List<Integer> ids = new ArrayList<>()
        ids << 1
        ids << 3
        when:
        List<Product> result = productService.findListOfProductsByIds(ids)
        then:
        result.size() == 2
        result.contains(new Product(1, "Product1"))
        result.contains(new Product(3, "Product3"))
    }
}
