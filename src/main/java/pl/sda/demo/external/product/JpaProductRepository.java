package pl.sda.demo.external.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Integer> {

    @Query("select p from ProductEntity p where p.id in :ids")
    Set<ProductEntity> findAllProductsByIdInList(@Param("ids") List<Integer> productIds);

    @Query("select p.id from ProductEntity p where p in :products")
    List<Integer> findAllProductsIdFromCollection(@Param("products") Set<ProductEntity> products);

    @Query("select p from ProductEntity p where p.name = :name")
    Optional<ProductEntity> findProductByName(@Param("name") String name);

    @Query("select u.products from UserEntity u where u.username = :name")
    List<ProductEntity> findAllProductsFromUserFridge(@Param("name") String name);
}
