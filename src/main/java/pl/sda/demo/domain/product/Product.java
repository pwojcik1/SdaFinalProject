package pl.sda.demo.domain.product;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Product {
    private Integer id;
    private String name;
}
