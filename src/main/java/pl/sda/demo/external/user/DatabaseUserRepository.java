package pl.sda.demo.external.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.domain.user.UserRepository;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DatabaseUserRepository implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaRecipeRepository jpaRecipeRepository;

    @Override
    public void addUser(User user) {
        UserEntity userEntity = createUser(user);
        jpaUserRepository.save(userEntity);
    }

    private UserEntity createUser(User user) {
        return UserEntity.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .favourites(jpaRecipeRepository.findAllRecipesByIdInCollection(user.getRecipeId()))
                .products(jpaProductRepository.findAllProductsByIdInList(user.getProductId()))
                .build();
    }

    @Override
    public void updateUser(User user) {
        jpaUserRepository.findById(user.getId())
                .ifPresent(userEntity -> {
                    userEntity.updateFromDomain(user);
                    jpaUserRepository.save(userEntity);
                });
    }

    @Override
    public void deleteUser(int id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public void addProductToFridge(Product product, User user) {
        ProductEntity productEntity = ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
        Optional<UserEntity> userEntity = jpaUserRepository.findById(user.getId());
        userEntity.ifPresent(e -> {
            e.getProducts().add(productEntity);
            jpaUserRepository.save(e);
        });
    }

    @Override
    public void removeProductFromFridge(int id, User user) {
        Optional<UserEntity> userEntity = jpaUserRepository.findById(user.getId());
        Optional<ProductEntity> productEntity = jpaProductRepository.findById(id);
        userEntity.ifPresent(ent -> {
            if (productEntity.isPresent()) {
                ent.getProducts().remove(productEntity.get());
                jpaUserRepository.save(ent);
            } else {
                throw new IllegalStateException("You cannot remove nonexistent product");
            }
        });
    }

    @Override
    public void addRecipeToFavourites(Recipe recipe, User user) {
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .products(jpaProductRepository.findAllProductsByIdInList(recipe.getProductId()))
                .build();
        Optional<UserEntity> userEntity = jpaUserRepository.findById(user.getId());
        userEntity.ifPresent(ent -> {
            ent.getFavourites().add(recipeEntity);
            jpaUserRepository.save(ent);
        });
    }

    @Override
    public void deleteRecipeFromFavourites(int id, User user) {  //test do tego poprawic
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        Optional<RecipeEntity> recipeEntity = jpaRecipeRepository.findById(id);
        if (recipeEntity.isPresent()) {
            if (userEntity.getFavourites().contains(recipeEntity.get())) {
                userEntity.getFavourites().remove(recipeEntity.get());
                jpaUserRepository.save(userEntity);
            } else {
                throw new IllegalStateException("You dont have this recipe in favourites");
            }
        } else {
            throw new IllegalStateException("This recipe doesnt exists");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findUserByName(username)
                .map(entityToUser());
    }
    private Function<UserEntity, User> entityToUser() {
        return userEntity -> User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .productId(jpaProductRepository.findAllProductsIdFromCollection(userEntity.getProducts()))
                .recipeId(jpaRecipeRepository.findAllRecipesIdFromCollection(userEntity.getFavourites()))
                .build();
    }

    @Override
    public List<Recipe> findAllUserFavourites(String username) {
        return jpaRecipeRepository.findAllUserFavourites(username)
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

    @Override
    public List<Product> findAllProductsFromUserFridge(String username) {
        return jpaProductRepository.findAllProductsFromUserFridge(username)
                .stream()
                .map(entityToProduct())
                .collect(Collectors.toList());
    }

    private Function<ProductEntity, Product> entityToProduct() {
        return productEntity -> Product.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .build();
    }
}