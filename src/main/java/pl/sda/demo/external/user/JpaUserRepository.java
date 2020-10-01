package pl.sda.demo.external.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("select r from RecipeEntity r where r.id in :ids")
    Set<RecipeEntity> findAllRecipesByIdInList(@Param("ids") List<Integer> recipeIds);

    @Query("select p from ProductEntity p where p.id in :ids")
    Set<ProductEntity> findAllProductsByIdInList(@Param("ids") List<Integer> productIds);

    Optional<UserEntity> findByUsername(String username);

    @Query("select p.id from ProductEntity p where p in :products")
    List<Integer> findAllProductsIdFromCollection(@Param("products") Set<ProductEntity> products);

    @Query("select r.id from RecipeEntity r where r in :recipes")
    List<Integer> findAllRecipesIdFromCollection(@Param("recipes") Set<RecipeEntity> recipes);

    @Query("select p from ProductEntity p where p.id =:productId")
    Optional<ProductEntity> findProductById(@Param("productId") Integer productId);

    @Query("select r from RecipeEntity r where r.id =:recipeId")
    Optional<RecipeEntity> findRecipeById(@Param("recipeId") Integer recipeId);

    @Query("select p from ProductEntity p where p.name =:name")
    Optional<ProductEntity> getProductByName(@Param("name") String name);
}
