package pl.sda.demo.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.api.ProductUserDTO;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addToFridge(@RequestBody ProductUserDTO productUserDTO){
        User user = userService.findByUsername(productUserDTO.getUsername());
        Product product = productService.getOne(productUserDTO.getProductId());
        userService.addProductToFridge(product,user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeFromFridge(@RequestBody ProductUserDTO productUserDTO){
        User user = userService.findByUsername(productUserDTO.getUsername());
        userService.removeProductFromFridge(productUserDTO.getProductId(),user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Product> getAllFromFridge(@RequestParam String username){
        return userService.getAllProductsFromFridge(username);
    }
}