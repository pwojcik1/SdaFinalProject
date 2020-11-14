package pl.sda.demo.external.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.recipe.RecipeRepository;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseRecipeRepository implements RecipeRepository {

    private final JpaRecipeRepository jpaRecipeRepository;
    private final JpaProductRepository jpaProductRepository;

    @Override
    public void addRecipeToDb(Recipe recipe) {
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .name(recipe.getName())
                .description(recipe.getDescription())
                .products(jpaProductRepository.findAllProductsByIdInList(recipe.getProductId()))
                .build();
        jpaRecipeRepository.save(recipeEntity);
    }

    @Override
    public void updateRecipeInDb(Recipe recipe) {
        jpaRecipeRepository.findRecipeById(recipe.getId())
                .ifPresent(recipeEntity -> {
                    recipeEntity.updateFromDomain(recipe, jpaProductRepository.findAllProductsByIdInList(recipe.getProductId()));
                    jpaRecipeRepository.save(recipeEntity);
                });
    }

    @Override
    public void deleteRecipeFromDb(int id) {
        jpaRecipeRepository.deleteById(id);
    }

    @Override
    public Optional<Recipe> findByRecipeName(String name) {
        return jpaRecipeRepository.getRecipeByName(name)
                .map(recipeEntity -> Recipe.builder()
                        .id(recipeEntity.getId())
                        .name(recipeEntity.getName())
                        .description(recipeEntity.getDescription())
                        .productId(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity.getProducts()))
                        .build());
    }

    @Override
    public Optional<Recipe> findByRecipeId(int id) {
        return jpaRecipeRepository.findById(id)
                .map(recipeEntity -> Recipe.builder()
                .id(recipeEntity.getId())
                .name(recipeEntity.getName())
                .description(recipeEntity.getDescription())
                .productId(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity.getProducts()))
                .build());
    }

    @Override
    public Set<Recipe> findByProducts(List<Product> products) {
        Set<ProductEntity> productEntities = products.stream().map(p -> ProductEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .build()).collect(Collectors.toSet());
        Set<RecipeEntity> allRecipesByProducts = jpaRecipeRepository.findAllRecipesByProducts(productEntities);
        return allRecipesByProducts
                .stream()
                .map(ent -> Recipe.builder()
                        .id(ent.getId())
                        .name(ent.getName())
                        .description(ent.getDescription())
                        .productId(jpaProductRepository.findAllProductsIdFromCollection(ent.getProducts()))
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return jpaRecipeRepository.findAll().stream().map(recipeEntity -> Recipe.builder()
                .id(recipeEntity.getId())
                .name(recipeEntity.getName())
                .description(recipeEntity.getDescription())
                .productId(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity.getProducts()))
                .build() ).collect(Collectors.toList());
    }
}
