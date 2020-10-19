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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DatabaseUserRepository implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaRecipeRepository jpaRecipeRepository;

    @Override
    public void createUser(User user) {
        UserEntity userEntity = UserEntity.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .favourites(jpaRecipeRepository.findAllRecipesByIdInCollection(user.getRecipeId()))
                .products(jpaProductRepository.findAllProductsByIdInList(user.getProductId()))
                .build();
        jpaUserRepository.save(userEntity);
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
        Optional<ProductEntity> productEntity = jpaProductRepository.findProductById(id);
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
        Optional<RecipeEntity> recipeEntity = jpaRecipeRepository.findRecipeById(id);
        if (recipeEntity.isPresent()) {
            if (userEntity.getFavourites().contains(recipeEntity.get())) {
                userEntity.getFavourites().remove(recipeEntity.get());
                jpaUserRepository.save(userEntity);
            }else{
                throw new IllegalStateException("You dont have this recipe in favourites");
            }
        } else {
            throw new IllegalStateException("This recipe doesnt exists");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.getUserByName(username)
                .map(ent -> User.builder()
                        .id(ent.getId())
                        .username(ent.getUsername())
                        .password(ent.getPassword())
                        .role(ent.getRole())
                        .productId(jpaProductRepository.findAllProductsIdFromCollection(ent.getProducts()))
                        .recipeId(jpaRecipeRepository.findAllRecipesIdFromCollection(ent.getFavourites()))
                        .build());
    }

    @Override
    public Optional<Product> getProductFromFridgeByName(String name, User user) {
        UserEntity userEntity = jpaUserRepository.getOne(user.getId());
        Optional<ProductEntity> productEntity = jpaProductRepository.getProductByName(name);
        if (productEntity.isPresent()) {
            if (userEntity.getProducts().contains(productEntity.get())) {
                return productEntity.map(ent -> Product.builder()
                        .id(ent.getId())
                        .name(ent.getName())
                        .build());
            }
            throw new IllegalStateException("You dont have this product in fridge");
        }
        throw new IllegalStateException("Product with given name doesnt exist");
    }

    @Override
    public List<Product> getAllProductsFromFridge(String username) {
        return jpaProductRepository.getAllProductsFromFridge(username)
                .stream()
                .map(ent -> new Product(ent.getId(), ent.getName()))
                .collect(Collectors.toList());
    }
}