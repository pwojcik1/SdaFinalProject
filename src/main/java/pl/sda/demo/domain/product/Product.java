package pl.sda.demo.domain.product;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {
    private Integer id;
    private String name;
}
