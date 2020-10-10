package pl.sda.demo.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class MainPageController {
    @GetMapping("/")
    String mainPage() {
        return "main.html";
    }
}
