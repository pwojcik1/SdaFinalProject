package pl.sda.demo.web.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    void createProduct(@RequestBody Product product){
        productService.addProductToLibrary(product);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProduct(@RequestParam Integer id){
        productService.deleteProductFromLibrary(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    void updateProduct(@RequestBody Product product){
        productService.updateProductInLibrary(product);
    }

    @GetMapping
    List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    Product getById(@PathVariable Integer id){
        return productService.getOne(id);
    }
}



