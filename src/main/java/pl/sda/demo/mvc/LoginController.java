package pl.sda.demo.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Transactional
public class LoginController {
    @GetMapping("/login")
    String loginPage() {
        return "login.html";
    }
}
