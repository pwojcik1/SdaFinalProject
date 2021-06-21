package pl.sda.demo.web.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sda.demo.domain.user.UserService;
import pl.sda.demo.dto.ApiMapService;
import pl.sda.demo.dto.LoginRq;

@RestController
@RequiredArgsConstructor
public class RegisterEndpoint {

    private final UserService userService;
    private final ApiMapService apiMapService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("api/register")
    public ResponseEntity<String> registerUser(@RequestBody LoginRq loginRq) {
        try{
            userService.createUser(apiMapService.convertToUser(loginRq));
            return ResponseEntity.status(201).build();
        }catch (IllegalStateException e){
            return ResponseEntity.status(403).build();
        }
    }

}
