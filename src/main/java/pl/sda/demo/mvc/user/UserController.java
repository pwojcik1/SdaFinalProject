package pl.sda.demo.mvc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.user.UserService;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView userPage() {
        ModelAndView mav = new ModelAndView("user.html");
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        mav.addObject("fridge", userService.getAllProductsFromFridge(username));
        return mav;
    }

    @GetMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    String deleteProductFromFridge(@RequestParam Integer id) {
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        pl.sda.demo.domain.user.User domainUser = userService.findByUsername(username);
        userService.removeProductFromFridge(id, domainUser);
        return "redirect:/user";
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    ModelAndView addToFridgePage() {
        ModelAndView mav = new ModelAndView("addProductToFridge.html");
        mav.addObject("prod", new Product());
        mav.addObject("products", productService.getAllProducts());
        return mav;
    }
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    String addToFridge(@ModelAttribute Product prod) {
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        pl.sda.demo.domain.user.User domainUser = userService.findByUsername(username);
        Product product = productService.getOne(prod.getId());
        userService.addProductToFridge(product,domainUser);
        return "redirect:/user";
    }
}
