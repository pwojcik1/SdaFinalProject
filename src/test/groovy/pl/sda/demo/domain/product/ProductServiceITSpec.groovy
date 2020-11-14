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
        ProductEntity result = jpaProductRepository.getOne(1);
        then:
        result.id == 1
        result.name == "productName"
    }
    def "should throw exception if product already exists"(){
        given:
        ProductEntity productEntity = new ProductEntity(null, "productName")
        jpaProductRepository.save(productEntity)
        Product product = new Product(null, "productName")
        when:
        productService.addProductToLibrary(product)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product with same name already exists"

    }
    def "should update product in db"(){
        given:
        ProductEntity productEntity = new ProductEntity(null, "oldProductName")
        Product product = new Product(1, "newProductName")
        jpaProductRepository.save(productEntity)
        when:
        productService.updateProductInLibrary(product)
        def result = jpaProductRepository.getOne(1)
        then:
        result.name == "newProductName"
        result.id == 1
    }
    def "should delete product from db"(){
        given:
        ProductEntity productEntity = new ProductEntity(null, "productName")
        jpaProductRepository.save(productEntity)
        Optional<ProductEntity> before = jpaProductRepository.findById(1)
        when:
        productService.deleteProductFromLibrary(1)
        Optional<ProductEntity> result = jpaProductRepository.findById(1)
        then:
        before.isPresent()
        before.get().id == 1
        before.get().name == "productName"
        result.isEmpty()
    }

    def "should return all products"(){
        given:
        ProductEntity productEntity1 = new ProductEntity(null, "productName1")
        ProductEntity productEntity2 = new ProductEntity(null, "productName2")
        jpaProductRepository.save(productEntity1)
        jpaProductRepository.save(productEntity2)
        when:
        List<Product> result = productService.getAllProducts()
        then:
        result.size() == 2
        result.contains(new Product(1,"productName1"))
        result.contains(new Product(2,"productName2"))
    }
    def "should return product by id"(){
        given:
        ProductEntity productEntity1 = new ProductEntity(null, "productName1")
        ProductEntity productEntity2 = new ProductEntity(null, "productName2")
        jpaProductRepository.save(productEntity1)
        jpaProductRepository.save(productEntity2)
        when:
        Product result = productService.getOne(2)
        then:
        result.id == 2
        result.name == "productName2"
    }

    def "should throw exception if product with given id doesnt exist"(){
        when:
        Product result = productService.getOne(1)
        then:
        def e = thrown(IllegalStateException)
        e.message == "Product with given id doesnt exist"
    }
    def "should return all products by ids"(){
        ProductEntity productEntity1 = new ProductEntity(null, "productName1")
        ProductEntity productEntity2 = new ProductEntity(null, "productName2")
        ProductEntity productEntity3 = new ProductEntity(null, "productName3")
        jpaProductRepository.save(productEntity1)
        jpaProductRepository.save(productEntity2)
        jpaProductRepository.save(productEntity3)
        List<Integer> ids = new ArrayList<>()
        ids << 1
        ids << 3
        when:
        List<Product> result = productService.findAllProductsByIds(ids)
        then:
        result.size() == 2
        result.contains(new Product(1, "productName1"))
        result.contains(new Product(3, "productName3"))
    }
}
