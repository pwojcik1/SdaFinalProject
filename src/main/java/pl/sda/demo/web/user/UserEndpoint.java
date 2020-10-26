package pl.sda.demo.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;

import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addToFridge(@RequestParam Integer id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        Product product = productService.getOne(id);
        userService.addProductToFridge(product, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeFromFridge(@RequestParam Integer id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.toString());
        userService.removeProductFromFridge(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Product> getAllFromFridge() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getAllProductsFromFridge(principal.toString());
    }
}