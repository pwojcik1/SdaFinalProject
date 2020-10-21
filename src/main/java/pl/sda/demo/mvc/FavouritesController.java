package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.MapService;

@Controller
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavouritesController {

    private final UserService userService;
    private final MapService mapService;
    private final RecipeService recipeService;


    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView favouritesPage() {
        ModelAndView mav = new ModelAndView("favourites.html");
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        mav.addObject("favourites", mapService.getAllFavouritesWithProducts(username));
        return mav;
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    String addNewDoctor(@RequestParam Integer id) {
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        userService.addRecipeToFavourites(recipeService.getOne(id), userService.findByUsername(username));
        return "redirect:/recipe";
    }


    @GetMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    String deleteRecipe(@RequestParam Integer id) {
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        userService.deleteRecipeFromFavourites(id, userService.findByUsername(username));
        return "redirect:/favourites";
    }

}
