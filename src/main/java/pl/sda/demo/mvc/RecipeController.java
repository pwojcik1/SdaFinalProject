package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.dto.MapService;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/recipe")
@RequiredArgsConstructor
@Transactional
public class RecipeController {

    private final RecipeService recipeService;
    private final ProductService productService;
    private final MapService mapService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView allRecipesPage() {
        ModelAndView mav = new ModelAndView("recipes.html");
        mav.addObject("recipes", mapService.getAllRecipesWithProducts());
        return mav;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addRecipePage() {
        ModelAndView mav = new ModelAndView("addRecipe.html");
        mav.addObject("recipe", new Recipe());
        mav.addObject("products", productService.getAllProducts());
        return mav;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    String addNewDoctor(@ModelAttribute Recipe recipe) {
        recipeService.addRecipeToDb(recipe);
        return "redirect:/recipe";
    }



    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteRecipe(@RequestParam Integer id) {
        recipeService.deleteRecipeFromDb(id);
        return "redirect:/recipe";
    }


}
