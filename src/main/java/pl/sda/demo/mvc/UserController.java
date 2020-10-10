package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.user.UserService;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    @PreAuthorize("isAuthenticated()")
    ModelAndView userPage() {
        ModelAndView mav = new ModelAndView("user.html");
       // mav.addObject("user", userService.getUserById()); //jak wyciagnac id aktualnie zalogowanego usera
        return mav;
    }
}
