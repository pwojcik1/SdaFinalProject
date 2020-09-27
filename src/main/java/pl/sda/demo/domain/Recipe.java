package pl.sda.demo.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Recipe {
    private int id;
    private String name;
    private String description;
    private List<Integer> productId;

}
