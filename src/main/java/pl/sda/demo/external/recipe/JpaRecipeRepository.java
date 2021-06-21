package pl.sda.demo.external.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.sda.demo.external.product.ProductEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaRecipeRepository extends JpaRepository<RecipeEntity, Integer> {
    @Query("select r from RecipeEntity r where r.id in :ids")
    Set<RecipeEntity> findAllRecipesByIdInCollection(@Param("ids") Collection<Integer> recipeIds);

    @Query("select r.id from RecipeEntity r where r in :recipes")
    List<Integer> findAllRecipesIdFromCollection(@Param("recipes") Collection<RecipeEntity> recipes);

    @Query("select r from RecipeEntity r where r.name =:name")
    Optional<RecipeEntity> findRecipeByName(@Param("name") String name);

    @Query("select r from RecipeEntity r where not exists (" +
            "     select p from RecipeEntity r1 inner join r1.products p " +
            "         where p not in (:products) and r.id = r1.id)")
    Set<RecipeEntity> findAllRecipesByProducts(@Param("products") Set<ProductEntity> products);

    @Query("select u.favourites from UserEntity u where u.username =:username")
    List<RecipeEntity> findAllUserFavourites(@Param("username") String username);
}
