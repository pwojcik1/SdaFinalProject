package pl.sda.demo.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Product {
    private Integer id;
    private String name;
}
