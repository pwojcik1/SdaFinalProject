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


    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteProduct(@RequestParam Integer id) {
        productService.deleteProductFromLibrary(id);
        return "redirect:/product";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addDoctorPage() {
        ModelAndView mav = new ModelAndView("addProduct.html");
        mav.addObject("product", new Product());
        return mav;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    String addNewDoctor(@ModelAttribute Product product) {
        productService.addProductToLibrary(product);
        return "redirect:/product";
    }

    @GetMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addProduct(@RequestParam(name = "id") Integer id) {
        ModelAndView mav = new ModelAndView("updateProduct.html");
        mav.addObject("product", productService.getOne(id));
        return mav;
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    String addOrUpdateProduct(@ModelAttribute Product product) {
        productService.updateProductInLibrary(product);
        return "redirect:/product";
    }
}
