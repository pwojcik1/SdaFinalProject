package pl.sda.demo.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
    private Integer id;
    private String username;
    private String password;
    private List<Integer> productId;
    private List<Integer> recipeId;
}
