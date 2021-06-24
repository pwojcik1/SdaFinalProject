package pl.sda.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sda.demo.domain.product.Product;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {

    private Integer id;
    private String name;
    private String description;
    private List<Product> products;

}
