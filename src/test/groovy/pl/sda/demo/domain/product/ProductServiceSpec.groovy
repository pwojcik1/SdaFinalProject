package pl.sda.demo.domain.product

import spock.lang.Specification

class ProductServiceSpec extends Specification {

    def productRepository = Mock(ProductRepository)
    ProductService productService = new ProductService(productRepository)

    def "should add product to db"() {
        given: "new product"
        Product product = new Product(null, "productName")

        when: "service adds product"
        productService.addProductToLibrary(product)

        then: "repository is called with expected parameters"
        1 * productRepository.getProductByName("productName") >> Optional.empty()
        1 * productRepository.addProductToLibrary(product)
    }

    def "should throw exception if product is already in db"() {
        given: "new product"
        Product product = new Product(null, "productName")

        when: "service tries to add product"
        productService.addProductToLibrary(product)

        then: "exception is thrown"
        1 * productRepository.getProductByName("productName") >> Optional.of(new Product(1, "productName"))

        def e = thrown(IllegalStateException)
        e.message == "Product with same name already exists"

        0 * productRepository.addProductToLibrary(product)
    }

    def "should update product in db"() {
        given: "existing product"
        Product product = new Product(1, "newProductName")

        when: "service updates product"
        productService.updateProductInLibrary(product)

        then: "repository is called with expected parameters"
        1 * productRepository.getOne(1) >> Optional.of(new Product(1, "productName"))
        1 * productRepository.updateProductInLibrary(product)
    }

    def "should throw exception when id is different"() {
        given: "existing product"
        Product product = new Product(1, "newProductName")

        when: "service tries to update product"
        productService.updateProductInLibrary(product)

        then: "exception is thrown"
        1 * productRepository.getOne(1) >> Optional.of(new Product(2, "productName"))

        def e = thrown(IllegalStateException)
        e.message == "Cannot update product with different id"

        0 * productRepository.updateProductInLibrary(product)
    }

    def "should return one product by id"() {
        when: "service tries to get product"
        def result = productService.getOne(2)

        then: "correct product is returned"
        1 * productRepository.getOne(2) >> Optional.of(new Product(2, "testName"))

        result.id == 2
        result.name == "testName"
    }

    def "should throw exception when product with given id doesnt exists"(){
        when: "service tries to return product"
        productService.getOne(2)

        then: "exception is thrown"
        1 * productRepository.getOne(2) >> Optional.empty()

        def e = thrown(IllegalStateException)
        e.message == "Product with given id doesnt exist"
    }
}
