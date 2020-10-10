package pl.sda.demo.domain.product;

import lombok.*;

import java.util.List;

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
