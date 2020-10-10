package pl.sda.demo.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserService;

@Controller
@RequestMapping("/register")
@Transactional
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @GetMapping
    ModelAndView registerPage() {
        ModelAndView mav = new ModelAndView("register.html");
        mav.addObject("user", new User());
        return mav;
    }
    @PostMapping
    String registerUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/";
    }
}
