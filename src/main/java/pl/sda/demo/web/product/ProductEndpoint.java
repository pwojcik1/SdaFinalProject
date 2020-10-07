package pl.sda.demo.web.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.external.product.DatabaseProductRepository;

import java.util.Optional;


@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductEndpoint {
    private final ProductService productService;
    private final DatabaseProductRepository databaseProductRepository;

    @PostMapping
    void addProductToLibrary(@RequestBody Product product) {
        productService.addProductToLibrary(product);
    }

    @PutMapping
    void updateProductInLibrary(@RequestBody Product product) {
        productService.updateProductInLibrary(product);
    }

    @DeleteMapping
    void deleteProductFromLibrary(@RequestParam int id) {
        productService.deleteProductFromLibrary(id);
    }

    @GetMapping(("/{name}"))
    Optional<Product> getProductByName(@RequestParam String name){
        return databaseProductRepository.getProductByName(name);
    }
}



