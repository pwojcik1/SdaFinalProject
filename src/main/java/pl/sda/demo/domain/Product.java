package pl.sda.demo.domain;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Product {

    private Integer id;
    private String name;
    private Integer quantity;

    List<Integer>RecipeId;


}
