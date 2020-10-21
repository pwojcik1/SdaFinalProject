package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.recipe.RecipeService;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.MapService;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final MapService mapService;
    private final UserService userService;
    private final RecipeService recipeService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView favouritesPage() {
        ModelAndView mav = new ModelAndView("search.html");
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        mav.addObject("search", mapService.findByProducts(userService.findByUsername(username).getProductId()));
        return mav;
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    String addNewDoctor(@RequestParam Integer id) {
        User principal = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = principal.getUsername();
        userService.addRecipeToFavourites(recipeService.getOne(id), userService.findByUsername(username));
        return "redirect:/search";
    }
}
