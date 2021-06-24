package pl.sda.demo.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Recipe {
    private Integer id;
    private String name;
    private String description;
    private List<Integer> productId;
}
