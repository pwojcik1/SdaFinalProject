package pl.sda.demo.domain.recipe;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Recipe {
    private Integer id;
    private String name;
    private String description;
    private List<Integer> productId;
}
