package pl.sda.demo.dto.mvc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeProductDTO {
    private Integer recipeId;
    private String recipeName;
    private String recipeDescription;
    private List<String> productNames;
}
