package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;

import javax.validation.Valid;


@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView allProductsPage() {
        ModelAndView mav = new ModelAndView("products.html");
        mav.addObject("products", productService.getAllProducts());
        return mav;
    }

    @GetMapping("/addOrUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addProduct(@RequestParam(name = "id", required = false) Integer id) {
        ModelAndView mav = new ModelAndView("addProduct.html");
        if (id != null) {
            mav.addObject("product", productService.getOne(id));
        } else {
            mav.addObject("patient", new Product());
        }
        return mav;
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteProduct(@RequestParam Integer id) {
        productService.deleteProductFromLibrary(id);
        return "redirect:/product";
    }

    @PostMapping("/addOrUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    String addOrUpdateProduct(@ModelAttribute @Valid Product product) {
        if (product.getId() == null) {
            productService.addProductToLibrary(product);
        } else {
            productService.updateProductInLibrary(product);
        }
        return "redirect:/product";
    }
}
