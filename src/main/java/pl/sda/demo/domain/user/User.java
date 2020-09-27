package pl.sda.demo.domain.user;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
    private Integer id;
    private String login;
    private String password;
    private List<Integer> productId;
    private List<Integer> recipeId;
}
