package pl.sda.demo.web.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    void createProduct(@RequestBody Product product) {
        productService.addProductToLibrary(product);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteProduct(@RequestParam Integer id) {
        productService.deleteProductFromLibrary(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    void updateProduct(@RequestBody Product product) {
        productService.updateProductInLibrary(product);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Product> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Product getById(@PathVariable Integer id) {
        return productService.findProductById(id);
    }
}