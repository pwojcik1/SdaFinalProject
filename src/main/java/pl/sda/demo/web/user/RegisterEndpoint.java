package pl.sda.demo.web.user;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.api.ApiMapService;
import pl.sda.demo.dto.api.LoginRq;

@RestController

@RequiredArgsConstructor
public class RegisterEndpoint {

    private final UserService userService;
    private final ApiMapService apiMapService;


    @PostMapping
    @RequestMapping("/api/register")
    public void registerUser(@RequestBody LoginRq loginRq) {
        userService.createUser(apiMapService.convertToUser(loginRq));
    }

}
