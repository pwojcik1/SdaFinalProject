package external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity

@Builder
@NoArgsConstructor

@Getter
@Table(name = "products")
public class ProductEntity {

}
