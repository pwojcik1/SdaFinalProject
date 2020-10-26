package pl.sda.demo.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody LoginRq loginRq) {
        userService.createUser(apiMapService.convertToUser(loginRq));
    }

}
