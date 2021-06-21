package pl.sda.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRq {
    private String username;
    private String password;
    private String role;

    public LoginRq(String username, String password) {
        this.password = password;
        this.username = username;
        this.role = null;
    }
}
