package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeService;

import javax.validation.Valid;
@Controller
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final ProductService productService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView allRecipesPage() {
        ModelAndView mav = new ModelAndView("recipes.html");
        mav.addObject("recipes", recipeService.getAllRecipes());
        return mav;
    }

    @GetMapping("/addOrUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addRecipe(@RequestParam(name = "id", required = false) Integer id) {
        ModelAndView mav = new ModelAndView("addRecipe.html");
        if (id != null) {
            mav.addObject("recipe", recipeService.getOne(id));
        } else {
            mav.addObject("recipe", new Recipe());
        }
        return mav;
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteRecipe(@RequestParam Integer id) {
        recipeService.deleteRecipeFromDb(id);
        return "redirect:/recipe";
    }

    @PostMapping("/addOrUpdate")
   @PreAuthorize("hasRole('ADMIN')")
    String addOrUpdateRecipe(@ModelAttribute @Valid Recipe recipe) {
        if (recipe.getId() == null) {
            recipeService.addRecipeToDb(recipe);
        } else {
            recipeService.updateRecipeInDb(recipe);
        }
        return "redirect:/recipe";
    }
}
