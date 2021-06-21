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
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseRecipeRepository implements RecipeRepository {

    private final JpaRecipeRepository jpaRecipeRepository;
    private final JpaProductRepository jpaProductRepository;

    @Override
    public void addRecipeToDb(Recipe recipe) {
        RecipeEntity recipeEntity = createRecipe(recipe);
        jpaRecipeRepository.save(recipeEntity);
    }

    private RecipeEntity createRecipe(Recipe recipe) {
        return RecipeEntity.builder()
                .name(recipe.getName())
                .description(recipe.getDescription())
                .products(getAllProductsByIdInList(recipe))
                .build();
    }

    @Override
    public void updateRecipeInDb(Recipe recipe) {
        jpaRecipeRepository.findById(recipe.getId())
                .ifPresent(recipeEntity -> {
                    recipeEntity.updateFromDomain(recipe, getAllProductsByIdInList(recipe));
                    jpaRecipeRepository.save(recipeEntity);
                });
    }

    private Set<ProductEntity> getAllProductsByIdInList(Recipe recipe) {
        return jpaProductRepository.findAllProductsByIdInList(recipe.getProductId());
    }

    @Override
    public void deleteRecipeFromDb(int id) {
        jpaRecipeRepository.deleteById(id);
    }

    @Override
    public Optional<Recipe> findRecipeByName(String name) {
        return jpaRecipeRepository.findRecipeByName(name)
                .map(entityToRecipe());
    }

    @Override
    public Optional<Recipe> findRecipeById(int id) {
        return jpaRecipeRepository.findById(id)
                .map(entityToRecipe());
    }

    @Override
    public Set<Recipe> findRecipeByProducts(List<Product> products) {
        Set<ProductEntity> productEntities = products
                .stream()
                .map(productToEntity())
                .collect(Collectors.toSet());

        Set<RecipeEntity> allRecipesByProducts = jpaRecipeRepository.findAllRecipesByProducts(productEntities);

        return allRecipesByProducts
                .stream()
                .map(entityToRecipe())
                .collect(Collectors.toSet());
    }

    private Function<Product, ProductEntity> productToEntity() {
        return p -> ProductEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .build();
    }

    @Override
    public List<Recipe> findAllRecipes() {
        return jpaRecipeRepository.findAll()
                .stream()
                .map(entityToRecipe())
                .collect(Collectors.toList());
    }

    private Function<RecipeEntity, Recipe> entityToRecipe() {
        return recipeEntity -> Recipe.builder()
                .id(recipeEntity.getId())
                .name(recipeEntity.getName())
                .description(recipeEntity.getDescription())
                .productId(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity.getProducts()))
                .build();
    }


}
