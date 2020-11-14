package pl.sda.demo.external.product

import pl.sda.demo.domain.product.Product
import spock.lang.Specification

class DatabaseProductRepositorySpec extends Specification {
    def jpaProductRepository = Mock(JpaProductRepository)
    DatabaseProductRepository databaseProductRepository = new DatabaseProductRepository(jpaProductRepository)

    def "should persist new product"() {
        given:
        Product product = new Product(null, "productName")
        when:
        databaseProductRepository.addProductToLibrary(product)
        then:
        1 * jpaProductRepository.save({ it.id == null && it.name == "productName" })
    }

    def "should update product"() {
        given:
        Product product = new Product(1, "newProductName")
        when:
        databaseProductRepository.updateProductInLibrary(product)
        then:
        1 * jpaProductRepository.findProductById(product.getId()) >> Optional.of(new ProductEntity(1, "oldName"))
        1 * jpaProductRepository.save({ it.id == 1 && it.name == "newProductName" })
    }
}
